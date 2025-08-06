package studio.fantasyit.ars_botania.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import studio.fantasyit.ars_botania.Config;
import studio.fantasyit.ars_botania.api.IReceiveOrGiveSource;
import studio.fantasyit.ars_botania.data.AccumulatorGetter;
import vazkii.botania.api.mana.ManaReceiver;

@Mixin(ManaReceiver.class)
public interface BotIManaRecvMixinReceiveOrGive extends IReceiveOrGiveSource {
    @Shadow
    boolean isFull();

    @Shadow
    int getCurrentMana();

    @Shadow
    void receiveMana(int i);

    default int getTransferRate() {
        return Config.maxRate;
    }

    default boolean canAcceptSource() {
        return !this.isFull();
    }

    default int getSource() {
        return AccumulatorGetter.getAccumulatorBotAuto(this).inner2outer(this.getCurrentMana());
    }

    default int getMaxSource() {
        return this.getCurrentMana() + this.getTransferRate();
    }

    default void setMaxSource(int var1) {
    }

    default int setSource(int var1) {
        return 0;
    }

    default int addSource(int var1) {
        int i = AccumulatorGetter.getAccumulatorBotAuto(this).accumulateAndGet(var1);
        this.receiveMana(i);
        return i;
    }

    default int removeSource(int var1){
        int i = AccumulatorGetter.getAccumulatorBotAuto(this).takeAndGetCost(var1);
        this.receiveMana(-i);
        return i;
    }
}
