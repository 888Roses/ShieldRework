package dev.rosenoire.shieldrework.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.rosenoire.shieldrework.common.cca.ShieldComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ItemMixin {
    @WrapMethod(method = "isBarVisible")
    private boolean shieldRework$isItemBarVisible(ItemStack stack, Operation<Boolean> original) {
        if (stack.is(Items.SHIELD)) {
            LocalPlayer clientPlayer = Minecraft.getInstance().player;

            if (clientPlayer != null && ShieldComponent.get(clientPlayer).currentHealthProgress() >= 1) {
                return false;
            }

            return true;
        }

        return original.call(stack);
    }

    @WrapMethod(method = "getBarWidth")
    private int shieldRework$getItemBarStep(ItemStack stack, Operation<Integer> original) {
        if (stack.is(Items.SHIELD)) {
            LocalPlayer clientPlayer = Minecraft.getInstance().player;

            if (clientPlayer != null) {
                float progress = ShieldComponent.get(clientPlayer).currentHealthProgress();
                return Mth.ceil(progress * 13);
            }
        }

        return original.call(stack);
    }

    @WrapMethod(method = "getBarColor")
    public int shieldRework$getItemBarColor(ItemStack stack, Operation<Integer> original) {
        if (stack.is(Items.SHIELD)) {
            LocalPlayer clientPlayer = Minecraft.getInstance().player;

            if (clientPlayer != null) {
                float progress = ShieldComponent.get(clientPlayer).currentHealthProgress();
                return Mth.hsvToRgb(progress / 3.0F, 1.0F, 1.0F);
            }
        }

        return original.call(stack);
    }
}
