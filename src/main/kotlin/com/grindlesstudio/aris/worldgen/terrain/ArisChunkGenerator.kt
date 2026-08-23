package com.grindlesstudio.aris.worldgen.terrain

import com.grindlesstudio.aris.Aris
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.HeightLimitView
import net.minecraft.world.biome.source.BiomeSource
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.StructureAccessor
import net.minecraft.world.gen.chunk.Blender
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.chunk.VerticalBlockSample
import net.minecraft.world.Heightmap
import net.minecraft.world.gen.noise.NoiseConfig
import java.util.concurrent.CompletableFuture

class ArisChunkGenerator(
    biomeSource: BiomeSource
) : ChunkGenerator(biomeSource) {

    companion object {

        val CODEC: MapCodec<ArisChunkGenerator> =
            RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source")
                        .forGetter { generator ->
                            generator.biomeSource
                        }
                ).apply(instance, ::ArisChunkGenerator)
            }
    }

    override fun getCodec(): MapCodec<ArisChunkGenerator> {
        return CODEC
    }

    /**
     * Технический диапазон Minecraft.
     * Aris сам генерирует terrain только в -350..650.
     */
    override fun getWorldHeight(): Int {
        return 1008
    }

    override fun getMinimumY(): Int {
        return -352
    }

    override fun getSeaLevel(): Int {
        return ArisTerrain.SEA_LEVEL
    }

    /**
     * Получаем стабильный seed для Aris из NoiseConfig.
     * NoiseConfig создаётся Minecraft с seed конкретного мира,
     * поэтому одинаковый мир всегда получает одинаковый Aris seed,
     * а другой seed создаёт другой terrain.
     */
    private fun getArisSeed(noiseConfig: NoiseConfig): Long {
        return noiseConfig
            .getOrCreateRandomDeriver(Aris.id("terrain"))
            .split(0L)
            .nextLong()
    }

    override fun getHeight(
        x: Int,
        z: Int,
        heightmap: Heightmap.Type,
        world: HeightLimitView,
        noiseConfig: NoiseConfig
    ): Int {
        return ArisTerrain.getHeight(
            x,
            z,
            getArisSeed(noiseConfig)
        )
    }

    override fun getColumnSample(
        x: Int,
        z: Int,
        world: HeightLimitView,
        noiseConfig: NoiseConfig
    ): VerticalBlockSample {
        val minY = world.bottomY
        val height = world.height
        val states = Array(height) { Blocks.AIR.defaultState }

        val surfaceY = ArisTerrain.getHeight(
            x,
            z,
            getArisSeed(noiseConfig)
        )

        for (index in states.indices) {
            val y = minY + index
            states[index] = getBlockStateForHeight(y, surfaceY)
        }

        return VerticalBlockSample(minY, states)
    }

    override fun populateNoise(
        blender: Blender,
        noiseConfig: NoiseConfig,
        structureAccessor: StructureAccessor,
        chunk: Chunk
    ): CompletableFuture<Chunk> {
        Aris.LOGGER.info(
            "Aris Terrain Generator ACTIVE — generating chunk {}",
            chunk.pos
        )
        val minY = chunk.bottomY
        val maxY = chunk.bottomY + chunk.height
        val arisSeed = getArisSeed(noiseConfig)

        // Chunk = 16x16 колонок. Высота каждой колонки рассчитывается
        // отдельно нашим Terrain Engine.
        for (localX in 0 until 16) {
            for (localZ in 0 until 16) {
                val worldX = chunk.pos.startX + localX
                val worldZ = chunk.pos.startZ + localZ

                val surfaceY = ArisTerrain.getHeight(
                    worldX,
                    worldZ,
                    arisSeed
                )

                for (y in minY until maxY) {
                    val state = getBlockStateForHeight(y, surfaceY)

                    if (!state.isAir) {
                        chunk.setBlockState(
                            BlockPos(worldX, y, worldZ),
                            state,
                            0
                        )
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(chunk)
    }

    private fun getBlockStateForHeight(
        y: Int,
        surfaceY: Int
    ): BlockState {
        // Подводная колонка: сначала формируем дно,
        // затем вода заполняет пространство до уровня моря.
        if (surfaceY < ArisTerrain.SEA_LEVEL) {
            if (y <= surfaceY) {
                return if (y >= surfaceY - 2) {
                    Blocks.SAND.defaultState
                } else {
                    Blocks.STONE.defaultState
                }
            }

            if (y <= ArisTerrain.SEA_LEVEL) {
                return Blocks.WATER.defaultState
            }

            return Blocks.AIR.defaultState
        }

        // Суша.
        if (y <= surfaceY) {
            if (y == surfaceY) {
                return Blocks.GRASS_BLOCK.defaultState
            }

            if (y >= surfaceY - 2) {
                return Blocks.DIRT.defaultState
            }

            return Blocks.STONE.defaultState
        }

        return Blocks.AIR.defaultState
    }

    override fun carve(
        chunkRegion: net.minecraft.world.ChunkRegion,
        seed: Long,
        noiseConfig: NoiseConfig,
        biomeAccess: net.minecraft.world.biome.source.BiomeAccess,
        structureAccessor: StructureAccessor,
        chunk: Chunk
    ) {
        // Пещеры появятся отдельным этапом Aris Underground.
    }

    override fun buildSurface(
        region: net.minecraft.world.ChunkRegion,
        structures: StructureAccessor,
        noiseConfig: NoiseConfig,
        chunk: Chunk
    ) {
        // Surface уже создаётся в populateNoise().
    }

    override fun populateEntities(
        region: net.minecraft.world.ChunkRegion
    ) {
        // Пока ничего не делаем.
    }

    override fun appendDebugHudText(
        text: MutableList<String>,
        noiseConfig: NoiseConfig,
        pos: BlockPos
    ) {
        text.add("Aris Terrain")
        text.add("Aris Height: ${ArisTerrain.getHeight(pos.x, pos.z, getArisSeed(noiseConfig))}")
    }
}
