package com.grindlesstudio.aris.worldgen.terrain

import java.util.Random
import kotlin.math.floor

object ArisNoise {

    /**
     * Простейший 2D value noise.
     *
     * Возвращает значение примерно от -1.0 до 1.0.
     *
     * ВАЖНО:
     * Это пока не финальный noise Aris.
     * Мы используем его для первого прототипа,
     * чтобы проверить саму систему terrain.
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

    private fun randomValue(x: Int, z: Int, seed: Long): Double {
        var value = seed

        value = value xor (x.toLong() * 341_873_128_712L)
        value = value xor (z.toLong() * 132_897_987_541L)

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