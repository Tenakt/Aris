package com.grindlesstudio.aris.worldgen.terrain

import java.util.Random
import kotlin.math.abs
import kotlin.math.floor

/**
 * Базовые noise-функции Aris.
 *
 * Пока это собственный лёгкий value noise, но уже с поддержкой
 * нескольких октав. Это даст крупный рельеф + более мелкие детали,
 * не превращая terrain в набор резких ступенек.
 */
object ArisNoise {

    /**
     * Fractal/value noise примерно в диапазоне [-1; 1].
     */
    fun fractal2D(
        x: Double,
        z: Double,
        scale: Double,
        seed: Long,
        octaves: Int = 4,
        persistence: Double = 0.5,
        lacunarity: Double = 2.0
    ): Double {
        var frequency = 1.0
        var amplitude = 1.0
        var value = 0.0
        var amplitudeSum = 0.0

        repeat(octaves) { octave ->
            value += noise2D(
                x,
                z,
                scale / frequency,
                seed + octave * 9_973L
            ) * amplitude

            amplitudeSum += amplitude
            amplitude *= persistence
            frequency *= lacunarity
        }

        return (value / amplitudeSum).coerceIn(-1.0, 1.0)
    }

    /**
     * Пространственно непрерывный 2D value noise.
     */
    fun noise2D(x: Double, z: Double, scale: Double, seed: Long): Double {
        val nx = x / scale
        val nz = z / scale

        val x0 = floor(nx).toInt()
        val z0 = floor(nz).toInt()
        val x1 = x0 + 1
        val z1 = z0 + 1

        val tx = smooth(nx - x0)
        val tz = smooth(nz - z0)

        val v00 = randomValue(x0, z0, seed)
        val v10 = randomValue(x1, z0, seed)
        val v01 = randomValue(x0, z1, seed)
        val v11 = randomValue(x1, z1, seed)

        val a = lerp(v00, v10, tx)
        val b = lerp(v01, v11, tx)

        return lerp(a, b, tz)
    }

    /**
     * Ridge noise: 0 = слабая горная зона, 1 = выраженный хребет.
     */
    fun ridged2D(
        x: Double,
        z: Double,
        scale: Double,
        seed: Long
    ): Double {
        val value = fractal2D(
            x,
            z,
            scale,
            seed,
            octaves = 4,
            persistence = 0.5
        )

        return (1.0 - abs(value)).coerceIn(0.0, 1.0)
    }

    private fun randomValue(x: Int, z: Int, seed: Long): Double {
        var value = seed
        value = value xor (x.toLong() * 341_873_128_712L)
        value = value xor (z.toLong() * 132_897_987_541L)
        value = value xor (value ushr 27)
        value *= -7046029254386353131L
        value = value xor (value ushr 31)

        val random = Random(value)
        return random.nextDouble() * 2.0 - 1.0
    }

    private fun lerp(a: Double, b: Double, t: Double): Double {
        return a + (b - a) * t
    }

    private fun smooth(t: Double): Double {
        return t * t * (3.0 - 2.0 * t)
    }
}
