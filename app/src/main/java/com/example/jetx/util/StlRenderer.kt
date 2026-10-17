package com.example.jetx.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*

class StlRenderer(context: Context, resId: Int) {

    private val vertices: FloatArray
    private val triangleCount: Int

    init {
        var vTmp = FloatArray(0)
        var countTmp = 0

        try {
            val stream: InputStream = context.resources.openRawResource(resId)
            val bytes = stream.readBytes()
            stream.close()

            if (bytes.size > 84) {
                val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
                buffer.position(80)
                val count = buffer.int

                // Muestreo optimizado para renderizado ultra suave y nítido
                val step = if (count > 25000) (count / 15000).coerceAtLeast(1) else 1
                val targetCount = count / step

                val vList = FloatArray(targetCount * 9)
                var vIdx = 0

                var minX = Float.MAX_VALUE; var maxX = -Float.MAX_VALUE
                var minY = Float.MAX_VALUE; var maxY = -Float.MAX_VALUE
                var minZ = Float.MAX_VALUE; var maxZ = -Float.MAX_VALUE

                for (i in 0 until count) {
                    if (buffer.remaining() < 50) break
                    // Ignoramos la normal almacenada del archivo y la calcularemos geométricamente
                    buffer.float; buffer.float; buffer.float 
                    val x1 = buffer.float; val y1 = buffer.float; val z1 = buffer.float
                    val x2 = buffer.float; val y2 = buffer.float; val z2 = buffer.float
                    val x3 = buffer.float; val y3 = buffer.float; val z3 = buffer.float
                    buffer.short // attribute

                    if (i % step == 0 && vIdx + 8 < vList.size) {
                        vList[vIdx++] = x1; vList[vIdx++] = y1; vList[vIdx++] = z1
                        vList[vIdx++] = x2; vList[vIdx++] = y2; vList[vIdx++] = z2
                        vList[vIdx++] = x3; vList[vIdx++] = y3; vList[vIdx++] = z3

                        minX = minOf(minX, x1, x2, x3); maxX = maxOf(maxX, x1, x2, x3)
                        minY = minOf(minY, y1, y2, y3); maxY = maxOf(maxY, y1, y2, y3)
                        minZ = minOf(minZ, z1, z2, z3); maxZ = maxOf(maxZ, z1, z2, z3)
                    }
                }

                val cx = (minX + maxX) / 2f
                val cy = (minY + maxY) / 2f
                val cz = (minZ + maxZ) / 2f
                val maxExtent = maxOf(maxX - minX, maxY - minY, maxZ - minZ).coerceAtLeast(0.001f)

                for (j in 0 until vIdx step 3) {
                    vList[j] = (vList[j] - cx) / maxExtent
                    vList[j + 1] = (vList[j + 1] - cy) / maxExtent
                    vList[j + 2] = (vList[j + 2] - cz) / maxExtent
                }

                vTmp = vList.copyOf(vIdx)
                countTmp = vIdx / 9
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        vertices = vTmp
        triangleCount = countTmp
    }

    private class TransformedTri(
        val zAvg: Float,
        val path: Path,
        val color: Int
    )

    fun renderToBitmap(
        size: Int = 200,
        yawDeg: Float = 0f,
        yawOffsetDeg: Float = 0f,
        pitchDeg: Float = 30f
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        if (triangleCount == 0) return bitmap

        val canvas = Canvas(bitmap)
        val center = size / 2f
        val scale = size * 0.44f

        val effectiveYaw = (yawDeg + yawOffsetDeg)
        val yaw = Math.toRadians(effectiveYaw.toDouble()).toFloat()
        val pitch = Math.toRadians(pitchDeg.toDouble()).toFloat()

        val cosY = cos(yaw); val sinY = sin(yaw)
        val cosP = cos(pitch); val sinP = sin(pitch)

        // Dirección de la fuente de luz 3D (iluminación cenital brillante)
        val lx = 0.4f; val ly = 0.9f; val lz = 0.4f
        val lLen = sqrt(lx * lx + ly * ly + lz * lz)
        val nLx = lx / lLen; val nLy = ly / lLen; val nLz = lz / lLen

        val triList = ArrayList<TransformedTri>(triangleCount)
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 0.8f // Elimina los artefactos de costuras/malla (efecto alambre)
            strokeJoin = Paint.Join.ROUND
        }

        for (i in 0 until triangleCount) {
            val vBase = i * 9

            val x1 = vertices[vBase]; val y1 = vertices[vBase + 1]; val z1 = vertices[vBase + 2]
            val x2 = vertices[vBase + 3]; val y2 = vertices[vBase + 4]; val z2 = vertices[vBase + 5]
            val x3 = vertices[vBase + 6]; val y3 = vertices[vBase + 7]; val z3 = vertices[vBase + 8]

            // Transformar los 3 vértices
            val rx1 = x1 * cosY + z1 * sinY
            val ry1 = y1 * cosP - (-x1 * sinY + z1 * cosY) * sinP
            val rz1 = y1 * sinP + (-x1 * sinY + z1 * cosY) * cosP

            val rx2 = x2 * cosY + z2 * sinY
            val ry2 = y2 * cosP - (-x2 * sinY + z2 * cosY) * sinP
            val rz2 = y2 * sinP + (-x2 * sinY + z2 * cosY) * cosP

            val rx3 = x3 * cosY + z3 * sinY
            val ry3 = y3 * cosP - (-x3 * sinY + z3 * cosY) * sinP
            val rz3 = y3 * sinP + (-x3 * sinY + z3 * cosY) * cosP

            // Calcular el vector normal real del triángulo en 3D
            val ax = rx2 - rx1; val ay = ry2 - ry1; val az = rz2 - rz1
            val bx = rx3 - rx1; val by = ry3 - ry1; val bz = rz3 - rz1

            val nx = ay * bz - az * by
            val ny = az * bx - ax * bz
            val nz = ax * by - ay * bx
            val nLen = sqrt(nx * nx + ny * ny + nz * nz).coerceAtLeast(0.0001f)

            val fnx = nx / nLen; val fny = ny / nLen; val fnz = nz / nLen

            // Iluminación Lambertiana directa
            val dot = (fnx * nLx + fny * nLy + fnz * nLz).coerceIn(0f, 1f)
            val lightIntensity = (0.45f + 0.55f * dot).coerceIn(0.35f, 1.0f)

            // Acabado de pintura opaca brillante metálica VIP Blanco/Cian
            val r = (180 + (255 - 180) * lightIntensity).toInt().coerceIn(0, 255)
            val g = (220 + (255 - 220) * lightIntensity).toInt().coerceIn(0, 255)
            val b = (245 + (255 - 245) * lightIntensity).toInt().coerceIn(0, 255)
            val triColor = Color.rgb(r, g, b)

            val path = Path()
            path.moveTo(center + rx1 * scale, center - ry1 * scale)
            path.lineTo(center + rx2 * scale, center - ry2 * scale)
            path.lineTo(center + rx3 * scale, center - ry3 * scale)
            path.close()

            val zAvg = (rz1 + rz2 + rz3) / 3f
            triList.add(TransformedTri(zAvg, path, triColor))
        }

        // Ordenamiento Z (Algoritmo de Pintor para oclusión 3D real)
        triList.sortBy { it.zAvg }

        for (tri in triList) {
            fillPaint.color = tri.color
            canvas.drawPath(tri.path, fillPaint)
        }

        return bitmap
    }
}