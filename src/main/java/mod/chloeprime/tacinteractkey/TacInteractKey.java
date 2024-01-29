package mod.chloeprime.tacinteractkey;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod("tacinteractkey")
public class TacInteractKey {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static boolean isUsingWhenHoldingGun;

    public TacInteractKey() {
    }
}
