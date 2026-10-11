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
    private val normals: FloatArray
    private val triangleCount: Int

    init {
        var vTmp = FloatArray(0)
        var nTmp = FloatArray(0)
        var countTmp = 0

        try {
            val stream: InputStream = context.resources.openRawResource(resId)
            val bytes = stream.readBytes()
            stream.close()

            if (bytes.size > 84) {
                val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
                buffer.position(80)
                val count = buffer.int

                // Decimate factor if model is dense to keep rendering ultra smooth
                val step = if (count > 25000) (count / 15000).coerceAtLeast(1) else 1
                val targetCount = count / step

                val vList = FloatArray(targetCount * 9)
                val nList = FloatArray(targetCount * 3)

                var vIdx = 0
                var nIdx = 0

                var minX = Float.MAX_VALUE; var maxX = -Float.MAX_VALUE
                var minY = Float.MAX_VALUE; var maxY = -Float.MAX_VALUE
                var minZ = Float.MAX_VALUE; var maxZ = -Float.MAX_VALUE

                for (i in 0 until count) {
                    if (buffer.remaining() < 50) break
                    val nx = buffer.float; val ny = buffer.float; val nz = buffer.float
                    val x1 = buffer.float; val y1 = buffer.float; val z1 = buffer.float
                    val x2 = buffer.float; val y2 = buffer.float; val z2 = buffer.float
                    val x3 = buffer.float; val y3 = buffer.float; val z3 = buffer.float
                    buffer.short // attribute

                    if (i % step == 0 && vIdx + 8 < vList.size) {
                        nList[nIdx++] = nx; nList[nIdx++] = ny; nList[nIdx++] = nz

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
                nTmp = nList.copyOf(nIdx)
                countTmp = vIdx / 9
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        vertices = vTmp
        normals = nTmp
        triangleCount = countTmp
    }

    private class TransformedTri(
        val zAvg: Float,
        val path: Path,
        val color: Int
    )

    fun renderToBitmap(size: Int = 180, yawDeg: Float = 0f, pitchDeg: Float = 25f): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        if (triangleCount == 0) return bitmap

        val canvas = Canvas(bitmap)
        val center = size / 2f
        val scale = size * 0.44f

        val yaw = Math.toRadians(yawDeg.toDouble()).toFloat()
        val pitch = Math.toRadians(pitchDeg.toDouble()).toFloat()

        val cosY = cos(yaw); val sinY = sin(yaw)
        val cosP = cos(pitch); val sinP = sin(pitch)

        // Light direction
        val lx = 0.5f; val ly = 0.8f; val lz = 0.4f
        val lLen = sqrt(lx * lx + ly * ly + lz * lz)
        val nLx = lx / lLen; val nLy = ly / lLen; val nLz = lz / lLen

        val triList = ArrayList<TransformedTri>(triangleCount)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }

        for (i in 0 until triangleCount) {
            val vBase = i * 9
            val nBase = i * 3

            val nx = normals[nBase]; val ny = normals[nBase + 1]; val nz = normals[nBase + 2]
            val rNx = nx * cosY + nz * sinY
            val rNy = ny * cosP - (-nx * sinY + nz * cosY) * sinP
            val rNz = ny * sinP + (-nx * sinY + nz * cosY) * cosP

            val dot = (rNx * nLx + rNy * nLy + rNz * nLz).coerceIn(0f, 1f)
            val lightIntensity = (0.45f + 0.55f * dot).coerceIn(0.35f, 1.0f)

            // High contrast VIP Metallic White & Sky Cyan shading
            val r = (180 + (255 - 180) * lightIntensity).toInt().coerceIn(0, 255)
            val g = (220 + (255 - 220) * lightIntensity).toInt().coerceIn(0, 255)
            val b = (240 + (255 - 240) * lightIntensity).toInt().coerceIn(0, 255)
            val triColor = Color.rgb(r, g, b)

            val path = Path()
            var zSum = 0f

            for (v in 0 until 3) {
                val ox = vertices[vBase + v * 3]
                val oy = vertices[vBase + v * 3 + 1]
                val oz = vertices[vBase + v * 3 + 2]

                val rx = ox * cosY + oz * sinY
                val ry = oy * cosP - (-ox * sinY + oz * cosY) * sinP
                val rz = oy * sinP + (-ox * sinY + oz * cosY) * cosP

                zSum += rz

                val px = center + rx * scale
                val py = center - ry * scale

                if (v == 0) path.moveTo(px, py) else path.lineTo(px, py)
            }
            path.close()

            triList.add(TransformedTri(zSum / 3f, path, triColor))
        }

        triList.sortBy { it.zAvg }

        for (tri in triList) {
            paint.color = tri.color
            canvas.drawPath(tri.path, paint)
        }

        return bitmap
    }
}