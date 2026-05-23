package dev.rosenoire.shieldrework.common.index;

import dev.rosenoire.shieldrework.common.ShieldRework;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface ModItemTags {
    TagKey<Item> SHIELDS = registerItemTag("shields");
    TagKey<Item> EXTRA_SHIELD_DAMAGE = registerItemTag("extra_shield_damage");

    static void register() {
    }

    static TagKey<Item> registerItemTag(String identifier) {
        return registerTag(Registries.ITEM, identifier);
    }

    static <T> TagKey<T> registerTag(ResourceKey<? extends Registry<T>> registryReference, String identifier) {
        return registerTag(registryReference, ShieldRework.id(identifier));
    }

    static <T> TagKey<T> registerTag(ResourceKey<? extends Registry<T>> registryReference, Identifier identifier) {
        return TagKey.create(registryReference, identifier);
    }
}
