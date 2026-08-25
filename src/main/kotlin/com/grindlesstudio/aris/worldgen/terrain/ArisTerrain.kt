package com.grindlesstudio.aris.worldgen.terrain

import kotlin.math.roundToInt

object ArisTerrain {

    // Дизайн Aris: именно эти значения определяют диапазон нашего terrain.
    const val MIN_HEIGHT = -350
    const val MAX_HEIGHT = 650
    const val SEA_LEVEL = 0

    /**
     * Обычный terrain Aris.
     *
     * Здесь ничего специально для Plains нет.
     * Этот метод продолжает генерировать:
     *
     * - материки
     * - океаны
     * - впадины
     * - горы
     * - хребты
     * - пики
     * - мелкий рельеф
     */
    fun getHeight(x: Int, z: Int, seed: Long): Int {

        val px = x.toDouble()
        val pz = z.toDouble()

        // ========================================================
        // 1. КРУПНАЯ ГЕОГРАФИЯ
        // ========================================================

        val continentalness = ArisNoise.fractal2D(
            px,
            pz,
            scale = 5000.0,
            seed = seed,
            octaves = 4,
            persistence = 0.5,
            lacunarity = 2.0
        )

        val landAmount = smoothstep(
            0.02,
            0.75,
            continentalness
        )

        val landHeight =
            8.0 + landAmount * 165.0

        // ========================================================
        // 2. ОКЕАН
        // ========================================================

        val oceanAmount = smoothstep(
            -0.90,
            -0.02,
            -continentalness
        )

        val oceanDepth =
            oceanAmount * 150.0

        val trenchNoise = ArisNoise.fractal2D(
            px,
            pz,
            scale = 1800.0,
            seed = seed + 40_001L,
            octaves = 3,
            persistence = 0.5
        )

        val trenchAmount =
            smoothstep(
                0.58,
                0.92,
                trenchNoise
            ) * oceanAmount

        val trenchDepth =
            trenchAmount * 220.0

        // ========================================================
        // 3. ГОРНЫЕ СИСТЕМЫ
        // ========================================================

        val mountainRegion = ArisNoise.fractal2D(
            px,
            pz,
            scale = 3200.0,
            seed = seed + 10_001L,
            octaves = 3,
            persistence = 0.5,
            lacunarity = 2.0
        )

        val mountainMask = smoothstep(
            0.10,
            0.60,
            mountainRegion
        )

        val ridgeNoise = ArisNoise.fractal2D(
            px,
            pz,
            scale = 1200.0,
            seed = seed + 20_001L,
            octaves = 3,
            persistence = 0.5,
            lacunarity = 2.0
        )

        val ridgeBase =
            (ridgeNoise + 1.0) * 0.5

        val ridgeShape =
            ridgeBase * ridgeBase

        val peakNoise = ArisNoise.fractal2D(
            px,
            pz,
            scale = 350.0,
            seed = seed + 30_001L,
            octaves = 3,
            persistence = 0.55,
            lacunarity = 2.0
        )

        val peakBase =
            (peakNoise + 1.0) * 0.5

        val peakShape =
            smoothstep(
                0.55,
                0.90,
                peakBase
            )

        val mountainShape =
            mountainMask *
                    (
                            ridgeShape * 0.75 +
                                    peakShape * 0.25
                            )

        val mountainProfile =
            mountainShape * mountainShape

        val mountainHeight =
            mountainProfile * 450.0

        // ========================================================
        // 4. МЕЛКИЙ РЕЛЬЕФ
        // ========================================================

        val detail = ArisNoise.fractal2D(
            px,
            pz,
            scale = 250.0,
            seed = seed + 20_001L,
            octaves = 3,
            persistence = 0.45,
            lacunarity = 2.0
        )

        val detailHeight =
            detail * 14.0

        // ========================================================
        // 5. ФИНАЛЬНЫЙ TERRAIN
        // ========================================================

        val mountainOnLand =
            mountainHeight * landAmount

        var height =
            landHeight * landAmount -
                    oceanDepth -
                    trenchDepth +
                    mountainOnLand +
                    detailHeight

        height = height.coerceIn(
            MIN_HEIGHT.toDouble(),
            MAX_HEIGHT.toDouble()
        )

        return height.roundToInt()
    }

    /**
     * Terrain для minecraft:plains.
     *
     * ВАЖНО:
     *
     * Здесь мы специально НЕ используем:
     *
     * - mountainRegion
     * - ridgeNoise
     * - peakNoise
     * - detailHeight
     *
     * Поэтому горы внутри Plains исчезают.
     *
     * При этом высота всё ещё зависит от continentalness,
     * поэтому Plains не находятся на одном фиксированном Y.
     */
    fun getPlainsHeight(
        x: Int,
        z: Int,
        seed: Long
    ): Int {

        val px = x.toDouble()
        val pz = z.toDouble()

        // ========================================================
        // КРУПНАЯ ГЕОГРАФИЯ
        // ========================================================

        val continentalness = ArisNoise.fractal2D(
            px,
            pz,
            scale = 5000.0,
            seed = seed,
            octaves = 4,
            persistence = 0.5,
            lacunarity = 2.0
        )

        /*
         * Определяем, насколько это суша.
         *
         * Чем больше continentalness,
         * тем выше Plains.
         */
        val landAmount = smoothstep(
            0.02,
            0.75,
            continentalness
        )

        /*
         * Базовая высота суши.
         *
         * От примерно Y=8
         * до примерно Y=173.
         *
         * Здесь НЕТ mountainHeight.
         */
        val landHeight =
            8.0 + landAmount * 165.0

        // ========================================================
        // НЕБОЛЬШОЙ ЕСТЕСТВЕННЫЙ РЕЛЬЕФ
        // ========================================================

        /*
         * Это отдельный очень слабый noise.
         *
         * Он нужен только для того, чтобы Plains
         * не выглядели как идеально ровная плита.
         *
         * Амплитуда ±2 блока.
         */
        val plainsDetail = ArisNoise.fractal2D(
            px,
            pz,
            scale = 500.0,
            seed = seed + 50_001L,
            octaves = 2,
            persistence = 0.5,
            lacunarity = 2.0
        )

        val detailHeight =
            plainsDetail * 2.0

        /*
         * Для суши используем landHeight.
         *
         * Если по какой-то причине Plains окажется
         * в области ниже уровня моря, не даём ей
         * стать океанским дном.
         */
        val baseHeight =
            if (landAmount > 0.0) {
                landHeight
            } else {
                SEA_LEVEL + 1.0
            }

        var height =
            baseHeight + detailHeight

        /*
         * Plains не должна уходить ниже уровня моря.
         */
        height = height.coerceAtLeast(
            SEA_LEVEL.toDouble() + 1.0
        )

        /*
         * И всё равно соблюдаем общий диапазон Aris.
         */
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

        val t =
            (value - start) / (end - start)

        return t * t * (3.0 - 2.0 * t)
    }
}