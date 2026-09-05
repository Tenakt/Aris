package com.grindlesstudio.aris.worldgen

import com.grindlesstudio.aris.Aris
import com.grindlesstudio.aris.worldgen.biome.ArisBiomeSource
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries

object ModBiomeSources {

    val ARIS_CODEC = ArisBiomeSource.CODEC

    fun registerFabric() {
        Registry.register(
            BuiltInRegistries.BIOME_SOURCE,
            Aris.id("aris"),
            ARIS_CODEC
        )

        Aris.LOGGER.info("Aris biome source registered")
    }
}