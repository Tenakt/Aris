package com.grindlesstudio.aris.mixin

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.LeavesBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.world.level.block.Block::class)
abstract class LeavesBlockMixin {

    @Inject(
        method = ["getCollisionShape"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun disableLeafCollision(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext,
        cir: CallbackInfoReturnable<VoxelShape>
    ) {
        if (state.block is LeavesBlock) {
            cir.returnValue = Shapes.empty()
        }
    }
}