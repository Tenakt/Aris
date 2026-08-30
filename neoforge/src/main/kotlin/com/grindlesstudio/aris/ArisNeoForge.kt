package com.grindlesstudio.aris

import com.grindlesstudio.aris.block.ModBlocks
import com.grindlesstudio.aris.registry.ModFeatures
import com.grindlesstudio.aris.worldgen.ModBiomeSources
import com.grindlesstudio.aris.worldgen.ModChunkGenerators
import net.neoforged.fml.common.Mod

@Mod(Aris.MOD_ID)
class ArisNeoForge {

    init {
        Aris.initialize()

        // Инициализируем регистрацию блоков
        ModBlocks.toString()

        ModFeatures.registerFeatures()
        ModBiomeSources.register()
        ModChunkGenerators.register()

        Aris.LOGGER.info("Aris NeoForge initialized")
    }
}