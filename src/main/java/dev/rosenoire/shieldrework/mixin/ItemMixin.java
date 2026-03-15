package dev.rosenoire.shieldrework.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.rosenoire.shieldrework.common.cca.ShieldComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ItemMixin {
    @WrapMethod(method = "isItemBarVisible")
    private boolean shieldRework$isItemBarVisible(ItemStack stack, Operation<Boolean> original) {
        if (stack.isOf(Items.SHIELD)) {
            return true;
        }

        return original.call(stack);
    }

    @WrapMethod(method = "getItemBarStep")
    private int shieldRework$getItemBarStep(ItemStack stack, Operation<Integer> original) {
        if (stack.isOf(Items.SHIELD)) {
            ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;

            if (clientPlayer != null){
                float progress = ShieldComponent.get(clientPlayer).currentHealthProgress();
                return MathHelper.ceil(progress * 13);
            }
        }

        return original.call(stack);
    }

    @WrapMethod(method = "getItemBarColor")
    public int shieldRework$getItemBarColor(ItemStack stack, Operation<Integer> original) {
        if (stack.isOf(Items.SHIELD)) {
            ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;

            if (clientPlayer != null) {
                float progress = ShieldComponent.get(clientPlayer).currentHealthProgress();
                return MathHelper.hsvToRgb(progress / 3.0F, 1.0F, 1.0F);
            }
        }

        return original.call(stack);
    }
}
