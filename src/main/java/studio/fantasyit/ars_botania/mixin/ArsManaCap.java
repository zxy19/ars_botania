package studio.fantasyit.ars_botania.mixin;

import com.google.common.collect.Iterables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import studio.fantasyit.ars_botania.ArsBotania;
import studio.fantasyit.ars_botania.Config;
import studio.fantasyit.ars_botania.api.IArsManaCap;
import studio.fantasyit.ars_botania.data.ConvertEnums;
import studio.fantasyit.ars_botania.utils.DoubleAccumulator;
import vazkii.botania.api.mana.ManaItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;

@Mixin(value = com.hollingsworth.arsnouveau.common.capability.ManaCap.class, remap = false)
public abstract class ArsManaCap implements IArsManaCap {

    @Nullable
    public DoubleAccumulator accumulator;

    public DoubleAccumulator getAccumulator() {
        if (this.accumulator == null)
            this.accumulator = new DoubleAccumulator(ConvertEnums.getWhenPlayerCoseBotaniaManaAndWillBeConvertedToPlayerSource());
        return this.accumulator;
    }

    @Nullable
    public DoubleAccumulator storeAccumulator;

    public DoubleAccumulator getStoreAccumulator() {
        if (this.storeAccumulator == null)
            this.storeAccumulator = new DoubleAccumulator(ConvertEnums.getWhenPlayerUsePlayerSourceToFillInMana());
        return this.storeAccumulator;
    }

    @Unique
    Player ars_botania$player;

    @Override
    public void setPlayer(Player player) {
        this.ars_botania$player = player;
    }

    @Override
    public Player getPlayer() {
        return ars_botania$player;
    }

    @Shadow
    private int maxMana;

    @Shadow
    private double mana;

    @Shadow
    public abstract double setMana(double mana);

    @Unique
    private double t_mana = 0;

    @Override
    public double ars_botania$getRawMana() {
        return this.mana;
    }

    @Override
    public int ars_botania$getRawMaxMana() {
        return this.maxMana;
    }

    @Inject(at = @At("RETURN"), method = "getCurrentMana", cancellable = true)
    public void ars_botania$getMana(CallbackInfoReturnable<Double> cir) {
        if (!Config.playerManaConvertEnable) return;
        if (ars_botania$player == null) return;
        ManaItemHandler inst = ManaItemHandler.instance();
        List<ItemStack> items = inst.getManaItems(ars_botania$player);
        List<ItemStack> acc = inst.getManaAccesories(ars_botania$player);
        double manaReceived = 0;

        for (ItemStack stackInSlot : Iterables.concat(items, acc)) {
            if (stackInSlot.is(ArsBotania.FAKE_MANA_ITEM.get())) continue;
            ManaItem manaItem = XplatAbstractions.INSTANCE.findManaItem(stackInSlot);
            if (manaItem == null) continue;
            if (manaItem.canExportManaToItem(ars_botania$player.getMainHandItem()) && manaItem.getMana() > 0) {
                manaReceived += manaItem.getMana();
            }

        }
        cir.setReturnValue(cir.getReturnValue() + getAccumulator().inner2outer(manaReceived));
    }

    @Inject(at = @At("RETURN"), method = "getMaxMana", cancellable = true)
    public void ars_botania$getMaxMana(CallbackInfoReturnable<Integer> cir) {
        if (!Config.playerManaConvertEnable) return;
        if (ars_botania$player == null) return;
        ManaItemHandler inst = ManaItemHandler.instance();
        List<ItemStack> items = inst.getManaItems(ars_botania$player);
        List<ItemStack> acc = inst.getManaAccesories(ars_botania$player);
        int manaReceived = 0;

        for (ItemStack stackInSlot : Iterables.concat(items, acc)) {
            if (stackInSlot.is(ArsBotania.FAKE_MANA_ITEM.get())) continue;
            ManaItem manaItem = XplatAbstractions.INSTANCE.findManaItem(stackInSlot);
            if (manaItem == null) continue;
            if (manaItem.canExportManaToItem(ars_botania$player.getMainHandItem()) && manaItem.getMana() > 0) {
                manaReceived += manaItem.getMaxMana();
            }
        }
        cir.setReturnValue(cir.getReturnValue() + getAccumulator().inner2outer(manaReceived));
    }

    @ModifyVariable(method = "addMana", at = @At(value = "HEAD"), argsOnly = true)
    public double ars_botania$addMana(double manaToAdd) {
        if (!Config.playerManaConvertEnable) return manaToAdd;
        if (ars_botania$player == null) return manaToAdd;
        t_mana = this.mana;
        if (manaToAdd > this.maxMana - this.mana) {
            if (Config.playerManaRecoveryChargeItem) {
                double extraMana = manaToAdd - (this.maxMana - this.mana);
                int realMana = getStoreAccumulator().accumulateAndGet((int) extraMana);
                ManaItemHandler.instance().dispatchMana(
                        ArsBotania.FAKE_MANA_ITEM.get().getDefaultInstance(),
                        ars_botania$player,
                        realMana,
                        true
                );
            }
            return this.maxMana - this.mana;
        }
        return manaToAdd;
    }

    @ModifyVariable(method = "removeMana", at = @At(value = "HEAD"), argsOnly = true)
    public double ars_botania$removeMana(double manaToRemove) {
        if (!Config.playerManaConvertEnable) return manaToRemove;
        if (ars_botania$player == null) return manaToRemove;
        t_mana = this.mana;
        if (manaToRemove > this.mana) {
            double extraMana = manaToRemove - this.mana;
            int realMana = getAccumulator().takeAndGetCost(extraMana);

            ManaItemHandler.instance().requestMana(
                    ArsBotania.FAKE_MANA_ITEM.get().getDefaultInstance(),
                    ars_botania$player,
                    realMana,
                    true
            );
            return this.mana;
        }
        return manaToRemove;
    }

    @Inject(method = "addMana", at = @At(value = "RETURN"))
    public void ars_botania$addMana_setMana(double manaToAdd, CallbackInfoReturnable<Double> cir) {
        if (!Config.playerManaConvertEnable) return;
        this.setMana(t_mana + manaToAdd);
    }

    @Inject(method = "removeMana", at = @At(value = "RETURN"))
    public void ars_botania$removeMana_setMana(double manaToRemove, CallbackInfoReturnable<Double> cir) {
        if (!Config.playerManaConvertEnable) return;
        this.setMana(t_mana - manaToRemove);
    }


    @Inject(at = @At("RETURN"), method = "serializeNBT*", cancellable = true)
    public void serializeNBT(CallbackInfoReturnable<CompoundTag> tagRet) {
        if (!Config.playerManaConvertEnable) return;
        CompoundTag returnValue = tagRet.getReturnValue();
        returnValue.putInt("max", this.maxMana);
        returnValue.putDouble("current", this.mana);
        tagRet.setReturnValue(returnValue);
    }
}
