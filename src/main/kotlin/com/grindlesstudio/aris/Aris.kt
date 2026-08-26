package com.grindlesstudio.aris

import com.grindlesstudio.aris.block.ModBlocks
import com.grindlesstudio.aris.registry.ModFeatures
import com.grindlesstudio.aris.worldgen.ModBiomeSources
import com.grindlesstudio.aris.worldgen.ModChunkGenerators
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.PlacedFeature
import org.slf4j.LoggerFactory

object Aris : ModInitializer {
	const val MOD_ID: String = "aris"
	val LOGGER = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		LOGGER.info("========================================")
		LOGGER.info("Aris initialized")
		LOGGER.info("Aris terrain range: Y=-350..650")
		LOGGER.info("Aris sea level: Y=0")
		LOGGER.info("========================================")

		ModBlocks.registerModBlocks()
		ModChunkGenerators.register()
		ModFeatures.registerFeatures()
		ModBiomeSources.register()

		// Внедрение генерации камушков во все биомы Верхнего мира
		val pebblePatchKey: RegistryKey<PlacedFeature> = RegistryKey.of(
			RegistryKeys.PLACED_FEATURE,
			id("pebble_patch")
		)

		BiomeModifications.addFeature(
			BiomeSelectors.foundInOverworld(),
			GenerationStep.Feature.VEGETAL_DECORATION,
			pebblePatchKey
		)
	}

	fun id(path: String): Identifier
			= Identifier.of(MOD_ID, path)
}