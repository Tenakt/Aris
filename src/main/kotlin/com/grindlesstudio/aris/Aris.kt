package com.grindlesstudio.aris

import com.grindlesstudio.aris.block.ModBlocks
import com.grindlesstudio.aris.worldgen.ModChunkGenerators
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object Aris : ModInitializer {
	const val MOD_ID: String = "aris"
	val LOGGER = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		LOGGER.info("Hello Fabric world!")

		ModBlocks.registerModBlocks()
		ModChunkGenerators.register()
	}

	fun id(path: String): Identifier
			= Identifier.of(MOD_ID, path)
}