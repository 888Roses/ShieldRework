package dev.rosenoire.shieldrework.common.index;

import dev.rosenoire.shieldrework.common.ShieldRework;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public interface ModSounds {
    SoundEvent SHIELD_DAMAGED = registerSound("shield_damaged");

    static void register() {}

    static SoundEvent registerSound(Identifier identifier) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, SoundEvent.createVariableRangeEvent(identifier));
    }

    static SoundEvent registerSound(String identifier) {
        return registerSound(ShieldRework.id(identifier));
    }
}
