package com.grindlesstudio.aris.mixins

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.LeavesBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(BlockBehaviour::class)
abstract class LeavesBlockMixin {

    @Inject(
        method = ["getCollisionShape"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun disableLeafCollision(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext,
        cir: CallbackInfoReturnable<VoxelShape>
    ) {
        if (state.block is LeavesBlock) {
            cir.returnValue = Shapes.empty()
        }
    }
}