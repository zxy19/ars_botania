package studio.fantasyit.ars_botania.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import studio.fantasyit.ars_botania.Config;
import studio.fantasyit.ars_botania.utils.DoubleAccumulator;

import java.util.concurrent.ConcurrentHashMap;

public class AccumulatorGetter {
    protected static ConcurrentHashMap<BlockPos, DoubleAccumulator> accumulatorMap = new ConcurrentHashMap<>();
    protected static DoubleAccumulator commonBot = new DoubleAccumulator(ConvertEnums.getWhenSourceWasPutIntoOrTakeOutFromManaContainer());

    public static DoubleAccumulator getAccumulatorBotAuto(Object target) {
        if (target instanceof BlockEntity be)
            return getAccumulatorBot(be.getBlockPos());
        else if (target instanceof BlockPos pos)
            return getAccumulatorBot(pos);
        return commonBot;
    }

    public static DoubleAccumulator getAccumulatorBot(BlockPos pos) {
        return accumulatorMap.computeIfAbsent(pos, (p) -> new DoubleAccumulator(ConvertEnums.getWhenSourceWasPutIntoOrTakeOutFromManaContainer()));
    }
}
