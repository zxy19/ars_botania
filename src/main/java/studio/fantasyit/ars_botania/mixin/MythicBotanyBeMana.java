package studio.fantasyit.ars_botania.mixin;

import mythicbotany.base.BlockEntityMana;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import studio.fantasyit.ars_botania.Config;
import studio.fantasyit.ars_botania.api.IAccumulatorGetter;
import studio.fantasyit.ars_botania.api.IReceiveOrGiveSource;
import studio.fantasyit.ars_botania.utils.DoubleAccumulator;

import javax.annotation.Nullable;

@Mixin(value = BlockEntityMana.class, remap = false)
abstract public class MythicBotanyBeMana implements IAccumulatorGetter {
    @Nullable
    public DoubleAccumulator accumulator;

    @Override
    public DoubleAccumulator getAccumulator() {
        if (this.accumulator == null)
            this.accumulator = new DoubleAccumulator(1 / Config.manaConvert);
        return this.accumulator;
    }
}
