package com.luigichopper.fantasyeternal.init;

import com.luigichopper.fantasyeternal.FantasyEternalMod;
import com.luigichopper.fantasyeternal.item.FarlandsTrialKeyItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    // Register the Farlands Trial Key item
    public static final Item FARLANDS_TRIAL_KEY = registerItem("farlands_trial_key",
            new FarlandsTrialKeyItem(new Item.Settings().maxCount(1)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(FantasyEternalMod.MOD_ID, name), item);
    }

    public static void initialize() {
        // Register items to creative inventory
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(FARLANDS_TRIAL_KEY);
        });
    }
}