package com.luigichopper.fantasyeternal.data;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;

public class PlayerCompassAttachment {
    public static final AttachmentType<Boolean> HAS_RECEIVED_COMPASS = AttachmentRegistry.create(
            Identifier.of("fantasy_eternal", "has_received_compass"),
            builder -> builder
                    .initializer(() -> false) // Default to false (hasn't received compass)
                    .persistent(Codec.BOOL) // Save across restarts
                    .copyOnDeath() // Keep data when player respawns
    );
}