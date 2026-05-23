package dev.rosenoire.shieldrework.common.index;

import dev.rosenoire.shieldrework.common.ShieldRework;
import java.util.UUID;
import java.util.function.UnaryOperator;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

@SuppressWarnings("unchecked")
public interface ModDataComponentTypes {
    DataComponentType<UUID> OWNER = register("owner", builder -> builder.persistent(UUIDUtil.AUTHLIB_CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC));

    static void register() {}

    static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return (DataComponentType<T>) Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ShieldRework.id(id), ((DataComponentType.Builder)builderOperator.apply(DataComponentType.builder())).build());
    }
}
