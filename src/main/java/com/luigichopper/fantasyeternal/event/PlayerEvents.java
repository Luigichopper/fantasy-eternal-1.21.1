package com.luigichopper.fantasyeternal.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.gen.structure.Structure;
import com.mojang.datafixers.util.Pair;

import java.util.Optional;

public class PlayerEvents {
    private static final RegistryKey<Structure> FARLANDS_TOWER_KEY = RegistryKey.of(
            RegistryKeys.STRUCTURE,
            Identifier.of("fantasy_eternal", "farlands_tower")
    );

    public static void initialize() {
        // Give compass when player first joins the server
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();

            // Debug: Always give compass for testing
            server.execute(() -> giveStartingCompass(player));

            // Uncomment this for production (only gives to new players):
            /*
            if (player.getStatHandler().getStat(net.minecraft.stat.Stats.CUSTOM.getOrCreateStat(net.minecraft.stat.Stats.PLAY_TIME)) == 0) {
                server.execute(() -> giveStartingCompass(player));
            }
            */
        });
    }

    private static void giveStartingCompass(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        // Try multiple methods to find the structure
        BlockPos towerPos = null;
        String method = "none";

        // Method 1: Use vanilla locate system
        towerPos = locateStructureUsingVanillaMethod(world, player.getBlockPos(), FARLANDS_TOWER_KEY);
        if (towerPos != null) {
            method = "vanilla locate system";
        }

        // Method 3: Last resort - create a test position (for debugging)
        if (towerPos == null) {
            // For testing: create a fake position 2000 blocks away
            towerPos = player.getBlockPos().add(2000, 0, 2000);
            method = "test position (no structure found)";

            player.sendMessage(Text.literal("⚠️ No Farlands Tower found! Creating test compass pointing 2000 blocks away.")
                    .formatted(Formatting.RED), false);
        }

        // Create the compass
        ItemStack compass = createFarlandsCompass(world, towerPos);

        // Add it to player's inventory
        if (!player.getInventory().insertStack(compass)) {
            player.dropItem(compass, false);
        }

        // Send detailed feedback
        double distance = Math.sqrt(player.getBlockPos().getSquaredDistance(towerPos));

        player.sendMessage(Text.literal("An ominous compass has been given to you.")
                .formatted(Formatting.DARK_PURPLE), false);
    }

    /**
     * Method 1: Use vanilla locate system (preferred)
     */
    private static BlockPos locateStructureUsingVanillaMethod(ServerWorld world, BlockPos playerPos, RegistryKey<Structure> structureKey) {
        try {
            var structureRegistry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);
            var structureEntry = structureRegistry.getEntry(structureKey);

            if (structureEntry.isEmpty()) {
                System.out.println("[Fantasy Eternal] Structure not found in registry: " + structureKey.getValue());
                return null;
            }

            System.out.println("[Fantasy Eternal] Found structure in registry, attempting to locate...");

            // Create a RegistryEntryList from our single entry (this is what the method expects)
            var entryList = net.minecraft.registry.entry.RegistryEntryList.of(structureEntry.get());

            // Use vanilla locate method with correct parameters
            Pair<BlockPos, ?> result = world.getChunkManager().getChunkGenerator().locateStructure(
                    world,
                    entryList,  // RegistryEntryList<Structure>
                    playerPos,
                    10000, // 10k block radius
                    false   // don't skip existing chunks
            );

            if (result != null) {
                BlockPos structurePos = result.getFirst();
                double distance = structurePos.getSquaredDistance(playerPos);

                System.out.println("[Fantasy Eternal] Structure found at: " + structurePos + " (distance: " + Math.sqrt(distance) + ")");

                // Ensure minimum distance
                if (distance >= 1000 * 1000) {
                    return structurePos;
                } else {
                    System.out.println("[Fantasy Eternal] Structure too close, skipping");
                }
            } else {
                System.out.println("[Fantasy Eternal] Vanilla locate returned null");
            }

        } catch (Exception e) {
            System.out.println("[Fantasy Eternal] Error in vanilla locate: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }



    /**
     * Creates a compass with proper lodestone tracker component data
     * Fixed version that properly points to coordinates
     */
    private static ItemStack createFarlandsCompass(ServerWorld world, BlockPos targetPos) {
        ItemStack compass = new ItemStack(Items.COMPASS);

        // Set the lodestone tracker component - the key fix is to set tracked to false
        // When tracked is false, the compass will point to the coordinates even if no lodestone exists there
        GlobalPos globalPos = GlobalPos.create(world.getRegistryKey(), targetPos);
        LodestoneTrackerComponent tracker = new LodestoneTrackerComponent(Optional.of(globalPos), false);
        compass.set(DataComponentTypes.LODESTONE_TRACKER, tracker);

        // Add custom name and styling
        compass.set(DataComponentTypes.CUSTOM_NAME,
                Text.literal("Farlands Compass").formatted(Formatting.GOLD, Formatting.BOLD));

        // Add enchantment glint
        compass.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

        System.out.println("[Fantasy Eternal] Created compass pointing to: " + targetPos + " in dimension: " + world.getRegistryKey().getValue());

        return compass;
    }
}