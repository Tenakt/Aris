package com.grindlesstudio.aris.worldgen.terrain

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.HeightLimitView
import net.minecraft.world.biome.BiomeKeys
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

                    BiomeSource.CODEC
                        .fieldOf("biome_source")
                        .forGetter { generator ->
                            generator.biomeSource
                        }

                ).apply(
                    instance,
                    ::ArisChunkGenerator
                )
            }

        /*
         * Этот флаг нужен только для диагностики.
         */
        private var firstChunkLogged = false
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

    /**
     * Получаем стабильный seed Aris.
     */
    private fun getArisSeed(
        noiseConfig: NoiseConfig
    ): Long {

        return noiseConfig
            .getOrCreateRandomDeriver(
                Aris.id("terrain")
            )
            .split(0L)
            .nextLong()
    }

    /**
     * Проверяем, является ли точка Plains.
     *
     * Minecraft уже имеет biome source.
     * Мы просто спрашиваем его:
     *
     * "Какой biome находится здесь?"
     */
    private fun isPlains(
        x: Int,
        y: Int,
        z: Int,
        noiseConfig: NoiseConfig
    ): Boolean {

        val biome =
            biomeSource.getBiome(
                x,
                y,
                z,
                noiseConfig.getMultiNoiseSampler()
            )

        return biome.matchesKey(
            BiomeKeys.PLAINS
        )
    }

    /**
     * Получает высоту terrain.
     *
     * Если это Plains:
     *     используем плоский terrain.
     *
     * Иначе:
     *     обычный Aris terrain.
     */
    private fun getSurfaceHeight(
        x: Int,
        z: Int,
        noiseConfig: NoiseConfig
    ): Int {

        val seed =
            getArisSeed(noiseConfig)

        /*
         * Сначала получаем обычную высоту Aris.
         *
         * Это нужно в том числе для определения biome
         * на уровне поверхности.
         */
        val normalHeight =
            ArisTerrain.getHeight(
                x,
                z,
                seed
            )

        /*
         * Узнаём biome именно около поверхности.
         */
        val plains =
            isPlains(
                x,
                normalHeight,
                z,
                noiseConfig
            )

        /*
         * Если это не Plains —
         * вообще ничего не меняем.
         */
        if (!plains) {
            return normalHeight
        }

        /*
         * Если это Plains —
         * используем специальный terrain.
         */
        return ArisTerrain.getPlainsHeight(
            x,
            z,
            seed
        )
    }

    override fun getHeight(
        x: Int,
        z: Int,
        heightmap: Heightmap.Type,
        world: HeightLimitView,
        noiseConfig: NoiseConfig
    ): Int {

        return getSurfaceHeight(
            x,
            z,
            noiseConfig
        )
    }

    override fun getColumnSample(
        x: Int,
        z: Int,
        world: HeightLimitView,
        noiseConfig: NoiseConfig
    ): VerticalBlockSample {

        val minY =
            world.bottomY

        val height =
            world.height

        val states =
            Array(height) {
                Blocks.AIR.defaultState
            }

        val surfaceY =
            getSurfaceHeight(
                x,
                z,
                noiseConfig
            )

        for (index in states.indices) {

            val y =
                minY + index

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

        /*
         * ========================================================
         * DIAGNOSTICS
         * ========================================================
         */

        if (!firstChunkLogged) {

            firstChunkLogged = true

            val arisSeed =
                getArisSeed(noiseConfig)

            Aris.LOGGER.info(
                "========================================"
            )

            Aris.LOGGER.info(
                "ARIS TERRAIN ENGINE ACTIVE"
            )

            Aris.LOGGER.info(
                "Generator: aris:aris"
            )

            Aris.LOGGER.info(
                "Terrain range: Y=${ArisTerrain.MIN_HEIGHT}..${ArisTerrain.MAX_HEIGHT}"
            )

            Aris.LOGGER.info(
                "Sea level: Y=${ArisTerrain.SEA_LEVEL}"
            )

            Aris.LOGGER.info(
                "Aris seed: $arisSeed"
            )

            Aris.LOGGER.info(
                "First terrain chunk: ${chunk.pos}"
            )

            Aris.LOGGER.info(
                "========================================"
            )
        }

        /*
         * ========================================================
         * TERRAIN GENERATION
         * ========================================================
         */

        val minY =
            chunk.bottomY

        val maxY =
            chunk.bottomY + chunk.height

        /*
         * Чанк 16x16.
         *
         * Для каждой X/Z рассчитываем свою высоту.
         */
        for (localX in 0 until 16) {

            for (localZ in 0 until 16) {

                val worldX =
                    chunk.pos.startX + localX

                val worldZ =
                    chunk.pos.startZ + localZ

                /*
                 * Здесь теперь используется
                 * Plains-aware terrain.
                 */
                val surfaceY =
                    getSurfaceHeight(
                        worldX,
                        worldZ,
                        noiseConfig
                    )

                /*
                 * Заполняем колонку.
                 */
                for (y in minY until maxY) {

                    val state =
                        getBlockStateForHeight(
                            y,
                            surfaceY
                        )

                    if (!state.isAir) {

                        chunk.setBlockState(
                            BlockPos(
                                worldX,
                                y,
                                worldZ
                            ),
                            state,
                            0
                        )
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(
            chunk
        )
    }

    /**
     * Определяем блок на конкретной высоте.
     *
     * Эту часть мы НЕ меняем.
     */
    private fun getBlockStateForHeight(
        y: Int,
        surfaceY: Int
    ): BlockState {

        /*
         * ========================================================
         * OCEAN
         * ========================================================
         */

        if (surfaceY < ArisTerrain.SEA_LEVEL) {

            /*
             * Дно океана.
             */
            if (y <= surfaceY) {

                return if (
                    y >= surfaceY - 2
                ) {
                    Blocks.SAND.defaultState
                } else {
                    Blocks.STONE.defaultState
                }
            }

            /*
             * Вода до уровня моря.
             */
            if (y <= ArisTerrain.SEA_LEVEL) {

                return Blocks.WATER.defaultState
            }

            return Blocks.AIR.defaultState
        }

        /*
         * ========================================================
         * LAND
         * ========================================================
         */

        if (y <= surfaceY) {

            /*
             * Верхний блок.
             */
            if (y == surfaceY) {

                return Blocks.GRASS_BLOCK.defaultState
            }

            /*
             * Два блока земли.
             */
            if (y >= surfaceY - 2) {

                return Blocks.DIRT.defaultState
            }

            /*
             * Остальное — камень.
             */
            return Blocks.STONE.defaultState
        }

        /*
         * Выше поверхности — воздух.
         */
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
        // Aris Underground будет добавлен позже.
    }

    override fun buildSurface(
        region: net.minecraft.world.ChunkRegion,
        structures: StructureAccessor,
        noiseConfig: NoiseConfig,
        chunk: Chunk
    ) {
        // Surface уже создан Aris.
    }

    override fun populateEntities(
        region: net.minecraft.world.ChunkRegion
    ) {
        // Пока ничего не делаем.
    }

    /**
     * F3 → Debug HUD.
     */
    override fun appendDebugHudText(
        text: MutableList<String>,
        noiseConfig: NoiseConfig,
        pos: BlockPos
    ) {

        val height =
            getSurfaceHeight(
                pos.x,
                pos.z,
                noiseConfig
            )

        text.add(
            "Aris Terrain"
        )

        text.add(
            "Aris Height: $height"
        )
    }
}