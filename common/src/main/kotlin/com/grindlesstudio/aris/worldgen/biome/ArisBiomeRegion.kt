package com.grindlesstudio.aris.worldgen.biome

import com.grindlesstudio.aris.worldgen.terrain.ArisRegion
import kotlin.math.floor
import kotlin.math.sin

object ArisBiomeRegion {

    fun getRegion(x: Int, z: Int, seed: Long): ArisRegion {

        // Очень крупные регионы
        val scale = 3000.0

        val nx = (x + seed * 0.00001) / scale
        val nz = (z - seed * 0.00001) / scale

        val noise =
            sin(nx * 6.283185307179586) *
                    sin(nz * 6.283185307179586)

        // Океан
        if (noise < -0.35) {
            return ArisRegion.OCEAN
        }

        // Горы
        if (noise > 0.55) {
            return ArisRegion.MOUNTAINS
        }

        // Таёжные области
        val taigaNoise =
            sin((nx + 1.7) * 3.141592653589793) *
                    sin((nz - 2.3) * 3.141592653589793)

        if (taigaNoise > 0.35) {
            return ArisRegion.TAIGA
        }

        return ArisRegion.PLAINS
    }
}