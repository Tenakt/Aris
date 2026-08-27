package com.grindlesstudio.aris.worldgen.terrain

import kotlin.math.roundToInt

object ArisTerrain {

    // ============================================================
    // ARIS WORLD SETTINGS
    // ============================================================

    const val MIN_HEIGHT = -350
    const val MAX_HEIGHT = 650

    /**
     * Реальный уровень моря.
     *
     * Поверхность воды находится ровно на Y=0.
     * Поэтому блоки воды находятся на Y=-1 и ниже.
     */
    const val SEA_LEVEL = 0


    // ============================================================
    // MAIN TERRAIN
    // ============================================================

    fun getHeight(
        x: Int,
        z: Int,
        seed: Long
    ): Int {

        val px = x.toDouble()
        val pz = z.toDouble()


        // ========================================================
        // 1. CONTINENTALNESS
        // ========================================================

        /**
         * Очень крупный noise.
         *
         * Именно он определяет:
         *
         * - материк
         * - океан
         * - острова
         * - границу суши и океана
         *
         * 5000 блоков = огромные географические регионы.
         */

        val continentalness = ArisNoise.fractal2D(
            px,
            pz,
            scale = 3000.0,
            seed = seed,
            octaves = 4,
            persistence = 0.5,
            lacunarity = 2.0
        )


        // ========================================================
        // 2. LAND MASK
        // ========================================================

        /**
         * Определяем, насколько точка относится к суше.
         *
         * 0.0 = океан
         * 1.0 = глубокая часть материка
         *
         * В районе перехода получаем берег.
         */

        val landAmount = smoothstep(
            -0.05,
            0.45,
            continentalness
        )


        // ========================================================
        // 3. OCEAN DEPTH
        // ========================================================

        /**
         * Отдельно рассчитываем глубину океана.
         *
         * Важно:
         *
         * океан НЕ пытается получить высоту около Y=60.
         *
         * Его высота всегда отрицательная.
         *
         * Например:
         *
         * берег:
         *     Y=-1
         *
         * мелководье:
         *     Y=-10
         *
         * океан:
         *     Y=-80
         *
         * глубокий океан:
         *     Y=-180
         *
         * впадина:
         *     Y=-350
         */

        val oceanMask =
            1.0 - landAmount


        /**
         * Базовая глубина океана.
         *
         * Максимум около 170 блоков.
         */

        val baseOceanDepth =
            oceanMask * 170.0


        // ========================================================
        // 4. OCEAN FLOOR VARIATION
        // ========================================================

        /**
         * Более мелкий noise делает океанское дно
         * неоднородным.
         *
         * Поэтому океан не будет огромной плоской чашей.
         */

        val oceanFloorNoise = ArisNoise.fractal2D(
            px,
            pz,
            scale = 1800.0,
            seed = seed + 40_001L,
            octaves = 3,
            persistence = 0.5,
            lacunarity = 2.0
        )


        /**
         * Переводим [-1;1] в [0;1].
         */

        val oceanVariation =
            (oceanFloorNoise + 1.0) * 0.5


        /**
         * Дополнительный рельеф дна.
         */

        val oceanFloorVariation =
            oceanVariation * 55.0 * oceanMask


        // ========================================================
        // 5. DEEP OCEAN TRENCHES
        // ========================================================

        /**
         * Отдельная система для очень глубоких впадин.
         *
         * Она создаёт аналоги:
         *
         * - глубоководных желобов
         * - океанических впадин
         * - больших депрессий
         */

        val trenchNoise = ArisNoise.fractal2D(
            px,
            pz,
            scale = 2200.0,
            seed = seed + 80_001L,
            octaves = 3,
            persistence = 0.5,
            lacunarity = 2.0
        )


        /**
         * Выделяем только наиболее глубокие области noise.
         */

        val trenchMask = smoothstep(
            0.45,
            0.85,
            trenchNoise
        ) * oceanMask


        /**
         * Максимальная дополнительная глубина.
         */

        val trenchDepth =
            trenchMask * 180.0


        // ========================================================
        // 6. FINAL OCEAN HEIGHT
        // ========================================================

        /**
         * Полная глубина океана.
         */

        val oceanDepth =
            baseOceanDepth +
                    oceanFloorVariation +
                    trenchDepth


        /**
         * Переводим глубину в Y.
         *
         * Например:
         *
         * depth = 50
         *
         * height = -50
         */

        val oceanHeight =
            SEA_LEVEL - oceanDepth


        // ========================================================
        // 7. LAND BASE HEIGHT
        // ========================================================

        /**
         * Теперь рассчитываем сушу.
         *
         * Суша всегда начинается выше уровня моря.
         */

        val landBaseHeight =
            4.0 +
                    landAmount * 145.0


        // ========================================================
        // 8. MOUNTAIN REGION
        // ========================================================

        val mountainRegion = ArisNoise.fractal2D(
            px,
            pz,
            scale = 1800.0,
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


        // ========================================================
        // 9. RIDGES
        // ========================================================

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


        // ========================================================
        // 10. PEAKS
        // ========================================================

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


        val peakShape = smoothstep(
            0.55,
            0.90,
            peakBase
        )


        // ========================================================
        // 11. MOUNTAIN SHAPE
        // ========================================================

        val mountainShape =
            mountainMask *
                    (
                            ridgeShape * 0.75 +
                                    peakShape * 0.25
                            )


        val mountainProfile =
            mountainShape * mountainShape


        /**
         * Высота гор.
         */

        val mountainHeight =
            mountainProfile * 450.0


        // ========================================================
        // 12. SMALL LAND DETAIL
        // ========================================================

        val detail = ArisNoise.fractal2D(
            px,
            pz,
            scale = 250.0,
            seed = seed + 60_001L,
            octaves = 3,
            persistence = 0.45,
            lacunarity = 2.0
        )


        val detailHeight =
            detail * 14.0


        // ========================================================
        // 13. LAND TERRAIN
        // ========================================================

        /**
         * Горы и мелкий рельеф работают только на суше.
         */

        val landHeight =
            landBaseHeight +
                    mountainHeight * landAmount +
                    detailHeight * landAmount


        // ========================================================
        // 14. BLEND LAND / OCEAN
        // ========================================================

        /**
         * Здесь самое важное.
         *
         * Если landAmount = 0:
         *
         *     получаем океан.
         *
         * Если landAmount = 1:
         *
         *     получаем сушу.
         *
         * В переходной зоне получается берег.
         */

        var height =
            oceanHeight * (1.0 - landAmount) +
                    landHeight * landAmount


        // ========================================================
        // 15. ENSURE SEA LEVEL
        // ========================================================

        /**
         * Если это океанская область,
         * поверхность не должна оказаться выше Y=0.
         */

        if (landAmount < 0.5) {

            height =
                height.coerceAtMost(
                    SEA_LEVEL - 1.0
                )
        }


        // ========================================================
        // 16. FINAL WORLD LIMIT
        // ========================================================

        height =
            height.coerceIn(
                MIN_HEIGHT.toDouble(),
                MAX_HEIGHT.toDouble()
            )


        return height.roundToInt()
    }


    // ============================================================
    // PLAINS
    // ============================================================

    fun getPlainsHeight(
        x: Int,
        z: Int,
        seed: Long
    ): Int {

        val px = x.toDouble()
        val pz = z.toDouble()


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
            -0.05,
            0.45,
            continentalness
        )


        /**
         * Plains всегда выше моря.
         */

        val landHeight =
            5.0 +
                    landAmount * 120.0


        /**
         * Очень слабый рельеф.
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


        var height =
            landHeight +
                    detailHeight


        /**
         * Plains не должна уходить в океан.
         */

        height =
            height.coerceAtLeast(
                SEA_LEVEL + 1.0
            )


        height =
            height.coerceIn(
                MIN_HEIGHT.toDouble(),
                MAX_HEIGHT.toDouble()
            )


        return height.roundToInt()
    }


