package mod.chloeprime.tacinteractkey.mixin.client;

import com.tac.guns.client.handler.ReloadHandler;
import com.tac.guns.item.transition.TimelessGunItem;
import mod.chloeprime.tacinteractkey.TacInteractKey;
import mod.chloeprime.tacinteractkey.client.TacInteractKeyClient;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void inject_checkSwapHandAndMakeRightClickRequest(CallbackInfo ci) {
        if (player == null || player.isSpectator() || !(player.getMainHandItem().getItem() instanceof TimelessGunItem)) {
            tacInteractKey$swallowClickRecords();
            return;
        }
        if (ReloadHandler.get().isReloading()) {
            tacInteractKey$swallowClickRecords();
            return;
        }
        try {
            TacInteractKey.isUsingWhenHoldingGun = true;
            while(TacInteractKeyClient.KEY_GUN_INTERACT.consumeClick()) {
                startUseItem();
            }
        } finally {
            TacInteractKey.isUsingWhenHoldingGun = false;
        }
    }

    @Unique
    @SuppressWarnings("StatementWithEmptyBody")
    private static void tacInteractKey$swallowClickRecords() {
        while (TacInteractKeyClient.KEY_GUN_INTERACT.consumeClick()) {}
    }

    @Shadow @Final public GameSettings options;
    @Shadow @Nullable public ClientPlayerEntity player;
    @Shadow protected abstract void startUseItem();
}
