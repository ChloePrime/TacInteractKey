package mod.chloeprime.tacinteractkey.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(IngameGui.class)
public interface GuiAccessor {
    @Invoker
    void invokeDrawBackdrop(MatrixStack pMatrixStack, FontRenderer pRenderer, int pHeightOffset, int pMessageWidth, int pColor);
}
