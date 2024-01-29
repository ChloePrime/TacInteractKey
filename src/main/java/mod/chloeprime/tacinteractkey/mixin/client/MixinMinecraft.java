package mod.chloeprime.tacinteractkey.mixin.client;

import com.tac.guns.client.handler.ReloadHandler;
import com.tac.guns.item.transition.TimelessGunItem;
import mod.chloeprime.tacinteractkey.TacInteractKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void inject_checkSwapHandAndMakeRightClickRequest(CallbackInfo ci) {
        if (player == null || player.isSpectator() || !(player.getMainHandItem().getItem() instanceof TimelessGunItem)) {
            return;
        }
        try {
            TacInteractKey.isUsingWhenHoldingGun = true;
            while(this.options.keySwapOffhand.consumeClick()) {
                if (ReloadHandler.get().isReloading()) {
                    continue;
                }
                startUseItem();
            }
        } finally {
            TacInteractKey.isUsingWhenHoldingGun = false;
        }
    }

    @Shadow @Final public Options options;
    @Shadow @Nullable public LocalPlayer player;
    @Shadow protected abstract void startUseItem();
}
