package studio.fantasyit.ars_botania.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import studio.fantasyit.ars_botania.api.IReceiveOrGiveSource;
import studio.fantasyit.ars_botania.data.ConvertEnums;
import studio.fantasyit.ars_botania.utils.DoubleAccumulator;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;

@Mixin(value = ManaPoolBlockEntity.class,remap = false)
public abstract class BotManaPool implements IReceiveOrGiveSource {
    @Nullable
    public DoubleAccumulator accumulator;

    public DoubleAccumulator getAccumulator(){
        if (this.accumulator == null)
            this.accumulator = new DoubleAccumulator(ConvertEnums.getWhenSourceWasPutIntoOrTakeOutFromManaContainer());
        return this.accumulator;
    }


    @Shadow public abstract boolean canReceiveManaFromBursts();

    @Shadow public abstract int getMaxMana();

    @Shadow public abstract int getCurrentMana();

    @Shadow private int mana;

    @Shadow public abstract void receiveMana(int mana);

    @Shadow private int manaCap;

    @Override
    public int getTransferRate() {
        return 10000;
    }

    @Override
    public boolean canAcceptSource() {
        return this.canReceiveManaFromBursts();
    }

    @Override
    public int getSource() {
        return this.getAccumulator().inner2outer(this.getCurrentMana());
    }

    @Override
    public int getMaxSource() {
        return this.getAccumulator().inner2outer(this.getMaxMana());
    }

    @Override
    public void setMaxSource(int i) {
        //No use here
    }

    @Override
    public int setSource(int i) {
        this.mana = this.getAccumulator().outer2inner(i);
        if (this.mana > this.getMaxMana())
            this.mana = this.getMaxMana();
        if (this.mana < 0)
            this.mana = 0;
        return this.mana;
    }

    @Override
    public int addSource(int i) {
        this.receiveMana(this.getAccumulator().accumulateAndGet(i));
        return this.getSource();
    }

    @Override
    public int removeSource(int i) {
        int cost = this.getAccumulator().takeAndGetCost(i);
        this.receiveMana(-cost);
        return 0;
    }
}
