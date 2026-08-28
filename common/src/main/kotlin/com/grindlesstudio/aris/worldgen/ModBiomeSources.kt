package com.grindlesstudio.aris.worldgen

import com.grindlesstudio.aris.Aris
import com.grindlesstudio.aris.worldgen.biome.ArisBiomeSource
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object ModBiomeSources {

    fun register() {

        Registry.register(
            Registries.BIOME_SOURCE,
            Aris.id("aris"),
            ArisBiomeSource.CODEC
        )

        Aris.LOGGER.info(
            "Aris biome source registered"
        )
    }
}