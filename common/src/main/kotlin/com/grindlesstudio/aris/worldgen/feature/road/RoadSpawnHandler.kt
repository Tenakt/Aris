package com.grindlesstudio.aris.worldgen.feature.road

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.storage.LevelData
import net.minecraft.world.level.levelgen.Heightmap
object RoadSpawnHandler {

    fun onLevelLoad(level: ServerLevel) {
        // Только верхний мир
        if (level.dimension() != ServerLevel.OVERWORLD) {
            return
        }

        setSpawnOnNearestRoad(level)
    }

    private fun setSpawnOnNearestRoad(level: ServerLevel) {
        val seed = level.seed
        val chunkPos = ChunkPos(0, 0)

        // Инициализируем граф дорог
        RoadGraph.initForChunk(
            level,
            chunkPos.x,
            chunkPos.z,
            seed
        )

        val segments = RoadGraph.getSegmentsForChunk(
            chunkPos,
            seed
        )

        if (segments.isEmpty()) {
            return
        }

        // Ищем ближайший к центру мира участок дороги
        val bestSegment = segments.minByOrNull { segment ->
            val start = segment.first
            val end = segment.second

            val midX = (start.x + end.x) / 2.0
            val midZ = (start.z + end.z) / 2.0

            midX * midX + midZ * midZ
        } ?: return

        val start = bestSegment.first
        val end = bestSegment.second

        // Середина дороги
        val spawnX = (start.x + end.x) / 2
        val spawnZ = (start.z + end.z) / 2

        // Высота поверхности
        val surfaceY = level.getHeight(
            Heightmap.Types.WORLD_SURFACE,
            spawnX,
            spawnZ
        )

        val spawnPos = BlockPos(
            spawnX,
            surfaceY,
            spawnZ
        )

        // Minecraft 1.21.11:
        // setDefaultSpawnPos() больше нет.
        level.setRespawnData(
            LevelData.RespawnData.of(
                level.dimension(),
                spawnPos,
                0.0f,
                0.0f
            )
        )
    }
}