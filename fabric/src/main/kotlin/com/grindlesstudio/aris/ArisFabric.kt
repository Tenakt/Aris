package com.grindlesstudio.aris

import com.grindlesstudio.aris.block.ModBlocks
import com.grindlesstudio.aris.registry.ModFeatures
import com.grindlesstudio.aris.skill.ArisSkillNetworking
import com.grindlesstudio.aris.worldgen.ModBiomeSources
import com.grindlesstudio.aris.worldgen.feature.road.RoadSpawnHandler
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.GenerationStep

class ArisFabric : ModInitializer {

    override fun onInitialize() {

        Aris.initialize()

        // ============================================================
        // BLOCKS
        // ============================================================
        Registry.register(BuiltInRegistries.BLOCK, Aris.id("dirt_slab"), ModBlocks.DIRT_SLAB)
        Registry.register(BuiltInRegistries.BLOCK, Aris.id("grass_slab"), ModBlocks.GRASS_SLAB)
        Registry.register(BuiltInRegistries.BLOCK, Aris.id("sand_slab"), ModBlocks.SAND_SLAB)
        Registry.register(BuiltInRegistries.BLOCK, Aris.id("pebble"), ModBlocks.PEBBLE)
        Registry.register(BuiltInRegistries.BLOCK, Aris.id("taiga_leaves"), ModBlocks.TAIGA_LEAVES)
        Registry.register(BuiltInRegistries.BLOCK, Aris.id("stick"), ModBlocks.STICK)

        // ============================================================
        // ITEMS
        // ============================================================
        Registry.register(BuiltInRegistries.ITEM, Aris.id("dirt_slab"), ModBlocks.DIRT_SLAB_ITEM)
        Registry.register(BuiltInRegistries.ITEM, Aris.id("grass_slab"), ModBlocks.GRASS_SLAB_ITEM)
        Registry.register(BuiltInRegistries.ITEM, Aris.id("sand_slab"), ModBlocks.SAND_SLAB_ITEM)
        Registry.register(BuiltInRegistries.ITEM, Aris.id("pebble"), ModBlocks.PEBBLE_ITEM)
        Registry.register(BuiltInRegistries.ITEM, Aris.id("taiga_leaves"), ModBlocks.TAIGA_LEAVES_ITEM)
        Registry.register(BuiltInRegistries.ITEM, Aris.id("stick"), ModBlocks.STICK_ITEM)

        // ============================================================
        // FEATURES
        // ============================================================
        Registry.register(BuiltInRegistries.FEATURE, Aris.id("slope_slab"), ModFeatures.SLOPE_SLAB)
        Registry.register(BuiltInRegistries.FEATURE, Aris.id("village_road"), ModFeatures.VILLAGE_ROAD)

        // ============================================================
        // BIOME SOURCES
        // ============================================================
        Registry.register(BuiltInRegistries.BIOME_SOURCE, Aris.id("aris"), ModBiomeSources.ARIS_CODEC)

        // ============================================================
        // SKILL NETWORKING & EVENTS
        // ============================================================
        ArisSkillNetworking.init()

        BiomeModifications.addFeature(
            BiomeSelectors.foundInOverworld(),
            GenerationStep.Decoration.VEGETAL_DECORATION,
            ResourceKey.create(Registries.PLACED_FEATURE, Aris.id("village_road"))
        )

        ServerWorldEvents.LOAD.register { _, level ->
            RoadSpawnHandler.onLevelLoad(level)
        }

        Aris.LOGGER.info("Aris Fabric initialized")
    }
}