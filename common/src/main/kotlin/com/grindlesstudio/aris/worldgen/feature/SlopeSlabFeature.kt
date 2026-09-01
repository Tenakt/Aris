package com.grindlesstudio.aris.worldgen.feature

import com.grindlesstudio.aris.block.ModBlocks
import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.state.properties.SlabType
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration

class SlopeSlabFeature(
    configCodec: Codec<NoneFeatureConfiguration>
) : Feature<NoneFeatureConfiguration>(configCodec) {

    override fun place(
        context: FeaturePlaceContext<NoneFeatureConfiguration>
    ): Boolean {

        val world = context.level()
        val origin = context.origin()

        for (x in 0..15) {

            for (z in 0..15) {

                val currentX = origin.x + x
                val currentZ = origin.z + z

                val surfaceY =
                    world.getHeight(
                        Heightmap.Types.WORLD_SURFACE_WG,
                        currentX,
                        currentZ
                    ) - 1

                val pos =
                    BlockPos(
                        currentX,
                        surfaceY,
                        currentZ
                    )

                val state =
                    world.getBlockState(pos)

                val abovePos =
                    pos.above()

                if (!world.getBlockState(abovePos).isAir) {
                    continue
                }

                var isStep = false

                for (dir in Direction.Plane.HORIZONTAL) {

                    val neighborPos =
                        pos.relative(dir)

                    val neighborY =
                        world.getHeight(
                            Heightmap.Types.WORLD_SURFACE_WG,
                            neighborPos.x,
                            neighborPos.z
                        ) - 1

                    if (neighborY == surfaceY + 1) {

                        isStep = true
                        break
                    }
                }

                if (!isStep) {
                    continue
                }

                val slabState = when {

                    state.`is`(Blocks.GRASS_BLOCK) ->
                        ModBlocks.GRASS_SLAB
                            .defaultBlockState()
                            .setValue(
                                SlabBlock.TYPE,
                                SlabType.BOTTOM
                            )

                    state.`is`(Blocks.DIRT) ->
                        ModBlocks.DIRT_SLAB
                            .defaultBlockState()
                            .setValue(
                                SlabBlock.TYPE,
                                SlabType.BOTTOM
                            )

                    state.`is`(Blocks.STONE) ->
                        Blocks.STONE_SLAB
                            .defaultBlockState()
                            .setValue(
                                SlabBlock.TYPE,
                                SlabType.BOTTOM
                            )

                    state.`is`(Blocks.SAND) ->
                        ModBlocks.SAND_SLAB
                            .defaultBlockState()
                            .setValue(
                                SlabBlock.TYPE,
                                SlabType.BOTTOM
                            )

                    else -> null
                }

                if (slabState != null) {

                    world.setBlock(
                        abovePos,
                        slabState,
                        3
                    )

                    if (state.`is`(Blocks.GRASS_BLOCK)) {

                        world.setBlock(
                            pos,
                            Blocks.DIRT.defaultBlockState(),
                            3
                        )
                    }
                }
            }
        }

        return true
    }
}