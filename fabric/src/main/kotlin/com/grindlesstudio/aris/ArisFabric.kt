package com.grindlesstudio.aris

import com.grindlesstudio.aris.registry.ModFeatures
import com.grindlesstudio.aris.worldgen.ModBiomeSources
import com.grindlesstudio.aris.worldgen.ModChunkGenerators
import net.fabricmc.api.ModInitializer

class ArisFabric : ModInitializer {

    override fun onInitialize() {

        Aris.initialize()

        // ----------------------------------------------------
        // Features
        // ----------------------------------------------------

        ModFeatures.registerFabric()

        // ----------------------------------------------------
        // Biome Source
        // ----------------------------------------------------

        ModBiomeSources.registerFabric()

        // ----------------------------------------------------
        // Chunk Generator
        // ----------------------------------------------------

        ModChunkGenerators.registerFabric()

        Aris.LOGGER.info(
            "Aris Fabric initialized"
        )
    }
}
