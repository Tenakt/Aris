package com.grindlesstudio.aris

import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object Aris : ModInitializer {
	const val MOD_ID: String = "aris"

	private val LOGGER = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!")
	}

	fun id(path: String): Identifier
		= Identifier.of(MOD_ID, path)
}
