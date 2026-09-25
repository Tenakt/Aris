package com.grindlesstudio.aris.worldgen

import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.SurfaceRules
import net.minecraft.world.level.levelgen.VerticalAnchor
import net.minecraft.world.level.levelgen.placement.CaveSurface

object ArisSurfaceRules {
    // Блоки
    private val BEDROCK = makeStateRule(Blocks.BEDROCK)
    private val SAND = makeStateRule(Blocks.SAND)
    private val SANDSTONE = makeStateRule(Blocks.SANDSTONE)
    private val GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK)
    private val DIRT = makeStateRule(Blocks.DIRT)
    private val STONE = makeStateRule(Blocks.STONE)

    private fun makeStateRule(block: Block): SurfaceRules.RuleSource {
        return SurfaceRules.state(block.defaultBlockState())
    }

    fun makeRules(): SurfaceRules.RuleSource {
        // Условия проверки
        val isAtBedrock = SurfaceRules.verticalGradient(
            "bedrock_floor",
            VerticalAnchor.bottom(),
            VerticalAnchor.aboveBottom(5)
        )
        val isSteep = SurfaceRules.steep()
        val isFloor = SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR)
        val isSurfaceDepth = SurfaceRules.stoneDepthCheck(0, true, CaveSurface.FLOOR)
        val isAbovePreliminarySurface = SurfaceRules.abovePreliminarySurface()

        // Правило покрытия для Пустыни (Песок на поверхности, Песчаник под ним)
        val desertSurface = SurfaceRules.ifTrue(
            isSurfaceDepth,
            SurfaceRules.sequence(
                SurfaceRules.ifTrue(isFloor, SAND),
                SANDSTONE
            )
        )

        // Дефолтное правило для Равнин/Тайги и прочих (Трава на поверхности, Земля под ней)
        val defaultSurface = SurfaceRules.ifTrue(
            isFloor,
            SurfaceRules.sequence(
                SurfaceRules.ifTrue(isAbovePreliminarySurface, GRASS_BLOCK),
                DIRT
            )
        )

        return SurfaceRules.sequence(
            // 1. Бедрок на самом дне (Y <= -59)
            SurfaceRules.ifTrue(isAtBedrock, BEDROCK),

            // 2. Скалы и крутые склоны
            SurfaceRules.ifTrue(isSteep, STONE),

            // 3. Проверка ванильного биома пустыни (Biomes.DESERT)
            SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.DESERT), desertSurface),

            // 4. Все остальные биомы (Равнины, Тайга и т.д.) уходят в дефолт
            defaultSurface,

            // 5. Запасной блок
            STONE
        )
    }
}