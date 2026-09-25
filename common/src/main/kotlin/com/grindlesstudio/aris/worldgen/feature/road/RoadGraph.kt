package com.grindlesstudio.aris.worldgen.feature.road

import net.minecraft.core.BlockPos
import net.minecraft.tags.BiomeTags
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.levelgen.LegacyRandomSource
import net.minecraft.world.level.levelgen.WorldgenRandom
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.sqrt

object RoadGraph {

    private const val REGION_SIZE = 34
    private const val VILLAGE_SEPARATION = 8
    private const val MIN_ROAD_LENGTH = 300.0
    private const val MAX_ROAD_LENGTH = 1200.0
    private const val VILLAGE_SALT = 10387312L
    private const val MAX_CONNECTIONS_PER_VILLAGE = 2

    private val graphs =
        ConcurrentHashMap<Long, MutableList<Pair<BlockPos, BlockPos>>>()

    private val calculatedRegions =
        ConcurrentHashMap<Long, MutableSet<Pair<Int, Int>>>()

    fun initForChunk(
        level: WorldGenLevel,
        chunkX: Int,
        chunkZ: Int,
        seed: Long
    ) {
        val regionX = Math.floorDiv(chunkX, REGION_SIZE)
        val regionZ = Math.floorDiv(chunkZ, REGION_SIZE)

        val calculated =
            calculatedRegions.computeIfAbsent(seed) {
                ConcurrentHashMap.newKeySet()
            }

        for (dx in -1..1) {
            for (dz in -1..1) {
                val rKey = (regionX + dx) to (regionZ + dz)
                if (calculated.add(rKey)) {
                    buildGraphForRegion(
                        level,
                        regionX + dx,
                        regionZ + dz,
                        seed
                    )
                }
            }
        }
    }

    private fun buildGraphForRegion(
        level: WorldGenLevel,
        regionX: Int,
        regionZ: Int,
        seed: Long
    ) {
        val currentVillage =
            getVillagePosInRegion(
                regionX,
                regionZ,
                seed
            )

        if (!isValidVillageLocation(level, currentVillage)) {
            return
        }

        val validNeighbors = mutableListOf<Pair<BlockPos, Double>>()

        for (dx in -1..1) {
            for (dz in -1..1) {
                if (dx == 0 && dz == 0) continue

                val neighborVillage =
                    getVillagePosInRegion(
                        regionX + dx,
                        regionZ + dz,
                        seed
                    )

                if (!isValidVillageLocation(level, neighborVillage)) continue

                val distance =
                    sqrt(
                        currentVillage
                            .distSqr(neighborVillage)
                            .toDouble()
                    )

                if (distance in MIN_ROAD_LENGTH..MAX_ROAD_LENGTH) {
                    validNeighbors.add(neighborVillage to distance)
                }
            }
        }

        validNeighbors.sortBy { it.second }
        validNeighbors.take(MAX_CONNECTIONS_PER_VILLAGE).forEach { (neighbor, _) ->
            addSegment(seed, currentVillage, neighbor)
        }
    }

    private fun addSegment(
        seed: Long,
        start: BlockPos,
        end: BlockPos
    ) {
        val graph =
            graphs.computeIfAbsent(seed) {
                mutableListOf()
            }

        synchronized(graph) {
            if (
                graph.none {
                    (it.first == start && it.second == end) ||
                            (it.first == end && it.second == start)
                }
            ) {
                graph.add(start to end)
            }
        }
    }

    private fun isValidVillageLocation(
        level: WorldGenLevel,
        pos: BlockPos
    ): Boolean {
        val biome =
            level.getUncachedNoiseBiome(
                pos.x shr 2,
                0,
                pos.z shr 2
            )

        if (
            biome.`is`(BiomeTags.IS_OCEAN) ||
            biome.`is`(BiomeTags.IS_DEEP_OCEAN) ||
            biome.`is`(BiomeTags.IS_RIVER)
        ) {
            return false
        }

        return true
    }

    private fun getVillagePosInRegion(
        regionX: Int,
        regionZ: Int,
        seed: Long
    ): BlockPos {
        // Вычисляем координаты деревни ровно по алгоритму майнкрафта!
        val random = WorldgenRandom(LegacyRandomSource(0L))
        random.setLargeFeatureWithSalt(seed, regionX, regionZ, VILLAGE_SALT.toInt())

        val maxRandom = REGION_SIZE - VILLAGE_SEPARATION // 34 - 8 = 26
        val randomX = random.nextInt(maxRandom)
        val randomZ = random.nextInt(maxRandom)

        val chunkX = regionX * REGION_SIZE + randomX
        val chunkZ = regionZ * REGION_SIZE + randomZ

        return BlockPos(
            (chunkX shl 4) + 8,
            0,
            (chunkZ shl 4) + 8
        )
    }

    fun getSegmentsForChunk(
        chunkPos: ChunkPos,
        seed: Long
    ): List<Pair<BlockPos, BlockPos>> {
        val allSegments = graphs[seed] ?: return emptyList()

        val minX = chunkPos.minBlockX - 10
        val maxX = chunkPos.maxBlockX + 10
        val minZ = chunkPos.minBlockZ - 10
        val maxZ = chunkPos.maxBlockZ + 10

        synchronized(allSegments) {
            return allSegments.filter { (start, end) ->
                val sMinX = minOf(start.x, end.x)
                val sMaxX = maxOf(start.x, end.x)
                val sMinZ = minOf(start.z, end.z)
                val sMaxZ = maxOf(start.z, end.z)

                maxX >= sMinX && minX <= sMaxX && maxZ >= sMinZ && minZ <= sMaxZ
            }
        }
    }

    fun clear(seed: Long) {
        graphs.remove(seed)
        calculatedRegions.remove(seed)
    }
}