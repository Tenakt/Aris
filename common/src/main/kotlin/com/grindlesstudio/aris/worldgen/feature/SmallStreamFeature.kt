package com.grindlesstudio.aris.worldgen.feature

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.feature.Feature

class SmallStreamFeature : Feature {

    override fun codec(): MapCodec<out Feature> {
        return CODEC
    }

    override fun place(
        level: WorldGenLevel,
        chunkGenerator: ChunkGenerator,
        random: RandomSource,
        origin: BlockPos
    ): Boolean {

        val surfacePos =
            level.getHeightmapPos(
                Heightmap.Types.WORLD_SURFACE_WG,
                origin
            )

        if (
            !level.getBlockState(
                surfacePos.below()
            ).`is`(Blocks.GRASS_BLOCK)
        ) {
            return false
        }

        val length =
            12 + random.nextInt(10)

        var currentPos =
            surfacePos

        var direction =
            Direction.Plane.HORIZONTAL
                .getRandomDirection(random)

        for (i in 0 until length) {

            val width =
                if (random.nextBoolean()) {
                    0
                } else {
                    1
                }

            for (dx in -width..width) {
                for (dz in -width..width) {

                    val airPos =
                        currentPos.offset(
                            dx,
                            0,
                            dz
                        )

                    val waterPos =
                        airPos.below()

                    val bottomPos =
                        airPos.below(2)

                    level.setBlock(
                        airPos,
                        Blocks.AIR.defaultBlockState(),
                        2
                    )

                    level.setBlock(
                        waterPos,
                        Blocks.WATER.defaultBlockState(),
                        2
                    )

                    val bottomBlock =
                        if (random.nextBoolean()) {
                            Blocks.GRAVEL
                        } else {
                            Blocks.DIRT
                        }

                    level.setBlock(
                        bottomPos,
                        bottomBlock.defaultBlockState(),
                        2
                    )
                }
            }

            if (random.nextFloat() < 0.35f) {

                direction =
                    if (random.nextBoolean()) {
                        direction.clockWise
                    } else {
                        direction.counterClockWise
                    }
            }

            currentPos =
                currentPos.relative(direction)
        }

        return true
    }

    companion object {

        val CODEC: MapCodec<SmallStreamFeature> =
            MapCodec.unit(
                ::SmallStreamFeature
            )
    }
}
