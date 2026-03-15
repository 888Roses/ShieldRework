package dev.rosenoire.shieldrework.common.index;

import dev.rosenoire.shieldrework.common.ShieldRework;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface ModItemTags {
    TagKey<Item> SHIELDS = registerItemTag("shields");
    TagKey<Item> EXTRA_SHIELD_DAMAGE = registerItemTag("extra_shield_damage");

    static void register() {
    }

    static TagKey<Item> registerItemTag(String identifier) {
        return registerTag(RegistryKeys.ITEM, identifier);
    }

    static <T> TagKey<T> registerTag(RegistryKey<? extends Registry<T>> registryReference, String identifier) {
        return registerTag(registryReference, ShieldRework.id(identifier));
    }

    static <T> TagKey<T> registerTag(RegistryKey<? extends Registry<T>> registryReference, Identifier identifier) {
        return TagKey.of(registryReference, identifier);
    }
}
