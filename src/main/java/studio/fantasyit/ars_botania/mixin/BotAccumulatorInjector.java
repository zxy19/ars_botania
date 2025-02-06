package studio.fantasyit.ars_botania.mixin;

import org.spongepowered.asm.mixin.Mixin;
import studio.fantasyit.ars_botania.Config;
import studio.fantasyit.ars_botania.api.IAccumulatorGetter;
import studio.fantasyit.ars_botania.utils.DoubleAccumulator;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntity;

import javax.annotation.Nullable;

@Mixin(value= BotaniaBlockEntity.class)
public class BotAccumulatorInjector implements IAccumulatorGetter {
    @Nullable
    public DoubleAccumulator accumulator;
    @Override
    public DoubleAccumulator getAccumulator() {
        if (this.accumulator == null)
            this.accumulator = new DoubleAccumulator(1/ Config.manaConvert);
        return this.accumulator;
    }
}
