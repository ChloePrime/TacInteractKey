package mod.chloeprime.tacinteractkey.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.item.transition.TimelessGunItem;
import cpw.mods.modlauncher.api.INameMappingService;
import mod.chloeprime.tacinteractkey.mixin.client.GuiAccessor;
import mod.chloeprime.tacinteractkey.util.InheritanceChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class InteractHintHud {
    private static final Minecraft MC = Minecraft.getInstance();

    public static final IIngameOverlay INTERACT_HINT_ELEMENT = OverlayRegistry.registerOverlayAbove(ForgeIngameGui.CROSSHAIR_ELEMENT, "Interact Hint", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (!MC.options.hideGui)
        {
            renderInteractHint(gui, screenWidth, screenHeight, partialTick, poseStack);
        }
    });

    public static boolean isInteractable(@Nullable HitResult hit) {
        if (hit == null) {
            return false;
        }
        return switch (hit.getType()) {
            case MISS -> false;
            case BLOCK -> Optional.ofNullable(MC.level)
                    .map(level -> {
                        var state = MC.level.getBlockState(((BlockHitResult) hit).getBlockPos());
                        return BLOCK_INHERITANCE_CHECKER.isInherited(state.getBlock().getClass());
                    })
                    .orElse(false);
            case ENTITY -> {
                var hitEntity = ((EntityHitResult) hit).getEntity();
                yield ENTITY_INHERITANCE_CHECKER.isInherited(hitEntity.getClass());
            }
        };
    }

    private static final InheritanceChecker<Block> BLOCK_INHERITANCE_CHECKER = new InheritanceChecker<>(
            Block.class,
            ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, "m_6227_"),
            BlockState.class, Level.class, BlockPos.class, Player.class, InteractionHand.class, BlockHitResult.class
    );

    private static final InheritanceChecker<Entity> ENTITY_INHERITANCE_CHECKER = new InheritanceChecker<>(
            Entity.class,
            ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, "m_6096_"),
            Player.class, InteractionHand.class
    );

    private static void renderInteractHint(ForgeIngameGui gui, int screenW, int screenH, float partialTick, PoseStack pStack) {
        boolean isHoldingGun = Optional.ofNullable(MC.player)
                .map(pl -> pl.getMainHandItem().getItem() instanceof TimelessGunItem)
                .orElse(false);
        if (!isHoldingGun) {
            return;
        }

        boolean isTargetInteractable = isInteractable(MC.hitResult);
        if (!isTargetInteractable) {
            return;
        }

        pStack.pushPose();
        pStack.translate(screenW / 2.0, screenH / 2.0, 0);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        var font = MC.font;
        var text = new TranslatableComponent(
                "interact.hint",
                firstLetterToUpperCase(MC.options.keySwapOffhand.getTranslatedKeyMessage().getString()),
                HINT_TEXT_ARG1
        ).withStyle(ChatFormatting.WHITE);

        ((GuiAccessor)gui).invokeDrawBackdrop(pStack, font, -4, font.width(text), 0xFFFFFFFF);
        font.drawShadow(pStack, text, -font.width(text) / 2F, 10F, 0xFFFFFFFF);
        RenderSystem.disableBlend();
        pStack.popPose();
    }

    private static final Component HINT_TEXT_ARG1 = new TranslatableComponent("interact.hint.type.interact");

    private static String firstLetterToUpperCase(String s) {
        if (s.isEmpty()) {
            return s;
        }
        var firstChar = s.charAt(0);
        if (firstChar >= 0x80 || Character.isUpperCase(firstChar)) {
            return s;
        }
        if (s.length() == 1) {
            return String.valueOf(Character.toUpperCase(firstChar));
        }
        var chars = s.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static void init() {}
}
