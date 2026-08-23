package com.grindlesstudio.aris.block

import com.grindlesstudio.aris.Aris
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.ShapeContext
import net.minecraft.block.SlabBlock
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

// ============================================================
// Маленький камешек
// ============================================================

class PebbleBlock(settings: Settings) : Block(settings) {

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


// ============================================================
// Регистрация блоков Aris
// ============================================================

object ModBlocks {

    // --------------------------------------------------------
    // Полублок земли
    // --------------------------------------------------------

    val DIRT_SLAB: Block = registerBlock(
        name = "dirt_slab",
        settings = { key ->
            AbstractBlock.Settings
                .copy(Blocks.DIRT)
                .registryKey(key)
        },
        factory = { settings ->
            SlabBlock(settings)
        }
    )


    // --------------------------------------------------------
    // Полублок дёрна
    // --------------------------------------------------------

    val GRASS_SLAB: Block = registerBlock(
        name = "grass_slab",
        settings = { key ->
            AbstractBlock.Settings
                .copy(Blocks.GRASS_BLOCK)
                .registryKey(key)
        },
        factory = { settings ->
            SlabBlock(settings)
        }
    )


    // --------------------------------------------------------
    // Маленький камешек
    // --------------------------------------------------------

    val PEBBLE: Block = registerBlock(
        name = "pebble",
        settings = { key ->
            AbstractBlock.Settings
                .copy(Blocks.STONE)
                .registryKey(key)
                .nonOpaque()
                .noCollision()
        },
        factory = { settings ->
            PebbleBlock(settings)
        }
    )


    // ========================================================
    // Общая регистрация блока + BlockItem
    // ========================================================

    private fun registerBlock(
        name: String,
        settings: (RegistryKey<Block>) -> AbstractBlock.Settings,
        factory: (AbstractBlock.Settings) -> Block
    ): Block {

        val id = Aris.id(name)

        // ----------------------------------------------------
        // Сначала создаём RegistryKey блока
        // ----------------------------------------------------

        val blockKey = RegistryKey.of(
            RegistryKeys.BLOCK,
            id
        )

        // ----------------------------------------------------
        // Создаём Settings уже с RegistryKey
        //
        // Это важно для Minecraft 1.21.11.
        // Без этого SlabBlock может вызвать:
        //
        // NullPointerException: Block id not set
        // ----------------------------------------------------

        val blockSettings = settings(blockKey)

        // ----------------------------------------------------
        // Создаём сам блок
        // ----------------------------------------------------

        val block = factory(blockSettings)

        // ----------------------------------------------------
        // Регистрируем блок
        // ----------------------------------------------------

        Registry.register(
            Registries.BLOCK,
            id,
            block
        )

        // ----------------------------------------------------
        // Создаём RegistryKey предмета
        // ----------------------------------------------------

        val itemKey = RegistryKey.of(
            RegistryKeys.ITEM,
            id
        )

        // ----------------------------------------------------
        // Регистрируем BlockItem
        // ----------------------------------------------------

        Registry.register(
            Registries.ITEM,
            id,
            BlockItem(
                block,
                Item.Settings()
                    .registryKey(itemKey)
            )
        )

        Aris.LOGGER.info(
            "Зарегистрирован блок: $id"
        )

        return block
    }


    // ========================================================
    // Вызов регистрации
    // ========================================================

    fun registerModBlocks() {
        Aris.LOGGER.info(
            "Регистрация блоков для ${Aris.MOD_ID}"
        )
    }
}