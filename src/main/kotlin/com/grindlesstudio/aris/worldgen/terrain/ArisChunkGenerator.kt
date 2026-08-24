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

        /*
         * Этот флаг нужен только для диагностики.
         *
         * Он позволит написать в лог сообщение один раз,
         * а не спамить лог каждым чанком.
         */
        private var firstChunkLogged = false
    }

    override fun getCodec(): MapCodec<ArisChunkGenerator> {
        return CODEC
    }

    /*
     * Технический диапазон Minecraft.
     *
     * Наш дизайн Aris:
     *
     *      -350 ... 650
     *
     * Но Minecraft использует технический диапазон:
     *
     *      -352 ... 655
     *
     * Последние значения нужны только для технической совместимости.
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

    /*
     * Получаем стабильный seed для Terrain Engine.
     *
     * Один и тот же Minecraft world seed
     * → один и тот же Aris terrain.
     *
     * Другой world seed
     * → другой Aris terrain.
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

        val states = Array(height) {
            Blocks.AIR.defaultState
        }

        val surfaceY = ArisTerrain.getHeight(
            x,
            z,
            getArisSeed(noiseConfig)
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

        /*
         * =========================================================
         * ARIS DIAGNOSTICS
         * =========================================================
         */

        if (!firstChunkLogged) {

            firstChunkLogged = true

            val arisSeed = getArisSeed(noiseConfig)

            Aris.LOGGER.info("========================================")
            Aris.LOGGER.info("ARIS TERRAIN ENGINE ACTIVE")
            Aris.LOGGER.info("Generator: aris:aris")
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
            Aris.LOGGER.info("========================================")
        }

        /*
         * =========================================================
         * TERRAIN GENERATION
         * =========================================================
         */

        val minY = chunk.bottomY
        val maxY = chunk.bottomY + chunk.height

        val arisSeed = getArisSeed(noiseConfig)

        /*
         * Чанк Minecraft имеет размер 16x16 блоков.
         *
         * Поэтому мы рассчитываем высоту отдельно
         * для каждой координаты X/Z.
         */
        for (localX in 0 until 16) {

            for (localZ in 0 until 16) {

                val worldX =
                    chunk.pos.startX + localX

                val worldZ =
                    chunk.pos.startZ + localZ

                /*
                 * Здесь происходит главное:
                 *
                 * Minecraft спрашивает:
                 *
                 * "Какая высота terrain в этой точке?"
                 *
                 * Aris отвечает:
                 *
                 * "Вот высота, которую рассчитал мой Terrain Engine."
                 */
                val surfaceY =
                    ArisTerrain.getHeight(
                        worldX,
                        worldZ,
                        arisSeed
                    )

                /*
                 * Заполняем колонку блоками
                 * от нижней границы мира
                 * до поверхности.
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

        return CompletableFuture.completedFuture(chunk)
    }

    /*
     * Определяем, какой блок должен находиться
     * на конкретной высоте.
     */
    private fun getBlockStateForHeight(
        y: Int,
        surfaceY: Int
    ): BlockState {

        /*
         * =========================================================
         * OCEAN
         * =========================================================
         */

        if (surfaceY < ArisTerrain.SEA_LEVEL) {

            /*
             * Дно океана.
             */
            if (y <= surfaceY) {

                return if (y >= surfaceY - 2) {
                    Blocks.SAND.defaultState
                } else {
                    Blocks.STONE.defaultState
                }
            }

            /*
             * Вода от дна до уровня моря.
             */
            if (y <= ArisTerrain.SEA_LEVEL) {
                return Blocks.WATER.defaultState
            }

            return Blocks.AIR.defaultState
        }

        /*
         * =========================================================
         * LAND
         * =========================================================
         */

        if (y <= surfaceY) {

            /*
             * Верхний блок.
             */
            if (y == surfaceY) {
                return Blocks.GRASS_BLOCK.defaultState
            }

            /*
             * Два слоя земли под травой.
             */
            if (y >= surfaceY - 2) {
                return Blocks.DIRT.defaultState
            }

            /*
             * Всё глубже — камень.
             */
            return Blocks.STONE.defaultState
        }

        /*
         * Выше поверхности — воздух.
         */
        return Blocks.AIR.defaultState
    }

    /*
     * Пещеры пока отключены.
     *
     * Позже здесь появится:
     *
     * Aris Underground
     */
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

    /*
     * Поверхность уже создаётся в populateNoise().
     */
    override fun buildSurface(
        region: net.minecraft.world.ChunkRegion,
        structures: StructureAccessor,
        noiseConfig: NoiseConfig,
        chunk: Chunk
    ) {
        // Surface уже создан Aris.
    }

    /*
     * Пока Aris сам ничего не добавляет
     * в populateEntities().
     */
    override fun populateEntities(
        region: net.minecraft.world.ChunkRegion
    ) {
        // Пока ничего не делаем.
    }

    /*
     * F3 → Debug HUD.
     *
     * Показывает высоту Aris в текущей позиции.
     */
    override fun appendDebugHudText(
        text: MutableList<String>,
        noiseConfig: NoiseConfig,
        pos: BlockPos
    ) {

        val height =
            ArisTerrain.getHeight(
                pos.x,
                pos.z,
                getArisSeed(noiseConfig)
            )

        text.add("Aris Terrain")
        text.add("Aris Height: $height")
    }
}