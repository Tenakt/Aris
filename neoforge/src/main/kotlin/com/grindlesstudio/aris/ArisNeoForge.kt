package com.grindlesstudio.aris

import com.grindlesstudio.aris.block.ModBlocks
import com.grindlesstudio.aris.registry.ModFeatures
//import com.grindlesstudio.aris.worldgen.ModBiomeSources
//import com.grindlesstudio.aris.worldgen.ModChunkGenerators
import net.minecraft.core.registries.BuiltInRegistries
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.registries.RegisterEvent

@Mod(Aris.MOD_ID)
class ArisNeoForge(modBus: IEventBus) {

    init {
        Aris.initialize()

        modBus.addListener(::register)

        Aris.LOGGER.info("Aris NeoForge initialized")
    }

    private fun register(event: RegisterEvent) {

        // ============================================================
        // BLOCKS
        // ============================================================

        event.register(BuiltInRegistries.BLOCK.key()) { registry ->

            registry.register(
                Aris.id("dirt_slab"),
                ModBlocks.DIRT_SLAB
            )

            registry.register(
                Aris.id("grass_slab"),
                ModBlocks.GRASS_SLAB
            )

            registry.register(
                Aris.id("sand_slab"),
                ModBlocks.SAND_SLAB
            )

            registry.register(
                Aris.id("pebble"),
                ModBlocks.PEBBLE
            )

            registry.register(
                Aris.id("taiga_leaves"),
                ModBlocks.TAIGA_LEAVES
            )
        }

        // ============================================================
        // ITEMS
        // ============================================================

        event.register(BuiltInRegistries.ITEM.key()) { registry ->

            registry.register(
                Aris.id("dirt_slab"),
                ModBlocks.DIRT_SLAB_ITEM
            )

            registry.register(
                Aris.id("grass_slab"),
                ModBlocks.GRASS_SLAB_ITEM
            )

            registry.register(
                Aris.id("sand_slab"),
                ModBlocks.SAND_SLAB_ITEM
            )

            registry.register(
                Aris.id("pebble"),
                ModBlocks.PEBBLE_ITEM
            )

            registry.register(
                Aris.id("taiga_leaves"),
                ModBlocks.TAIGA_LEAVES_ITEM
            )
        }

        // ============================================================
        // FEATURES
        // ============================================================

        event.register(BuiltInRegistries.FEATURE.key()) { registry ->

            registry.register(
                Aris.id("slope_slab"),
                ModFeatures.SLOPE_SLAB
            )
        }

        // ============================================================
        // BIOME SOURCES
        // ============================================================
//
//        event.register(BuiltInRegistries.BIOME_SOURCE.key()) { registry ->
//
//            registry.register(
//                Aris.id("aris"),
//                ModBiomeSources.ARIS_CODEC
//            )
//        }

        // ============================================================
        // CHUNK GENERATORS
        // ============================================================

//        event.register(BuiltInRegistries.CHUNK_GENERATOR.key()) { registry ->
//
//            registry.register(
//                Aris.id("aris"),
//                ModChunkGenerators.ARIS_CODEC
//            )
//        }
    }
}