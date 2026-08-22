package com.grindlesstudio.aris.block

import com.grindlesstudio.aris.Aris
import net.minecraft.block.*
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

// Кастомный класс для маленького камушка
class PebbleBlock(settings: Settings) : Block(settings) {
    // Хитбокс: маленький плоский прямоугольник в центре блока (высота 2 пикселя)
    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return VoxelShapes.cuboid(0.3, 0.0, 0.3, 0.7, 0.125, 0.7)
    }
}

object ModBlocks {
    // 1. Полублок земли
    val DIRT_SLAB: Block = registerBlock(
        "dirt_slab",
        SlabBlock(AbstractBlock.Settings.copy(Blocks.DIRT))
    )

    // 2. Полублок дёрна
    val GRASS_SLAB: Block = registerBlock(
        "grass_slab",
        SlabBlock(AbstractBlock.Settings.copy(Blocks.GRASS_BLOCK))
    )

    // 3. Маленький камешек (не блокирует ходьбу игрока, без коллизии)
    val PEBBLE: Block = registerBlock(
        "pebble",
        PebbleBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque().noCollision())
    )

    private fun registerBlock(name: String, block: Block): Block {
        val id = Aris.id(name)
        val registeredBlock = Registry.register(Registries.BLOCK, id, block)
        Registry.register(Registries.ITEM, id, BlockItem(registeredBlock, Item.Settings()))
        return registeredBlock
    }

    fun registerModBlocks() {
        Aris.LOGGER.info("Регистрация блоков для " + Aris.MOD_ID)
    }
}