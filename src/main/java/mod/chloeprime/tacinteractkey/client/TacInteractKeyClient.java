package mod.chloeprime.tacinteractkey.client;

import com.tac.guns.client.GunConflictContext;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public class TacInteractKeyClient
{
    public static final KeyBinding KEY_GUN_INTERACT = new KeyBinding(
            "key.tacinteractkey.interact", GunConflictContext.IN_GAME_HOLDING_WEAPON,
            InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_F, "key.categories.inventory"
    );

    public static void construct()
    {
        InteractHintHud.init();
    }

    public static void clientSetup(FMLClientSetupEvent e)
    {
        ClientRegistry.registerKeyBinding(KEY_GUN_INTERACT);
    }
}
