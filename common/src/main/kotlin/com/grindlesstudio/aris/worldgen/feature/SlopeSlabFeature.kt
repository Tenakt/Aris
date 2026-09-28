package com.grindlesstudio.aris.worldgen.feature

import com.grindlesstudio.aris.block.ModBlocks
import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.state.properties.SlabType
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.feature.Feature

class SlopeSlabFeature : Feature {

    override fun codec(): MapCodec<out Feature> {
        return CODEC
    }

    override fun place(
        level: WorldGenLevel,
        chunkGenerator: ChunkGenerator,
        random: RandomSource,
        origin: BlockPos
    ): Boolean {

        for (x in 0..15) {
            for (z in 0..15) {

                val currentX = origin.x + x
                val currentZ = origin.z + z

                val surfaceY =
                    level.getHeight(
                        Heightmap.Types.WORLD_SURFACE_WG,
                        currentX,
                        currentZ
                    ) - 1

                val pos = BlockPos(
                    currentX,
                    surfaceY,
                    currentZ
                )

                val state = level.getBlockState(pos)
                val abovePos = pos.above()

                if (!level.getBlockState(abovePos).isAir) {
                    continue
                }

                var isStep = false

                for (dir in Direction.Plane.HORIZONTAL) {

                    val neighborPos = pos.relative(dir)

                    val neighborY =
                        level.getHeight(
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

                    level.setBlock(
                        abovePos,
                        slabState,
                        3
                    )

                    if (state.`is`(Blocks.GRASS_BLOCK)) {
                        level.setBlock(
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

    companion object {

        val CODEC: MapCodec<SlopeSlabFeature> =
            MapCodec.unit(::SlopeSlabFeature)
    }
}