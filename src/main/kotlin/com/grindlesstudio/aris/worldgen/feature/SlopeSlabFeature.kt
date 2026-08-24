package com.grindlesstudio.aris.worldgen.feature

import com.grindlesstudio.aris.block.ModBlocks
import com.mojang.serialization.Codec
import net.minecraft.block.Blocks
import net.minecraft.block.SlabBlock
import net.minecraft.block.enums.SlabType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.Heightmap
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.util.FeatureContext

class SlopeSlabFeature(configCodec: Codec<DefaultFeatureConfig>) : Feature<DefaultFeatureConfig>(configCodec) {
    override fun generate(context: FeatureContext<DefaultFeatureConfig>): Boolean {
        val world = context.world
        val origin = context.origin

        for (x in 0..15) {
            for (z in 0..15) {
                val currentX = origin.x + x
                val currentZ = origin.z + z
                val surfaceY = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, currentX, currentZ) - 1
                val pos = BlockPos(currentX, surfaceY, currentZ)
                val state = world.getBlockState(pos)

                val abovePos = pos.up()
                if (!world.getBlockState(abovePos).isAir) continue

                // Проверяем, есть ли рядом соседняя позиция, которая выше ровно на 1 блок
                var isStep = false
                for (dir in Direction.Type.HORIZONTAL) {
                    val neighborPos = pos.offset(dir)
                    val neighborY = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, neighborPos.x, neighborPos.z) - 1
                    if (neighborY == surfaceY + 1) {
                        isStep = true
                        break
                    }
                }

                if (isStep) {
                    val slabState = when {
                        state.isOf(Blocks.GRASS_BLOCK) -> ModBlocks.GRASS_SLAB.defaultState.with(SlabBlock.TYPE, SlabType.BOTTOM)
                        state.isOf(Blocks.DIRT) -> ModBlocks.DIRT_SLAB.defaultState.with(SlabBlock.TYPE, SlabType.BOTTOM)
                        state.isOf(Blocks.STONE) -> Blocks.STONE_SLAB.defaultState.with(SlabBlock.TYPE, SlabType.BOTTOM)
                        else -> null
                    }

                    if (slabState != null) {
                        // 1. Ставим полублок
                        world.setBlockState(abovePos, slabState, 3)

                        // 2. Превращаем перекрытый дёрн снизу в грязь
                        if (state.isOf(Blocks.GRASS_BLOCK)) {
                            world.setBlockState(pos, Blocks.DIRT.defaultState, 3)
                        }
                    }
                }
            }
        }
        return true
    }
}