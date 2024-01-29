package mod.chloeprime.tacinteractkey;

import com.mojang.logging.LogUtils;
import mod.chloeprime.tacinteractkey.client.ClientProxy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.Environment;
import org.slf4j.Logger;

@Mod("tacinteractkey")
public class TacInteractKey {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static boolean isUsingWhenHoldingGun;

    public TacInteractKey() {
        if (Environment.get().getDist() == Dist.CLIENT) {
            ClientProxy.construct();
        }
    }
}
