package com.grindlesstudio.aris

import com.grindlesstudio.aris.block.ModBlocks
import com.mojang.logging.LogUtils
import net.minecraft.resources.Identifier
import org.slf4j.Logger

object Aris {

    const val MOD_ID = "aris"
    const val MOD_NAME = "Aris"
    const val MOD_VERSION = "1.0.0"

    val LOGGER: Logger = LogUtils.getLogger()

    fun id(path: String): Identifier {
        return Identifier.fromNamespaceAndPath(MOD_ID, path)
    }

    fun initialize() {
        ModBlocks.register()

        LOGGER.info("$MOD_NAME $MOD_VERSION initialized")
    }
}