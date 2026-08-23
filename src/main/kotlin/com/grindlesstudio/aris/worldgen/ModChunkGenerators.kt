package com.grindlesstudio.aris.worldgen

import com.grindlesstudio.aris.Aris
import com.grindlesstudio.aris.worldgen.terrain.ArisChunkGenerator
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

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