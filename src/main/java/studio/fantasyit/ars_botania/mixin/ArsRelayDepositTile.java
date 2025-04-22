package studio.fantasyit.ars_botania.mixin;

import com.hollingsworth.arsnouveau.common.block.tile.RelayDepositTile;
import com.hollingsworth.arsnouveau.common.block.tile.RelayTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import studio.fantasyit.ars_botania.api.ICustomTick;

@Mixin(value = RelayDepositTile.class,remap = false)
abstract public class ArsRelayDepositTile extends ArsRelayTile implements ICustomTick {
    public ArsRelayDepositTile(BlockEntityType<?> manaTile, BlockPos pos, BlockState state) {
        super(manaTile, pos, state);
    }

    @Redirect(method="tick",at=@At(value="INVOKE",target="Lcom/hollingsworth/arsnouveau/common/block/tile/RelayTile;tick()V"))
    public void ars_botania$tick(RelayTile instance){
        this.ars_botania$customTick();
    }
}
