package mod.chloeprime.tacinteractkey.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class TacInteractKeyClient {
    public static final KeyMapping KEY_GUN_INTERACT = new KeyMapping(
            "key.tacinteractkey.interact", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, InputConstants.KEY_F, "key.categories.inventory"
    );

    public static void construct() {
        InteractHintHud.init();
    }

    public static void clientSetup(FMLClientSetupEvent e) {
        ClientRegistry.registerKeyBinding(KEY_GUN_INTERACT);
    }
}
