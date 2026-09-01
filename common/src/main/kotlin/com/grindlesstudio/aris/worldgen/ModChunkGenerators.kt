package com.grindlesstudio.aris.worldgen

import com.grindlesstudio.aris.Aris
import com.grindlesstudio.aris.worldgen.terrain.ArisChunkGenerator
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries

object ModChunkGenerators {

    val ARIS_CODEC = ArisChunkGenerator.CODEC

    fun registerFabric() {
        Registry.register(
            BuiltInRegistries.CHUNK_GENERATOR,
            Aris.id("aris"),
            ARIS_CODEC
        )

        Aris.LOGGER.info("Aris chunk generator registered")
    }
}