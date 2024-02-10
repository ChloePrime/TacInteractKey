package mod.chloeprime.tacinteractkey.mixin.client;

import com.tac.guns.client.handler.AimingHandler;
import mod.chloeprime.tacinteractkey.TacInteractKey;
import net.minecraftforge.client.event.InputEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AimingHandler.class, remap = false)
public class MixinTacAimingHandler {
    // This Function doesn't exist in TaC 1.16.5
//    @Inject(method = "onClickInput", at = @At("HEAD"), cancellable = true)
//    private void inject_onClickInput(InputEvent.ClickInputEvent e, CallbackInfo ci) {
//        if (TacInteractKey.isUsingWhenHoldingGun) {
//            ci.cancel();
//        }
//    }
}
