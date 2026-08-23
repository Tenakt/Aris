package com.grindlesstudio.aris.worldgen.terrain

import kotlin.math.roundToInt

object ArisTerrain {

    const val MIN_HEIGHT = -350
    const val MAX_HEIGHT = 650
    const val SEA_LEVEL = 0

    /**
     * Главная функция terrain Aris.
     *
     * По координатам X/Z возвращает высоту поверхности.
     */
    fun getHeight(x: Int, z: Int, seed: Long): Int {

        /*
         * 1. Крупнейший noise.
         *
         * Он определяет:
         * океан это,
         * берег,
         * материк
         * или глубокая суша.
         */
        val continentalness =
            ArisNoise.noise2D(
                x.toDouble(),
                z.toDouble(),
                5000.0,
                seed
            )

        /*
         * 2. Noise гор.
         *
         * Пока он просто добавляет крупные
         * возвышенности.
         */
        val mountains =
            ArisNoise.noise2D(
                x.toDouble(),
                z.toDouble(),
                1800.0,
                seed + 1000
            )

        /*
         * 3. Мелкие детали.
         *
         * Они не должны сильно менять высоту.
         */
        val detail =
            ArisNoise.noise2D(
                x.toDouble(),
                z.toDouble(),
                250.0,
                seed + 2000
            )

        /*
         * Континентальная высота.
         *
         * Если continentalness отрицательный,
         * мы постепенно опускаемся ниже уровня моря.
         */
        val baseHeight = continentalness * 180.0

        /*
         * Горы.
         *
         * Пока они могут добавлять примерно
         * до сотен блоков высоты.
         */
        val mountainHeight =
            mountains.coerceAtLeast(0.0) * 450.0

        /*
         * Небольшие детали рельефа.
         */
        val detailHeight = detail * 12.0

        var height =
            baseHeight +
                    mountainHeight +
                    detailHeight

        /*
         * Ограничиваем terrain нашим диапазоном.
         *
         * Именно здесь находятся наши:
         *
         * -350
         * +
         * 650
         */
        height = height.coerceIn(
            MIN_HEIGHT.toDouble(),
            MAX_HEIGHT.toDouble()
        )

        return height.roundToInt()
    }
}