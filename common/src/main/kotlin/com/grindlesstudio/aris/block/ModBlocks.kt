package com.grindlesstudio.aris.block

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
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.WorldView

// ============================================================
// Маленький камешек
// ============================================================

class PebbleBlock(settings: Settings) : Block(settings) {

    // Проверка: камушек стоит только над твердой гранью блока
    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val downPos = pos.down()
        return world.getBlockState(downPos).isSideSolidFullSquare(world, downPos, Direction.UP)
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        val baseShape = Block.createCuboidShape(4.8, 0.0, 4.8, 11.2, 2.0, 11.2)

        // В 1.21.11 getModelOffset принимает только pos
        val offset = state.getModelOffset(pos)
        return baseShape.offset(offset.x, offset.y, offset.z)
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

    val SAND_SLAB: Block = registerBlock(
        name = "sand_slab",
        settings = { key ->
            AbstractBlock.Settings
                .copy(Blocks.SAND)
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
                .offset(AbstractBlock.OffsetType.XZ) // <--- Включает случайное смещение для 1.21.11
        },
        factory = { settings ->
            PebbleBlock(settings)
        }
    )

    val TAIGA_LEAVES: Block = registerBlock(
        name = "taiga_leaves",
        settings = { key ->
            AbstractBlock.Settings
                .copy(Blocks.SPRUCE_LEAVES)
                .registryKey(key)
        },
        factory = { settings ->
            ArisLeavesBlock(settings)
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

        val blockKey = RegistryKey.of(RegistryKeys.BLOCK, id)
        val blockSettings = settings(blockKey)
        val block = factory(blockSettings)

        Registry.register(Registries.BLOCK, id, block)

        val itemKey = RegistryKey.of(RegistryKeys.ITEM, id)
        Registry.register(
            Registries.ITEM,
            id,
            BlockItem(block, Item.Settings().registryKey(itemKey))
        )

        Aris.LOGGER.info("Зарегистрирован блок: $id")
        return block
    }

    fun registerModBlocks() {
        Aris.LOGGER.info("Регистрация блоков для ${Aris.MOD_ID}")
    }
}