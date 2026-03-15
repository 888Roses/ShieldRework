package dev.rosenoire.shieldrework.common.index;

import dev.rosenoire.shieldrework.common.ShieldRework;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Uuids;

import java.util.UUID;
import java.util.function.UnaryOperator;

@SuppressWarnings("unchecked")
public interface ModDataComponentTypes {
    ComponentType<UUID> OWNER = register("owner", builder -> builder.codec(Uuids.CODEC).packetCodec(Uuids.PACKET_CODEC));

    static void register() {}

    static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return (ComponentType<T>) Registry.register(Registries.DATA_COMPONENT_TYPE, ShieldRework.id(id), ((ComponentType.Builder)builderOperator.apply(ComponentType.builder())).build());
    }
}
