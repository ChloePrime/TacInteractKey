package mod.chloeprime.tacinteractkey.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Gui.class)
public interface GuiAccessor {
    @Invoker
    void invokeDrawBackdrop(PoseStack pPoseStack, Font pFont, int pHeightOffset, int pMessageWidth, int pColor);
}
