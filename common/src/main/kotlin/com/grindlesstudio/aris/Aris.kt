package com.grindlesstudio.aris

import com.grindlesstudio.aris.block.ModBlocks
import com.grindlesstudio.aris.registry.ModFeatures
import com.grindlesstudio.aris.worldgen.ModBiomeSources
import com.grindlesstudio.aris.worldgen.ModChunkGenerators
import org.slf4j.LoggerFactory

object Aris {

	const val MOD_ID: String = "aris"

	val LOGGER = LoggerFactory.getLogger(MOD_ID)

	fun initialize() {
		LOGGER.info("========================================")
		LOGGER.info("Aris initialized")
		LOGGER.info("Aris terrain range: Y=-350..650")
		LOGGER.info("Aris sea level: Y=0")
		LOGGER.info("========================================")

		// Blocks
		ModBlocks.registerModBlocks()

		// World generation
		ModChunkGenerators.register()
		ModFeatures.registerFeatures()
		ModBiomeSources.register()
	}
}