package com.grindlesstudio.aris.block

import com.grindlesstudio.aris.Aris
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape


class PebbleBlock(
    properties: BlockBehaviour.Properties
) : Block(properties) {

    override fun canSurvive(
        state: BlockState,
        level: LevelReader,
        pos: BlockPos
    ): Boolean {
        val downPos = pos.below()

        return level.getBlockState(downPos)
            .isFaceSturdy(level, downPos, Direction.UP)
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {

        val baseShape = Block.box(
            4.8,
            0.0,
            4.8,
            11.2,
            2.0,
            11.2
        )

        val offset = state.getOffset(pos)

        return baseShape.move(
            offset.x,
            offset.y,
            offset.z
        )
    }
}


object ModBlocks {

    // ============================================================
    // BLOCK KEYS
    // ============================================================

    val DIRT_SLAB_KEY: ResourceKey<Block> =
        ResourceKey.create(
            BuiltInRegistries.BLOCK.key(),
            Aris.id("dirt_slab")
        )

    val GRASS_SLAB_KEY: ResourceKey<Block> =
        ResourceKey.create(
            BuiltInRegistries.BLOCK.key(),
            Aris.id("grass_slab")
        )

    val SAND_SLAB_KEY: ResourceKey<Block> =
        ResourceKey.create(
            BuiltInRegistries.BLOCK.key(),
            Aris.id("sand_slab")
        )

    val PEBBLE_KEY: ResourceKey<Block> =
        ResourceKey.create(
            BuiltInRegistries.BLOCK.key(),
            Aris.id("pebble")
        )

    val TAIGA_LEAVES_KEY: ResourceKey<Block> =
        ResourceKey.create(
            BuiltInRegistries.BLOCK.key(),
            Aris.id("taiga_leaves")
        )


    // ============================================================
    // ITEM KEYS
    // ============================================================

    val DIRT_SLAB_ITEM_KEY: ResourceKey<Item> =
        ResourceKey.create(
            BuiltInRegistries.ITEM.key(),
            Aris.id("dirt_slab")
        )

    val GRASS_SLAB_ITEM_KEY: ResourceKey<Item> =
        ResourceKey.create(
            BuiltInRegistries.ITEM.key(),
            Aris.id("grass_slab")
        )

    val SAND_SLAB_ITEM_KEY: ResourceKey<Item> =
        ResourceKey.create(
            BuiltInRegistries.ITEM.key(),
            Aris.id("sand_slab")
        )

    val PEBBLE_ITEM_KEY: ResourceKey<Item> =
        ResourceKey.create(
            BuiltInRegistries.ITEM.key(),
            Aris.id("pebble")
        )

    val TAIGA_LEAVES_ITEM_KEY: ResourceKey<Item> =
        ResourceKey.create(
            BuiltInRegistries.ITEM.key(),
            Aris.id("taiga_leaves")
        )


    // ============================================================
    // BLOCKS
    // ============================================================
    //
    // IMPORTANT:
    // Blocks are lazy because NeoForge registries are frozen
    // when the mod constructor is created.
    //
    // The Block is created only when RegisterEvent actually
    // requests it.
    //

    val DIRT_SLAB: Block by lazy(LazyThreadSafetyMode.NONE) {
        SlabBlock(
            BlockBehaviour.Properties
                .ofFullCopy(Blocks.DIRT)
                .setId(DIRT_SLAB_KEY)
        )
    }

    val GRASS_SLAB: Block by lazy(LazyThreadSafetyMode.NONE) {
        SlabBlock(
            BlockBehaviour.Properties
                .ofFullCopy(Blocks.GRASS_BLOCK)
                .setId(GRASS_SLAB_KEY)
        )
    }

    val SAND_SLAB: Block by lazy(LazyThreadSafetyMode.NONE) {
        SlabBlock(
            BlockBehaviour.Properties
                .ofFullCopy(Blocks.SAND)
                .setId(SAND_SLAB_KEY)
        )
    }

    val PEBBLE: Block by lazy(LazyThreadSafetyMode.NONE) {
        PebbleBlock(
            BlockBehaviour.Properties
                .ofFullCopy(Blocks.STONE)
                .setId(PEBBLE_KEY)
                .noOcclusion()
                .noCollision()
                .offsetType(BlockBehaviour.OffsetType.XZ)
        )
    }

    val TAIGA_LEAVES: Block by lazy(LazyThreadSafetyMode.NONE) {
        ArisLeavesBlock(
            BlockBehaviour.Properties
                .ofFullCopy(Blocks.SPRUCE_LEAVES)
                .setId(TAIGA_LEAVES_KEY)
        )
    }


    // ============================================================
    // BLOCK ITEMS
    // ============================================================

    val DIRT_SLAB_ITEM: Item by lazy(LazyThreadSafetyMode.NONE) {
        BlockItem(
            DIRT_SLAB,
            Item.Properties()
                .setId(DIRT_SLAB_ITEM_KEY)
        )
    }

    val GRASS_SLAB_ITEM: Item by lazy(LazyThreadSafetyMode.NONE) {
        BlockItem(
            GRASS_SLAB,
            Item.Properties()
                .setId(GRASS_SLAB_ITEM_KEY)
        )
    }

    val SAND_SLAB_ITEM: Item by lazy(LazyThreadSafetyMode.NONE) {
        BlockItem(
            SAND_SLAB,
            Item.Properties()
                .setId(SAND_SLAB_ITEM_KEY)
        )
    }

    val PEBBLE_ITEM: Item by lazy(LazyThreadSafetyMode.NONE) {
        BlockItem(
            PEBBLE,
            Item.Properties()
                .setId(PEBBLE_ITEM_KEY)
        )
    }

    val TAIGA_LEAVES_ITEM: Item by lazy(LazyThreadSafetyMode.NONE) {
        BlockItem(
            TAIGA_LEAVES,
            Item.Properties()
                .setId(TAIGA_LEAVES_ITEM_KEY)
        )
    }


    // ============================================================
    // FABRIC REGISTRATION
    // ============================================================

    fun register() {

        // ------------------------------------------------------------
        // BLOCKS
        // ------------------------------------------------------------

        Registry.register(
            BuiltInRegistries.BLOCK,
            DIRT_SLAB_KEY,
            DIRT_SLAB
        )

        Registry.register(
            BuiltInRegistries.BLOCK,
            GRASS_SLAB_KEY,
            GRASS_SLAB
        )

        Registry.register(
            BuiltInRegistries.BLOCK,
            SAND_SLAB_KEY,
            SAND_SLAB
        )

        Registry.register(
            BuiltInRegistries.BLOCK,
            PEBBLE_KEY,
            PEBBLE
        )

        Registry.register(
            BuiltInRegistries.BLOCK,
            TAIGA_LEAVES_KEY,
            TAIGA_LEAVES
        )


        // ------------------------------------------------------------
        // ITEMS
        // ------------------------------------------------------------

        Registry.register(
            BuiltInRegistries.ITEM,
            DIRT_SLAB_ITEM_KEY,
            DIRT_SLAB_ITEM
        )

        Registry.register(
            BuiltInRegistries.ITEM,
            GRASS_SLAB_ITEM_KEY,
            GRASS_SLAB_ITEM
        )

        Registry.register(
            BuiltInRegistries.ITEM,
            SAND_SLAB_ITEM_KEY,
            SAND_SLAB_ITEM
        )

        Registry.register(
            BuiltInRegistries.ITEM,
            PEBBLE_ITEM_KEY,
            PEBBLE_ITEM
        )

        Registry.register(
            BuiltInRegistries.ITEM,
            TAIGA_LEAVES_ITEM_KEY,
            TAIGA_LEAVES_ITEM
        )

        Aris.LOGGER.info(
            "Aris blocks and block items registered"
        )
    }
}