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
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.GenerationStep

class ArisFabric : ModInitializer {

    override fun onInitialize() {

        Aris.initialize()
        ModBlocks.register()
        ModFeatures.register()
        ModBiomeSources.register()
        ArisSkillNetworking.init()

        Aris.LOGGER.info(
            "Aris Fabric initialized"
        )

        BiomeModifications.addFeature(
            BiomeSelectors.foundInOverworld(),
            GenerationStep.Decoration.VEGETAL_DECORATION,
            ResourceKey.create(Registries.PLACED_FEATURE, Aris.id("village_road"))
        )

        ServerWorldEvents.LOAD.register { _, level ->
            RoadSpawnHandler.onLevelLoad(level)
        }
    }
}