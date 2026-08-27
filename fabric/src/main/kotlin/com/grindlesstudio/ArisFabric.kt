package com.grindlesstudio.aris.fabric

import com.grindlesstudio.aris.Aris
import net.fabricmc.api.ModInitializer

class ArisFabric : ModInitializer {

    override fun onInitialize() {
        Aris.initialize()

        Aris.LOGGER.info(
            "Aris Fabric initialized"
        )
    }
}