    // ============================================================
    // SMOOTHSTEP
    // ============================================================

    private fun smoothstep(
        start: Double,
        end: Double,
        value: Double
    ): Double {

        if (value <= start) {
            return 0.0
        }

        if (value >= end) {
            return 1.0
        }


        val t =
            (value - start) /
                    (end - start)


        return t * t * (3.0 - 2.0 * t)
    }

    fun getRegion(
        x: Int,
        z: Int,
        seed: Long
    ): ArisRegion {

        val continentalness = ArisNoise.fractal2D(
            x.toDouble(),
            z.toDouble(),
            scale = 5000.0,
            seed = seed,
            octaves = 4,
            persistence = 0.5,
            lacunarity = 2.0
        )

        /*
         * Это та же самая карта материков,
         * которую используем для terrain.
         */

        val landAmount = smoothstep(
            -0.05,
            0.45,
            continentalness
        )

        /*
         * Океан.
         *
         * Всё, что находится достаточно далеко
         * от материка, считается океаном.
         */

        if (landAmount < 0.5) {
            return ArisRegion.OCEAN
        }


        /*
         * Определяем горный регион.
         */

        val mountainNoise = ArisNoise.fractal2D(
            x.toDouble(),
            z.toDouble(),
            scale = 3200.0,
            seed = seed + 10_001L,
            octaves = 3,
            persistence = 0.5,
            lacunarity = 2.0
        )

        val mountainMask = smoothstep(
            0.10,
            0.60,
            mountainNoise
        )

        if (mountainMask > 0.65) {
            return ArisRegion.MOUNTAINS
        }


        /*
         * Отдельный noise для распределения
         * тайги среди обычной суши.
         */

        val taigaNoise = ArisNoise.fractal2D(
            x.toDouble(),
            z.toDouble(),
            1200.0,
            seed = seed + 70_001L,
            octaves = 3,
            persistence = 0.5,
            lacunarity = 2.0
        )

        if (taigaNoise > 0.25) {
            return ArisRegion.TAIGA
        }


        /*
         * Всё остальное пока считаем Plains.
         */

        return ArisRegion.PLAINS
    }
}