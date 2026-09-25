package com.grindlesstudio.aris.worldgen.feature.road

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component
import net.minecraft.tags.BiomeTags
import net.minecraft.tags.BlockTags
import net.minecraft.util.ProblemReporter
import net.minecraft.util.RandomSource
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.SignBlockEntity
import net.minecraft.world.level.block.entity.SignText
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration
import net.minecraft.world.level.storage.TagValueInput
import kotlin.math.sin
import kotlin.math.sqrt

class VillageRoadFeature(
    codec: Codec<NoneFeatureConfiguration>
) : Feature<NoneFeatureConfiguration>(codec) {

    companion object {
        private const val ROAD_RADIUS = 2.2
        private const val ROAD_CENTER_RADIUS = 1.0
        private const val DISTORTION_SCALE = 0.04
        private const val DISTORTION_STRENGTH = 4.0
        private const val CLEARANCE_HEIGHT = 4
        private const val VILLAGE_SAFE_RADIUS = 40.0
        private const val LAMP_POST_CHANCE = 100
        private const val LAMP_POST_HEIGHT = 3
    }

    override fun place(
        context: FeaturePlaceContext<NoneFeatureConfiguration>
    ): Boolean {
        val level = context.level()
        val origin = context.origin()
        val random = context.random()

        val chunkPos = ChunkPos(origin)
        val seed = level.seed

        RoadGraph.initForChunk(
            level,
            chunkPos.x,
            chunkPos.z,
            seed
        )

        val segments = RoadGraph.getSegmentsForChunk(chunkPos, seed)

        if (segments.isEmpty()) {
            return false
        }

        val minX = chunkPos.minBlockX
        val minZ = chunkPos.minBlockZ
        val maxX = chunkPos.maxBlockX
        val maxZ = chunkPos.maxBlockZ

        var placedAny = false

        for ((startNode, endNode) in segments) {

            if (
                !segmentIntersectsChunk(
                    startNode,
                    endNode,
                    minX,
                    minZ,
                    maxX,
                    maxZ
                )
            ) {
                continue
            }

            for (x in minX..maxX) {
                for (z in minZ..maxZ) {

                    // Не строить дорогу внутри самой деревни,
                    // чтобы не ломать дома
                    val dxStart = (x - startNode.x).toDouble()
                    val dzStart = (z - startNode.z).toDouble()
                    val distToStart =
                        sqrt(dxStart * dxStart + dzStart * dzStart)

                    val dxEnd = (x - endNode.x).toDouble()
                    val dzEnd = (z - endNode.z).toDouble()
                    val distToEnd =
                        sqrt(dxEnd * dxEnd + dzEnd * dzEnd)

                    if (
                        distToStart < VILLAGE_SAFE_RADIUS ||
                        distToEnd < VILLAGE_SAFE_RADIUS
                    ) {
                        continue
                    }

                    val distortion =
                        sin(x * DISTORTION_SCALE) *
                                DISTORTION_STRENGTH +
                                sin(z * DISTORTION_SCALE) *
                                DISTORTION_STRENGTH

                    val distance =
                        RoadMath.distanceToSegment(
                            x.toDouble() + distortion,
                            z.toDouble(),
                            startNode.x.toDouble(),
                            startNode.z.toDouble(),
                            endNode.x.toDouble(),
                            endNode.z.toDouble()
                        )

                    if (distance > ROAD_RADIUS) {
                        continue
                    }

                    val surfaceY = findSurfaceY(level, x, z)
                    val pathPos = BlockPos(x, surfaceY, z)

                    if (!isValidRoadBiome(level, pathPos)) {
                        continue
                    }

                    if (
                        buildRoadOrBridge(
                            level,
                            pathPos,
                            distance,
                            random
                        )
                    ) {
                        clearAirAbove(level, pathPos)
                        placedAny = true

                        // Элементы на краю дороги
                        if (distance in 1.9..ROAD_RADIUS) {

                            tryPlaceSign(
                                level,
                                pathPos,
                                startNode,
                                endNode
                            )

                            tryPlaceLampPost(
                                level,
                                pathPos
                            )
                        }
                    }
                }
            }
        }

        return placedAny
    }

    private fun findSurfaceY(
        level: WorldGenLevel,
        x: Int,
        z: Int
    ): Int {
        return level.getHeight(
            Heightmap.Types.WORLD_SURFACE_WG,
            x,
            z
        ) - 1
    }

    private fun isValidRoadBiome(
        level: WorldGenLevel,
        pos: BlockPos
    ): Boolean {
        val biome = level.getBiome(pos)

        return !biome.`is`(BiomeTags.IS_DEEP_OCEAN)
    }

    private fun buildRoadOrBridge(
        level: WorldGenLevel,
        pos: BlockPos,
        distToCenter: Double,
        random: RandomSource
    ): Boolean {
        val state = level.getBlockState(pos)
        val fluid = level.getFluidState(pos)

        // Пропускаем постройки деревни
        if (isStructureBlock(state)) {
            return false
        }

        // Если здесь вода — строим мост
        if (!fluid.isEmpty || state.block == Blocks.WATER) {
            val bridgeY = maxOf(level.seaLevel, pos.y)
            val bridgePos = BlockPos(pos.x, bridgeY, pos.z)

            return buildBridge(
                level,
                bridgePos,
                distToCenter
            )
        }

        if (state.block == Blocks.BEDROCK || state.isAir) {
            return false
        }

        val blockToPlace =
            if (distToCenter < ROAD_CENTER_RADIUS) {
                when (random.nextInt(10)) {
                    0, 1 -> Blocks.MOSSY_COBBLESTONE
                    2 -> Blocks.GRAVEL
                    else -> Blocks.COBBLESTONE
                }
            } else {
                when (random.nextInt(5)) {
                    0 -> Blocks.COBBLESTONE
                    1, 2 -> Blocks.GRAVEL
                    else -> Blocks.DIRT_PATH
                }
            }

        level.setBlock(
            pos,
            blockToPlace.defaultBlockState(),
            2
        )

        return true
    }

    private fun buildBridge(
        level: WorldGenLevel,
        pos: BlockPos,
        distToCenter: Double
    ): Boolean {
        level.setBlock(
            pos,
            Blocks.OAK_PLANKS.defaultBlockState(),
            2
        )

        if (distToCenter > 1.2) {
            val fencePos = pos.above()

            if (level.getBlockState(fencePos).isAir) {
                level.setBlock(
                    fencePos,
                    Blocks.OAK_FENCE.defaultBlockState(),
                    2
                )
            }
        } else {
            val above = pos.above()

            if (level.getBlockState(above).block == Blocks.WATER) {
                level.setBlock(
                    above,
                    Blocks.AIR.defaultBlockState(),
                    2
                )
            }
        }

        return true
    }

    private fun clearAirAbove(
        level: WorldGenLevel,
        pos: BlockPos
    ) {
        // Очищаем стандартные 4 блока воздуха над полотном дороги
        for (yOffset in 1..CLEARANCE_HEIGHT) {
            clearBlock(
                level,
                pos.above(yOffset)
            )
        }

        // Убираем зависшие стволы деревьев выше 4-го блока
        var topPos = pos.above(CLEARANCE_HEIGHT + 1)

        while (level.getBlockState(topPos).`is`(BlockTags.LOGS)) {
            level.setBlock(
                topPos,
                Blocks.AIR.defaultBlockState(),
                2
            )

            topPos = topPos.above()
        }
    }

    private fun clearBlock(
        level: WorldGenLevel,
        pos: BlockPos
    ) {
        val state = level.getBlockState(pos)

        // Не ломать элементы построек деревни
        if (isStructureBlock(state)) {
            return
        }

        if (!state.isAir && state.block != Blocks.BEDROCK) {
            level.setBlock(
                pos,
                Blocks.AIR.defaultBlockState(),
                2
            )
        }
    }

    private fun isStructureBlock(
        state: BlockState
    ): Boolean {
        val block = state.block

        return state.`is`(BlockTags.PLANKS) ||
                state.`is`(BlockTags.WOODEN_STAIRS) ||
                state.`is`(BlockTags.WOODEN_SLABS) ||
                state.`is`(BlockTags.WOODEN_DOORS) ||
                state.`is`(BlockTags.WOODEN_FENCES) ||
                state.`is`(BlockTags.WALLS) ||
                state.`is`(BlockTags.WOOL) ||
                block == Blocks.OAK_SIGN ||
                block == Blocks.GLASS ||
                block == Blocks.GLASS_PANE ||
                block == Blocks.TERRACOTTA ||
                block == Blocks.WHITE_TERRACOTTA ||
                block == Blocks.BRICKS ||
                block == Blocks.CRAFTING_TABLE
    }

    /**
     * Столб с фонарём.
     *
     * Ставится только на краю дороги.
     *
     * Структура:
     *
     *      LANTERN
     *         |
     *         |
     *         |
     *      ROAD
     */
    private fun tryPlaceLampPost(
        level: WorldGenLevel,
        roadPos: BlockPos
    ) {
        val hash = roadPos.x * 31 + roadPos.z * 17

        // Редкость фонарей
        if (Math.floorMod(hash, LAMP_POST_CHANCE) != 0) {
            return
        }

        // Проверяем место для столба
        for (yOffset in 1..LAMP_POST_HEIGHT) {
            val postPos = roadPos.above(yOffset)

            if (!level.getBlockState(postPos).isAir) {
                return
            }
        }

        // Позиция фонаря
        val lanternPos =
            roadPos.above(LAMP_POST_HEIGHT + 1)

        if (!level.getBlockState(lanternPos).isAir) {
            return
        }

        // Строим вертикальный столб
        for (yOffset in 1..LAMP_POST_HEIGHT) {
            level.setBlock(
                roadPos.above(yOffset),
                Blocks.OAK_FENCE.defaultBlockState(),
                2
            )
        }

        // Ставим фонарь сверху
        level.setBlock(
            lanternPos,
            Blocks.LANTERN.defaultBlockState(),
            2
        )
    }

    private fun tryPlaceSign(
        level: WorldGenLevel,
        roadEdgePos: BlockPos,
        startNode: BlockPos,
        endNode: BlockPos
    ) {
        val hash = roadEdgePos.x * 31 + roadEdgePos.z * 17

        if (Math.floorMod(hash, 120) != 0) {
            return
        }

        val dxStart =
            (roadEdgePos.x - startNode.x).toDouble()

        val dzStart =
            (roadEdgePos.z - startNode.z).toDouble()

        val distToStart =
            sqrt(
                dxStart * dxStart +
                        dzStart * dzStart
            )

        val dxEnd =
            (roadEdgePos.x - endNode.x).toDouble()

        val dzEnd =
            (roadEdgePos.z - endNode.z).toDouble()

        val distToEnd =
            sqrt(
                dxEnd * dxEnd +
                        dzEnd * dzEnd
            )

        if (distToStart < 60.0 || distToEnd < 60.0) {
            return
        }

        val fencePos = roadEdgePos.above()
        val signPos = fencePos.above()

        if (
            !level.getBlockState(fencePos).isAir ||
            !level.getBlockState(signPos).isAir
        ) {
            return
        }

        val targetVillagePos =
            if (distToStart < distToEnd) {
                endNode
            } else {
                startNode
            }

        val distanceBlocks =
            (
                    if (distToStart < distToEnd) {
                        distToEnd
                    } else {
                        distToStart
                    }
                    ).toInt()

        level.setBlock(
            fencePos,
            Blocks.OAK_FENCE.defaultBlockState(),
            2
        )

        level.setBlock(
            signPos,
            Blocks.OAK_SIGN.defaultBlockState(),
            2
        )

        val blockEntity =
            level.getBlockEntity(signPos)

        if (blockEntity is SignBlockEntity) {
            val text = SignText()
                .setMessage(
                    0,
                    Component.literal("Деревня")
                )
                .setMessage(
                    1,
                    Component.literal("->$distanceBlocks м")
                )

            val ops =
                level.registryAccess()
                    .createSerializationContext(
                        NbtOps.INSTANCE
                    )

            val textTag =
                SignText.DIRECT_CODEC
                    .encodeStart(ops, text)
                    .result()
                    .orElse(null)

            if (textTag is CompoundTag) {
                val tag = CompoundTag()

                tag.put(
                    "front_text",
                    textTag
                )

                val valueInput =
                    TagValueInput.create(
                        ProblemReporter.DISCARDING,
                        level.registryAccess(),
                        tag
                    )

                blockEntity.loadWithComponents(
                    valueInput
                )
            }
        }
    }

    private fun segmentIntersectsChunk(
        start: BlockPos,
        end: BlockPos,
        minX: Int,
        minZ: Int,
        maxX: Int,
        maxZ: Int
    ): Boolean {
        val padding =
            ROAD_RADIUS.toInt() + 1

        val segmentMinX =
            minOf(start.x, end.x) - padding

        val segmentMaxX =
            maxOf(start.x, end.x) + padding

        val segmentMinZ =
            minOf(start.z, end.z) - padding

        val segmentMaxZ =
            maxOf(start.z, end.z) + padding

        return !(
                segmentMaxX < minX ||
                        segmentMinX > maxX ||
                        segmentMaxZ < minZ ||
                        segmentMinZ > maxZ
                )
    }
}