package mod.chloeprime.tacinteractkey.mixin.client;

import net.minecraft.block.Block;
import net.minecraft.block.StairsBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StairsBlock.class)
public interface StairBlockAccessor {
    @Invoker(remap = false)
    Block invokeGetModelBlock();
}
