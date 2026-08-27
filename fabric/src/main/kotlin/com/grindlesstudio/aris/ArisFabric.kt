package com.grindlesstudio.aris

import net.fabricmc.api.ModInitializer

class ArisFabric : ModInitializer {

    override fun onInitialize() {
        Aris.initialize()

        Aris.LOGGER.info(
            "Aris Fabric initialized"
        )
    }
}