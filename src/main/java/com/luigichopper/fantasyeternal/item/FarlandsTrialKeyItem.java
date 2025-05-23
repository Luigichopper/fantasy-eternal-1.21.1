// FarlandsTrialKeyItem.java
package com.luigichopper.fantasyeternal.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class FarlandsTrialKeyItem extends Item {

    public FarlandsTrialKeyItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            // Add custom right-click behavior here
            serverPlayer.sendMessage(Text.literal("The Farlands Trial Key pulses with ancient energy...")
                    .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC), false);

            // You can add key usage logic here, such as:
            // - Opening trial doors
            // - Unlocking trial chests
            // - Starting trial sequences
            // - Teleporting to trial areas

            return TypedActionResult.success(itemStack);
        }

        return TypedActionResult.pass(itemStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.literal("Used to unlock ancient trials").formatted(Formatting.DARK_PURPLE));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true; // Always has enchantment glint
    }
}