package mod.chloeprime.tacinteractkey.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec INSTANCE;
    public static final ForgeConfigSpec.BooleanValue DISABLE_HUD_TEXT;
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        DISABLE_HUD_TEXT = builder
                .comment("Disable the hud hint (\"[F] interact\") in center of the screen \nwhen pointing at something that is interactable")
                .define("disable_hud_text", false);
        INSTANCE = builder.build();
    }

    private ClientConfig() {
    }
}
