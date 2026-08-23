package com.grindlesstudio.aris.worldgen.terrain

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.HeightLimitView
import net.minecraft.world.biome.source.BiomeSource
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.chunk.Blender
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.chunk.VerticalBlockSample
import net.minecraft.world.Heightmap
import net.minecraft.world.gen.noise.NoiseConfig
import net.minecraft.world.gen.StructureAccessor
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

    override fun getWorldHeight(): Int {
        return 1008
    }

    override fun getMinimumY(): Int {
        return -352
    }

    override fun getSeaLevel(): Int {
        return ArisTerrain.SEA_LEVEL
    }

    override fun getHeight(
        x: Int,
        z: Int,
        heightmap: Heightmap.Type,
        world: HeightLimitView,
        noiseConfig: NoiseConfig
    ): Int {

        /*
         * Пока ArisTerrain использует собственный
         * фиксированный seed.
         *
         * Позже сюда подключим настоящий seed мира.
         */
        return ArisTerrain.getHeight(
            x,
            z,
            0L
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

        val states = Array(height) {
            Blocks.AIR.defaultState
        }

        val surfaceY = ArisTerrain.getHeight(
            x,
            z,
            0L
        )

        for (index in states.indices) {

            val y = minY + index

            states[index] =
                getBlockStateForHeight(
                    y,
                    surfaceY
                )
        }

        return VerticalBlockSample(
            minY,
            states
        )
    }

    override fun populateNoise(
        blender: Blender,
        noiseConfig: NoiseConfig,
        structureAccessor: StructureAccessor,
        chunk: Chunk
    ): CompletableFuture<Chunk> {

        val minY = chunk.bottomY
        val maxY = chunk.bottomY + chunk.height

        /*
         * Chunk имеет размер 16×16.
         *
         * Для каждого X/Z определяем высоту
         * поверхности Aris.
         */
        for (localX in 0 until 16) {

            for (localZ in 0 until 16) {

                val worldX =
                    chunk.pos.startX + localX

                val worldZ =
                    chunk.pos.startZ + localZ

                val surfaceY =
                    ArisTerrain.getHeight(
                        worldX,
                        worldZ,
                        0L
                    )

                /*
                 * Заполняем вертикальную колонку.
                 */
                for (y in minY until maxY) {

                    val state =
                        getBlockStateForHeight(
                            y,
                            surfaceY
                        )

                    if (!state.isAir) {

                        val pos = BlockPos(
                            worldX,
                            y,
                            worldZ
                        )

                        /*
                         * В этой версии Minecraft
                         * setBlockState принимает
                         * BlockState и BlockState flags.
                         *
                         * 0 = без дополнительных обновлений.
                         */
                        chunk.setBlockState(
                            pos,
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

        /*
         * Ниже или на уровне поверхности
         * находится земля.
         */
        if (y <= surfaceY) {

            /*
             * Верхний блок.
             */
            if (y == surfaceY) {
                return Blocks.GRASS_BLOCK.defaultState
            }

            /*
             * Два блока под травой.
             */
            if (y >= surfaceY - 2) {
                return Blocks.DIRT.defaultState
            }

            /*
             * Остальная толща.
             */
            return Blocks.STONE.defaultState
        }

        /*
         * Если поверхность находится ниже
         * уровня моря — создаём воду.
         */
        if (
            surfaceY < ArisTerrain.SEA_LEVEL &&
            y <= ArisTerrain.SEA_LEVEL
        ) {
            return Blocks.WATER.defaultState
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
        /*
         * Пещеры пока отключены.
         *
         * Позже здесь появится
         * собственная система пещер Aris.
         */
    }

    override fun buildSurface(
        region: net.minecraft.world.ChunkRegion,
        structures: StructureAccessor,
        noiseConfig: NoiseConfig,
        chunk: Chunk
    ) {
        /*
         * Пока поверхность уже создаётся
         * непосредственно в populateNoise().
         */
    }

    override fun populateEntities(
        region: net.minecraft.world.ChunkRegion
    ) {
        /*
         * Пока ничего не делаем.
         */
    }

    override fun appendDebugHudText(
        text: MutableList<String>,
        noiseConfig: NoiseConfig,
        pos: BlockPos
    ) {
        /*
         * Позже здесь можно будет выводить
         * значения Aris Noise через F3.
         *
         * Например:
         *
         * Aris Height: 127
         * Continentalness: 0.42
         * Mountain: 0.17
         */
        text.add(
            "Aris Terrain"
        )
    }
}