package mod.chloeprime.tacinteractkey.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tac.guns.client.handler.ReloadHandler;
import com.tac.guns.item.TransitionalTypes.TimelessGunItem;
import cpw.mods.modlauncher.api.INameMappingService;
import mod.chloeprime.tacinteractkey.mixin.client.GuiAccessor;
import mod.chloeprime.tacinteractkey.mixin.client.StairBlockAccessor;
import mod.chloeprime.tacinteractkey.util.InheritanceChecker;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.IExtensibleEnum;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class InteractHintHud
{
    public enum Type implements IExtensibleEnum
    {
        INTERACT(new TranslationTextComponent("interact.hint.type.interact")),
        DRIVE_TANK(new TranslationTextComponent("interact.hint.type.drive.tank"));

        @SuppressWarnings("unused")
        public static Type create(String name, TextComponent text)
        {
            throw new IllegalStateException("Enum not extended");
        }

        public TextComponent getDisplayText()
        {
            return text;
        }

        Type(TextComponent text)
        {
            this.text = text;
        }

        private final TextComponent text;
    }

    private static final Minecraft MC = Minecraft.getInstance();

    @SubscribeEvent(receiveCanceled = true)
    public static void onPreCrossHair(RenderGameOverlayEvent.Pre event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS && !MC.options.hideGui && !ClientConfig.DISABLE_HUD_TEXT.get())
            renderInteractHint(
                    MC.gui,
                    MC.getWindow().getGuiScaledWidth(),
                    MC.getWindow().getGuiScaledHeight(),
                    event.getPartialTicks(),
                    event.getMatrixStack()
            );
    }

    public static boolean isInteractable(@Nullable RayTraceResult hit)
    {
        if (hit == null)
        {
            return false;
        }
        switch (hit.getType())
        {
            case BLOCK:
                return Optional.ofNullable(MC.level)
                               .map(level ->
                                    {
                                        Block block = MC.level.getBlockState(
                                                ((BlockRayTraceResult) hit).getBlockPos()).getBlock();
                                        return block instanceof StairBlockAccessor
                                                ? BLOCK_INHERITANCE_CHECKER.isInherited(
                                                ((StairBlockAccessor) block).invokeGetModelBlock().getClass())
                                                : BLOCK_INHERITANCE_CHECKER.isInherited(block.getClass());
                                    })
                               .orElse(false);
            case ENTITY:
                Entity hitEntity = ((EntityRayTraceResult) hit).getEntity();
                return hitEntity instanceof MobEntity
                        ? MOB_INHERITANCE_CHECKER.isInherited(((MobEntity) hitEntity).getClass())
                        : ENTITY_INHERITANCE_CHECKER.isInherited(hitEntity.getClass());
            default:
                return false;
        }
    }

    private static Type getInteractType(@Nullable RayTraceResult hitResult)
    {
        if (!(hitResult instanceof EntityRayTraceResult))
        {
            return Type.INTERACT;
        }

        Entity target = ((EntityRayTraceResult) hitResult).getEntity();
        return target instanceof PigEntity && ((PigEntity) target).isSaddled()
                ? Type.DRIVE_TANK
                : Type.INTERACT;
    }

    private static final InheritanceChecker<AbstractBlock> BLOCK_INHERITANCE_CHECKER = new InheritanceChecker<>(
            AbstractBlock.class,
            ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, "func_225533_a_"),
            BlockState.class, World.class, BlockPos.class, PlayerEntity.class, Hand.class, BlockRayTraceResult.class
    );

    private static final InheritanceChecker<Entity> ENTITY_INHERITANCE_CHECKER = new InheritanceChecker<>(
            Entity.class,
            ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, "func_184230_a"),
            PlayerEntity.class, Hand.class
    );

    private static final InheritanceChecker<MobEntity> MOB_INHERITANCE_CHECKER = new InheritanceChecker<>(
            MobEntity.class,
            ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, "func_230254_b_"),
            PlayerEntity.class, Hand.class
    );

    @SuppressWarnings("unused")
    private static void renderInteractHint(IngameGui gui, int screenW, int screenH, float partialTick, MatrixStack pStack)
    {
        boolean isHoldingGun = Optional.ofNullable(MC.player)
                                       .map(pl -> pl.getMainHandItem().getItem() instanceof TimelessGunItem)
                                       .orElse(false);
        if (!isHoldingGun || ReloadHandler.get().isReloading())
        {
            return;
        }

        boolean isTargetInteractable = isInteractable(MC.hitResult);
        if (!isTargetInteractable)
        {
            return;
        }

        pStack.pushPose();
        pStack.translate(screenW / 2.0, screenH / 2.0, 0);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        FontRenderer font = MC.font;
        Type type = getInteractType(MC.hitResult);
        ITextComponent text = new TranslationTextComponent(
                "interact.hint",
                firstLetterToUpperCase(TacInteractKeyClient.KEY_GUN_INTERACT.getTranslatedKeyMessage().getString()),
                type.getDisplayText()
        ).withStyle(TextFormatting.GRAY);
        // WHITE with full alpha is too outstanding

        ((GuiAccessor) gui).invokeDrawBackdrop(pStack, font, -4, font.width(text), 0xFFFFFFF0);
        font.drawShadow(pStack, text, -font.width(text) / 2F, 10F, 0xFFFFFFF0);
        RenderSystem.disableBlend();
        pStack.popPose();
    }

    private static String firstLetterToUpperCase(String s)
    {
        if (s.isEmpty())
        {
            return s;
        }
        char firstChar = s.charAt(0);
        if (firstChar >= 0x80 || Character.isUpperCase(firstChar))
        {
            return s;
        }
        if (s.length() == 1)
        {
            return String.valueOf(Character.toUpperCase(firstChar));
        }
        char[] chars = s.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static void init()
    {
    }
}
