package com.grindlesstudio.aris.worldgen.biome

import com.grindlesstudio.aris.worldgen.terrain.ArisRegion
import kotlin.math.floor

object ArisBiomeRegion {

    fun getRegion(x: Int, z: Int, seed: Long): ArisRegion {
        // Размер крупных регионов.
        // 1200 = примерно область 1200×1200 блоков.
        val scale = 1200.0

        val nx = x / scale
        val nz = z / scale

        // Основной крупный шум.
        val regionNoise = smoothNoise(nx, nz, seed)

        // Дополнительный шум для разнообразия.
        val detailNoise = smoothNoise(
            nx * 2.5,
            nz * 2.5,
            seed + 1000
        )

        /*
         * Сначала определяем редкие особые регионы.
         */

        // Холмы — небольшие/средние участки.
        if (regionNoise > 0.55 && detailNoise > 0.0) {
            return ArisRegion.HILLS
        }

        // Пустыня — большие сухие регионы.
        if (regionNoise < -0.35) {
            return ArisRegion.DESERT
        }

        // Тайга — отдельные крупные регионы.
        if (regionNoise > 0.15 && detailNoise < -0.15) {
            return ArisRegion.TAIGA
        }

        // Всё остальное — равнины.
        return ArisRegion.PLAINS
    }

    /**
     * Плавный псевдослучайный 2D noise.
     *
     * Возвращает значение примерно от -1.0 до 1.0.
     */
    private fun smoothNoise(
        x: Double,
        z: Double,
        seed: Long
    ): Double {
        val x0 = floor(x).toInt()
        val z0 = floor(z).toInt()

        val x1 = x0 + 1
        val z1 = z0 + 1

        val tx = x - x0
        val tz = z - z0

        // Плавное сглаживание.
        val sx = smoothStep(tx)
        val sz = smoothStep(tz)

        val n00 = random(x0, z0, seed)
        val n10 = random(x1, z0, seed)
        val n01 = random(x0, z1, seed)
        val n11 = random(x1, z1, seed)

        val nx0 = lerp(n00, n10, sx)
        val nx1 = lerp(n01, n11, sx)

        return lerp(nx0, nx1, sz)
    }

    private fun random(
        x: Int,
        z: Int,
        seed: Long
    ): Double {
        var value = seed

        value += x.toLong() * 341873128712L
        value += z.toLong() * 132897987541L

        value = value xor (value shr 13)
        value *= 127412617629L
        value = value xor (value shr 16)

        return (value and 0x7FFFFFFF) / 1073741824.0 - 1.0
    }

    private fun smoothStep(t: Double): Double {
        return t * t * (3.0 - 2.0 * t)
    }

    private fun lerp(
        a: Double,
        b: Double,
        t: Double
    ): Double {
        return a + (b - a) * t
    }
}