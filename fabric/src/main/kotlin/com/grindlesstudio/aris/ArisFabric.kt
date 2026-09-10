package com.grindlesstudio.aris

import com.grindlesstudio.aris.block.ModBlocks
import com.grindlesstudio.aris.registry.ModFeatures
import com.grindlesstudio.aris.worldgen.ArisSpawn
import com.grindlesstudio.aris.worldgen.ModBiomeSources
//import com.grindlesstudio.aris.worldgen.ModBiomeSources
//import com.grindlesstudio.aris.worldgen.ModChunkGenerators
import net.fabricmc.api.ModInitializer

class ArisFabric : ModInitializer {

    override fun onInitialize() {

        Aris.initialize()
        ModBlocks.register()
        ModFeatures.registerFabric()
        ModBiomeSources.registerFabric()
        ArisSpawn.init()

        Aris.LOGGER.info(
            "Aris Fabric initialized"
        )
    }
}