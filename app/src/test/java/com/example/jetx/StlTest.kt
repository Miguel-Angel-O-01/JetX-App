package com.example.jetx

import java.io.File
import org.junit.Test
import org.junit.Assert.*

class StlTest {
    @Test
    fun testStlParsing() {
        val file = File("src/main/res/raw/jetx3d.stl")
        assertTrue("File should exist in res/raw", file.exists())
    }
}