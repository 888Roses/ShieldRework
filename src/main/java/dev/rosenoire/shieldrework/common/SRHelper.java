package dev.rosenoire.shieldrework.common;

import com.google.common.collect.ImmutableList;
import dev.rosenoire.shieldrework.common.index.ModDataComponentTypes;
import net.minecraft.core.component.DataComponentMap;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.IntFunction;

public interface SRHelper {
    float AXE_BREAKING_DAMAGE = 999999f;

    static Optional<UUID> optionalOwner(@NonNull DataComponentMap map) {
        return Optional.ofNullable(map.get(ModDataComponentTypes.OWNER));
    }

    static <T> T[] make(int count, IntFunction<T[]> arrayFactory, Function<Integer, T> factory) {
        var builder = ImmutableList.<T>builderWithExpectedSize(count);
        for (var i = 0; i < count; i++) builder.add(factory.apply(i));
        return builder.build().toArray(arrayFactory);
    }
}
