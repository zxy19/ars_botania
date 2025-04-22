package studio.fantasyit.ars_botania.mixin;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.ITickableBlock;
import com.hollingsworth.arsnouveau.common.block.Relay;
import com.hollingsworth.arsnouveau.common.block.tile.RelayCollectorTile;
import com.hollingsworth.arsnouveau.common.block.tile.RelayDepositTile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import studio.fantasyit.ars_botania.api.ICustomTick;

@Mixin(Relay.class)
public abstract class ArsRelayBlock implements ITickableBlock {
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return ITickableBlock.createTickerHelper(type, type, (l, pos, s, te) -> {
            if (te instanceof RelayDepositTile || te instanceof RelayCollectorTile)
                ((ITickable) te).tick(l, s, pos);
            if (te instanceof ICustomTick ict) {
                ict.ars_botania$customTick();
            }
        });
    }
}
