package com.grindlesstudio.aris.worldgen.terrain

import kotlin.math.roundToInt

object ArisTerrain {

    // Дизайн Aris: именно эти значения определяют диапазон нашего terrain.
    const val MIN_HEIGHT = -350
    const val MAX_HEIGHT = 650
    const val SEA_LEVEL = 0

    /**
     * Первый настоящий прототип Aris Terrain v0.1.
     *
     * Пока здесь только крупная география:
     * континенты -> океаны -> равнины -> горные зоны -> детали.
     * Реки, биомы и erosion добавим отдельными слоями позже.
     */
    fun getHeight(x: Int, z: Int, seed: Long): Int {
        val px = x.toDouble()
        val pz = z.toDouble()

        // 1. Очень крупная география материков.
        // 5000 блоков оставляем как базовый масштаб первого прототипа.
        val continentalness = ArisNoise.fractal2D(
            px,
            pz,
            scale = 5000.0,
            seed = seed,
            octaves = 4,
            persistence = 0.5,
            lacunarity = 2.0
        )

        // 2. Крупная форма суши. Положительная часть континентального
        // значения постепенно поднимает terrain выше уровня моря.
        val landAmount = smoothstep(
            0.02,
            0.75,
            continentalness
        )

        // Базовая высота суши: от почти береговой до высоких внутренних районов.
        val landHeight = 8.0 + landAmount * 165.0

        // 3. Океан. Чем сильнее отрицательная continentalness,
        // тем глубже вода. Основной диапазон у берега постепенно уходит
        // к большим глубинам.
        val oceanAmount = smoothstep(
            -0.90,
            -0.02,
            -continentalness
        )

        val oceanDepth = oceanAmount * 150.0

        // Редкие глубокие впадины. Они не делают весь океан глубоким,
        // но позволяют отдельным районам уходить значительно ниже.
        val trenchNoise = ArisNoise.fractal2D(
            px,
            pz,
            scale = 1800.0,
            seed = seed + 40_001L,
            octaves = 3,
            persistence = 0.5
        )

        val trenchAmount = smoothstep(
            0.58,
            0.92,
            trenchNoise
        ) * oceanAmount

        val trenchDepth = trenchAmount * 220.0

        // 4. Горные системы.
        // Отдельный крупный noise формирует зоны, где вообще могут
        // появляться горные цепи.
        val mountainNoise = ArisNoise.fractal2D(
            px,
            pz,
            scale = 1800.0,
            seed = seed + 10_001L,
            octaves = 4,
            persistence = 0.5,
            lacunarity = 2.0
        )

        // Чем выше mountainNoise, тем сильнее горная зона.
        // Квадратичная форма оставляет большую часть мира низкой,
        // а сильные значения превращает в настоящие горные массивы.
        val mountainAmount = smoothstep(
            0.05,
            0.75,
            mountainNoise
        )
        val mountainHeight = mountainAmount * mountainAmount * 500.0

        // 5. Более мелкий рельеф. Он только слегка меняет поверхность,
        // чтобы не разрушать крупную географическую форму.
        val detail = ArisNoise.fractal2D(
            px,
            pz,
            scale = 250.0,
            seed = seed + 20_001L,
            octaves = 3,
            persistence = 0.45,
            lacunarity = 2.0
        )

        val detailHeight = detail * 14.0

        // Горные системы должны появляться на суше, а не посреди океана.
        val mountainOnLand = mountainHeight * landAmount

        // Собираем финальную высоту.
        var height =
            landHeight * landAmount -
            oceanDepth -
            trenchDepth +
            mountainOnLand +
            detailHeight

        // Наш terrain всегда ограничен именно -350..650.
        height = height.coerceIn(
            MIN_HEIGHT.toDouble(),
            MAX_HEIGHT.toDouble()
        )

        return height.roundToInt()
    }

    /**
     * Плавная интерполяция 0..1 между start и end.
     */
    private fun smoothstep(
        start: Double,
        end: Double,
        value: Double
    ): Double {
        if (value <= start) return 0.0
        if (value >= end) return 1.0

        val t = (value - start) / (end - start)
        return t * t * (3.0 - 2.0 * t)
    }
}
