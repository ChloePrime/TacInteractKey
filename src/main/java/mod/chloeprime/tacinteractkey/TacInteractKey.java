package mod.chloeprime.tacinteractkey;

import mod.chloeprime.tacinteractkey.client.TacInteractKeyClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.Environment;

@Mod("tacinteractkey")
public class TacInteractKey {

    public static boolean isUsingWhenHoldingGun;

    public TacInteractKey() {
        if (Environment.get().getDist() == Dist.CLIENT) {
            TacInteractKeyClient.construct();
        }
        FMLJavaModLoadingContext.get().getModEventBus().addListener(TacInteractKeyClient::clientSetup);
    }
}
