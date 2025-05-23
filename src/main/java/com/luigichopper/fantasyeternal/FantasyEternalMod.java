package com.luigichopper.fantasyeternal;

import net.fabricmc.api.ModInitializer;
import com.luigichopper.fantasyeternal.init.ModItems;
import com.luigichopper.fantasyeternal.init.ModDataComponents;
import com.luigichopper.fantasyeternal.event.PlayerEvents;

public class FantasyEternalMod implements ModInitializer {
	public static final String MOD_ID = "fantasy_eternal";

	@Override
	public void onInitialize() {
		ModDataComponents.initialize();
		ModItems.initialize();
		PlayerEvents.initialize(); // Initialize the compass-giving system
	}
}