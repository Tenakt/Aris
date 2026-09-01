package com.grindlesstudio.aris.worldgen.feature

import com.mojang.serialization.Codec
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration

class SmallStreamFeature(config: Codec<NoneFeatureConfiguration>) : Feature<NoneFeatureConfiguration>(config) {
    override fun place(context: FeaturePlaceContext<NoneFeatureConfiguration>): Boolean {
        val world = context.level()
        val random = context.random()
        val origin = context.origin()

        val surfacePos = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, origin)
        if (!world.getBlockState(surfacePos.below()).`is`(Blocks.GRASS_BLOCK)) {
            return false
        }

        val length = 12 + random.nextInt(10)
        var currentPos = surfacePos
        var direction = Direction.Plane.HORIZONTAL.getRandomDirection(random)

        for (i in 0 until length) {
            val width = if (random.nextBoolean()) 0 else 1

            for (dx in -width..width) {
                for (dz in -width..width) {
                    val airPos = currentPos.offset(dx, 0, dz)
                    val waterPos = airPos.below()
                    val bottomPos = airPos.below(2)

                    world.setBlock(airPos, Blocks.AIR.defaultBlockState(), 2)
                    world.setBlock(waterPos, Blocks.WATER.defaultBlockState(), 2)

                    val bottomBlock = if (random.nextBoolean()) Blocks.GRAVEL else Blocks.DIRT
                    world.setBlock(bottomPos, bottomBlock.defaultBlockState(), 2)
                }
            }

            if (random.nextFloat() < 0.35f) {
                direction = if (random.nextBoolean()) direction.clockWise else direction.counterClockWise
            }
            currentPos = currentPos.relative(direction)
        }

        return true
    }
}