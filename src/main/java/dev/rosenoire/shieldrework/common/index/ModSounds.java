package dev.rosenoire.shieldrework.common.index;

import dev.rosenoire.shieldrework.common.ShieldRework;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public interface ModSounds {
    SoundEvent SHIELD_DAMAGED = registerSound("shield_damaged");

    static void register() {}

    static SoundEvent registerSound(Identifier identifier) {
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    static SoundEvent registerSound(String identifier) {
        return registerSound(ShieldRework.id(identifier));
    }
}
