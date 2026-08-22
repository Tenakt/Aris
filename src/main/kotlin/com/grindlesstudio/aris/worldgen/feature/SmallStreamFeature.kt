package com.grindlesstudio.aris.worldgen.feature

import com.mojang.serialization.Codec
import net.minecraft.block.Blocks
import net.minecraft.util.math.Direction
import net.minecraft.world.Heightmap
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.util.FeatureContext

class SmallStreamFeature(config: Codec<DefaultFeatureConfig>) : Feature<DefaultFeatureConfig>(config) {
    override fun generate(context: FeatureContext<DefaultFeatureConfig>): Boolean {
        val world = context.world
        val random = context.random
        val origin = context.origin

        // Ищем верхнюю точку поверхности
        val surfacePos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, origin)
        if (!world.getBlockState(surfacePos.down()).isOf(Blocks.GRASS_BLOCK)) {
            return false
        }

        val length = 12 + random.nextInt(10) // Длина ручья: 12–22 блока
        var currentPos = surfacePos
        var direction = Direction.Type.HORIZONTAL.random(random)

        for (i in 0 until length) {
            val width = if (random.nextBoolean()) 0 else 1 // Ширина ручья (0 = 1 блок, 1 = 2-3 блока)

            for (dx in -width..width) {
                for (dz in -width..width) {
                    val airPos = currentPos.add(dx, 0, dz)       // Уровень травы (освобождаем воздух)
                    val waterPos = airPos.down()                 // Уровень русла (опускаем воду ниже земли)
                    val bottomPos = airPos.down(2)               // Дно ручья

                    // Прорезаем русло ниже уровня поверхности
                    world.setBlockState(airPos, Blocks.AIR.defaultState, 2)
                    world.setBlockState(waterPos, Blocks.WATER.defaultState, 2)

                    // Делаем дно из гравия или песка для красоты
                    val bottomBlock = if (random.nextBoolean()) Blocks.GRAVEL else Blocks.DIRT
                    world.setBlockState(bottomPos, bottomBlock.defaultState, 2)
                }
            }

            // Извилистость: ручей периодически плавно меняет направление
            if (random.nextFloat() < 0.35f) {
                direction = if (random.nextBoolean()) direction.rotateYClockwise() else direction.rotateYCounterclockwise()
            }
            currentPos = currentPos.offset(direction)
        }

        return true
    }
}