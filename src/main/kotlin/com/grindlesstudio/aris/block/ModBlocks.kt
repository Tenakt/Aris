package com.grindlesstudio.aris.block

import com.grindlesstudio.aris.Aris
import net.minecraft.block.*
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

// Маленький камешек
class PebbleBlock(settings: AbstractBlock.Settings) : Block(settings) {

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return VoxelShapes.cuboid(
            0.3, 0.0, 0.3,
            0.7, 0.125, 0.7
        )
    }
}

object ModBlocks {

    // =========================================================
    // DIRT SLAB
    // =========================================================

    val DIRT_SLAB: Block = registerBlock(
        "dirt_slab"
    ) { settings ->
        SlabBlock(
            settings
        )
    }

    // =========================================================
    // GRASS SLAB
    // =========================================================

    val GRASS_SLAB: Block = registerBlock(
        "grass_slab"
    ) { settings ->
        SlabBlock(
            settings
        )
    }

    // =========================================================
    // PEBBLE
    // =========================================================

    val PEBBLE: Block = registerBlock(
        "pebble"
    ) { settings ->
        PebbleBlock(
            settings
                .nonOpaque()
                .noCollision()
        )
    }

    // =========================================================
    // РЕГИСТРАЦИЯ
    // =========================================================

    private fun registerBlock(
        name: String,
        factory: (AbstractBlock.Settings) -> Block
    ): Block {

        val id = Aris.id(name)

        // Сначала создаём RegistryKey блока
        val blockKey = RegistryKey.of(
            RegistryKeys.BLOCK,
            id
        )

        // Settings сразу получает ID блока
        val settings = AbstractBlock.Settings
            .create()
            .registryKey(blockKey)

        // Создаём блок уже с известным ID
        val block = factory(settings)

        // Регистрируем блок
        Registry.register(
            Registries.BLOCK,
            id,
            block
        )

        // RegistryKey предмета
        val itemKey = RegistryKey.of(
            RegistryKeys.ITEM,
            id
        )

        // Регистрируем BlockItem
        Registry.register(
            Registries.ITEM,
            id,
            BlockItem(
                block,
                Item.Settings()
                    .registryKey(itemKey)
            )
        )

        return block
    }

    fun registerModBlocks() {
        Aris.LOGGER.info(
            "Регистрация блоков для ${Aris.MOD_ID}"
        )
    }
}