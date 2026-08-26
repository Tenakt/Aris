package com.grindlesstudio.aris.mixin

import net.minecraft.block.BlockState
import net.minecraft.block.LeavesBlock
import net.minecraft.block.ShapeContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.block.AbstractBlock::class)
abstract class LeavesBlockMixin {

    @Inject(
        method = ["getCollisionShape"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun disableLeafCollision(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext,
        cir: CallbackInfoReturnable<VoxelShape>
    ) {
        if (state.block is LeavesBlock) {
            cir.returnValue = VoxelShapes.empty()
        }
    }
}