package com.grindlesstudio.aris.worldgen

import com.grindlesstudio.aris.worldgen.terrain.ArisChunkGenerator
import net.minecraft.registry.Registry
import net.minecraft.registry.Registries
import com.grindlesstudio.aris.Aris

object ModChunkGenerators {

    fun register() {
        Registry.register(
            Registries.CHUNK_GENERATOR,
            Aris.id("aris"),
            ArisChunkGenerator.CODEC
        )

        Aris.LOGGER.info("Aris chunk generator registered")
    }
}