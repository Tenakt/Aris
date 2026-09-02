//package com.grindlesstudio.aris.worldgen.terrain
//
//import com.grindlesstudio.aris.Aris
//import com.mojang.serialization.MapCodec
//import com.mojang.serialization.codecs.RecordCodecBuilder
//import net.minecraft.core.BlockPos
//import net.minecraft.server.level.WorldGenRegion
//import net.minecraft.world.level.LevelHeightAccessor
//import net.minecraft.world.level.NoiseColumn
//import net.minecraft.world.level.StructureManager
//import net.minecraft.world.level.biome.BiomeManager
//import net.minecraft.world.level.biome.BiomeSource
//import net.minecraft.world.level.biome.Biomes
//import net.minecraft.world.level.block.Blocks
//import net.minecraft.world.level.block.state.BlockState
//import net.minecraft.world.level.chunk.ChunkAccess
//import net.minecraft.world.level.chunk.ChunkGenerator
//import net.minecraft.world.level.levelgen.Heightmap
//import net.minecraft.world.level.levelgen.RandomState
//import net.minecraft.world.level.levelgen.blending.Blender
//import java.util.concurrent.CompletableFuture
//
//class ArisChunkGenerator(
//    biomeSource: BiomeSource
//) : ChunkGenerator(biomeSource) {
//
//    companion object {
//
//        val CODEC: MapCodec<ArisChunkGenerator> =
//            RecordCodecBuilder.mapCodec { instance ->
//
//                instance.group(
//
//                    BiomeSource.CODEC
//                        .fieldOf("biome_source")
//                        .forGetter { generator ->
//                            generator.biomeSource
//                        }
//
//                ).apply(
//                    instance,
//                    ::ArisChunkGenerator
//                )
//            }
//
//        private var firstChunkLogged = false
//    }
//
//    override fun codec(): MapCodec<ArisChunkGenerator> {
//        return CODEC
//    }
//
//    override fun getGenDepth(): Int {
//        return 1008
//    }
//
//    override fun getMinY(): Int {
//        return -352
//    }
//
//    override fun getSeaLevel(): Int {
//        return ArisTerrain.SEA_LEVEL
//    }
//
//    private fun getArisSeed(
//        randomState: RandomState
//    ): Long {
//        return randomState
//            .getOrCreateRandomFactory(Aris.id("terrain"))
//            .fromHashOf(Aris.id("terrain").toString())
//            .nextLong()
//    }
//
//    private fun isPlains(
//        x: Int,
//        y: Int,
//        z: Int,
//        randomState: RandomState
//    ): Boolean {
//
//        val biome =
//            biomeSource.getNoiseBiome(
//                x,
//                y,
//                z,
//                randomState.sampler()
//            )
//
//        return biome.`is`(Biomes.PLAINS)
//    }
//
//    private fun getSurfaceHeight(
//        x: Int,
//        z: Int,
//        randomState: RandomState
//    ): Int {
//
//        val seed =
//            getArisSeed(randomState)
//
//        val normalHeight =
//            ArisTerrain.getHeight(
//                x,
//                z,
//                seed
//            )
//
//        val plains =
//            isPlains(
//                x,
//                normalHeight,
//                z,
//                randomState
//            )
//
//        if (!plains) {
//            return normalHeight
//        }
//
//        return ArisTerrain.getPlainsHeight(
//            x,
//            z,
//            seed
//        )
//    }
//
//    override fun getBaseHeight(
//        x: Int,
//        z: Int,
//        type: Heightmap.Types,
//        level: LevelHeightAccessor,
//        randomState: RandomState
//    ): Int {
//
//        return getSurfaceHeight(
//            x,
//            z,
//            randomState
//        )
//    }
//
//    override fun getBaseColumn(
//        x: Int,
//        z: Int,
//        level: LevelHeightAccessor,
//        randomState: RandomState
//    ): NoiseColumn {
//
//        val minY =
//            level.minY
//
//        val height =
//            level.height
//
//        val states =
//            Array(height) {
//                Blocks.AIR.defaultBlockState()
//            }
//
//        val surfaceY =
//            getSurfaceHeight(
//                x,
//                z,
//                randomState
//            )
//
//        for (index in states.indices) {
//
//            val y =
//                minY + index
//
//            states[index] =
//                getBlockStateForHeight(
//                    y,
//                    surfaceY
//                )
//        }
//
//        return NoiseColumn(
//            minY,
//            states
//        )
//    }
//
//    override fun fillFromNoise(
//        blender: Blender,
//        randomState: RandomState,
//        structureManager: StructureManager,
//        chunk: ChunkAccess
//    ): CompletableFuture<ChunkAccess> {
//
//        if (!firstChunkLogged) {
//
//            firstChunkLogged = true
//
//            val arisSeed =
//                getArisSeed(randomState)
//
//            Aris.LOGGER.info("========================================")
//            Aris.LOGGER.info("ARIS TERRAIN ENGINE ACTIVE")
//            Aris.LOGGER.info("Generator: aris:aris")
//            Aris.LOGGER.info("Terrain range: Y=${ArisTerrain.MIN_HEIGHT}..${ArisTerrain.MAX_HEIGHT}")
//            Aris.LOGGER.info("Sea level: Y=${ArisTerrain.SEA_LEVEL}")
//            Aris.LOGGER.info("Aris seed: $arisSeed")
//            Aris.LOGGER.info("First terrain chunk: ${chunk.pos}")
//            Aris.LOGGER.info("========================================")
//        }
//
//        val minY =
//            chunk.minY
//
//        val maxY =
//            chunk.minY + chunk.height
//
//        for (localX in 0 until 16) {
//
//            for (localZ in 0 until 16) {
//
//                val worldX =
//                    chunk.pos.minBlockX + localX
//
//                val worldZ =
//                    chunk.pos.minBlockZ + localZ
//
//                val surfaceY =
//                    getSurfaceHeight(
//                        worldX,
//                        worldZ,
//                        randomState
//                    )
//
//                for (y in minY until maxY) {
//
//                    val state =
//                        getBlockStateForHeight(
//                            y,
//                            surfaceY
//                        )
//
//                    if (!state.isAir) {
//
//                        chunk.setBlockState(
//                            BlockPos(
//                                worldX,
//                                y,
//                                worldZ
//                            ),
//                            state,
//                            0
//                        )
//                    }
//                }
//            }
//        }
//
//        return CompletableFuture.completedFuture(
//            chunk
//        )
//    }
//
//    private fun getBlockStateForHeight(
//        y: Int,
//        surfaceY: Int
//    ): BlockState {
//
//        if (surfaceY < ArisTerrain.SEA_LEVEL) {
//
//            if (y <= surfaceY) {
//
//                return if (
//                    y >= surfaceY - 2
//                ) {
//                    Blocks.SAND.defaultBlockState()
//                } else {
//                    Blocks.STONE.defaultBlockState()
//                }
//            }
//
//            if (y <= ArisTerrain.SEA_LEVEL) {
//
//                return Blocks.WATER.defaultBlockState()
//            }
//
//            return Blocks.AIR.defaultBlockState()
//        }
//
//        if (y <= surfaceY) {
//
//            if (y == surfaceY) {
//
//                return Blocks.GRASS_BLOCK.defaultBlockState()
//            }
//
//            if (y >= surfaceY - 2) {
//
//                return Blocks.DIRT.defaultBlockState()
//            }
//
//            return Blocks.STONE.defaultBlockState()
//        }
//
//        return Blocks.AIR.defaultBlockState()
//    }
//
//    override fun applyCarvers(
//        level: WorldGenRegion,
//        seed: Long,
//        randomState: RandomState,
//        biomeManager: BiomeManager,
//        structureManager: StructureManager,
//        chunk: ChunkAccess
//    ) {
//    }
//
//    override fun buildSurface(
//        level: WorldGenRegion,
//        structureManager: StructureManager,
//        randomState: RandomState,
//        chunk: ChunkAccess
//    ) {
//    }
//
//    override fun spawnOriginalMobs(
//        level: WorldGenRegion
//    ) {
//    }
//
//    override fun addDebugScreenInfo(
//        text: MutableList<String>,
//        randomState: RandomState,
//        pos: BlockPos
//    ) {
//
//        val height =
//            getSurfaceHeight(
//                pos.x,
//                pos.z,
//                randomState
//            )
//
//        text.add("Aris Terrain")
//        text.add("Aris Height: $height")
//    }
//}