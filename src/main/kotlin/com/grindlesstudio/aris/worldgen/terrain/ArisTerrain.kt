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
//
// Горы Aris состоят из нескольких уровней:
//
// 1. mountainRegion — большая горная область.
// 2. ridgeNoise — положение основных хребтов.
// 3. peakNoise — небольшие пики внутри хребтов.
//
// Благодаря этому одна mountain system может содержать
// несколько связанных между собой гор.


// --------------------------------------------------------
// 4.1. Большая горная область
// --------------------------------------------------------

        val mountainRegion = ArisNoise.fractal2D(
            px,
            pz,
            scale = 3200.0,
            seed = seed + 10_001L,
            octaves = 3,
            persistence = 0.5,
            lacunarity = 2.0
        )

// Определяем, насколько мы близко к центру
// большой горной системы.
        val mountainMask = smoothstep(
            0.10,
            0.60,
            mountainRegion
        )


// --------------------------------------------------------
// 4.2. Основные хребты
// --------------------------------------------------------
//
// Более крупный noise создаёт длинные структуры.
//
// Мы специально используем меньшую частоту,
// чем у mountainRegion, чтобы внутри одной
// большой системы появлялось несколько хребтов.

        val ridgeNoise = ArisNoise.fractal2D(
            px,
            pz,
            scale = 1200.0,
            seed = seed + 20_001L,
            octaves = 3,
            persistence = 0.5,
            lacunarity = 2.0
        )


// Переводим:
//
// -1 ... +1
//
// в:
//
// 0 ... 1

        val ridgeBase =
            (ridgeNoise + 1.0) * 0.5


// Усиливаем центральную часть хребтов.
//
// Значения около 1 остаются высокими,
// а слабые значения становятся намного меньше.

        val ridgeShape =
            ridgeBase * ridgeBase


// --------------------------------------------------------
// 4.3. Пики
// --------------------------------------------------------
//
// Теперь добавляем более мелкий noise.
//
// Он не создаёт новую горную систему.
// Он только делает вершины внутри
// существующих хребтов разнообразнее.

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


// Пики должны появляться только в сильных
// частях хребта.

        val peakShape =
            smoothstep(
                0.55,
                0.90,
                peakBase
            )


// --------------------------------------------------------
// 4.4. Объединяем хребты и пики
// --------------------------------------------------------
//
// Основная форма горы:
//
// ridgeShape
//
// Дополнительная высота:
//
// peakShape

        val mountainShape =
            mountainMask *
                    (
                            ridgeShape * 0.75 +
                                    peakShape * 0.25
                            )


// --------------------------------------------------------
// 4.5. Профиль высоты
// --------------------------------------------------------
//
// Здесь мы делаем важную вещь.
//
// Вместо того чтобы просто:
//
// mountainShape * 500
//
// используем степень.
//
// Это делает слабые части горы ниже,
// а сильные части — заметнее.
//
// Получается более выраженная вершина.

        val mountainProfile =
            mountainShape * mountainShape


// --------------------------------------------------------
// 4.6. Высота гор
// --------------------------------------------------------
//
// Максимальная добавка пока около 450 блоков.
//
// Мы специально немного снизили значение,
// потому что сначала хотим проверить форму,
// а потом уже увеличивать высоту.

        val mountainHeight =
            mountainProfile * 450.0

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
