package studio.fantasyit.ars_botania.mixin;

import com.hollingsworth.arsnouveau.api.source.IMultiSourceTargetProvider;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import studio.fantasyit.ars_botania.api.ICustomTick;

import java.util.ArrayList;
import java.util.Iterator;

@Mixin(value = com.hollingsworth.arsnouveau.common.block.tile.RelaySplitterTile.class, remap = false)
public abstract class ArsRelaySplitterTile extends ArsRelayTile implements IMultiSourceTargetProvider, ICustomTick {
    @Shadow private ArrayList<BlockPos> fromList;

    @Shadow private ArrayList<BlockPos> toList;

    @Shadow public abstract void createParticles(BlockPos from, BlockPos to);

    public ArsRelaySplitterTile(BlockEntityType<?> manaTile, BlockPos pos, BlockState state) {
        super(manaTile, pos, state);
    }

    @Override
    public void ars_botania$customTick() {
        if (this.level.getGameTime() % 20L == 0L && !this.toList.isEmpty() && !this.level.isClientSide && !this.disabled) {
            this._processFromList();
            this._processToList();
            this.updateBlock();
        }
    }

    public void _processFromList() {
        if (!this.fromList.isEmpty()) {
            ArrayList<BlockPos> stale = new ArrayList();
            int ratePer = this.getTransferRate() / this.fromList.size();
            Iterator var3 = this.fromList.iterator();

            BlockPos fromPos;
            while(var3.hasNext()) {
                fromPos = (BlockPos)var3.next();
                if (this.level.isLoaded(fromPos)) {
                    BlockEntity var6 = this.level.getBlockEntity(fromPos);
                    if (var6 instanceof ISourceTile fromTile) {
                        int fromRate = Math.min(ratePer, this.getTransferRate(fromTile, this));
                        if (this.transferSource(fromTile, this, fromRate) > 0) {
                            this.createParticles(fromPos, this.worldPosition);
                        }
                    } else {
                        stale.add(fromPos);
                    }
                }
            }

            var3 = stale.iterator();

            while(var3.hasNext()) {
                fromPos = (BlockPos)var3.next();
                this.fromList.remove(fromPos);
                this.updateBlock();
            }

        }
    }

    public void _processToList() {
        if (!this.toList.isEmpty()) {
            ArrayList<BlockPos> stale = new ArrayList();
            int ratePer = this.getSource() / this.toList.size();
            Iterator var3 = this.toList.iterator();

            BlockPos toPos;
            while(var3.hasNext()) {
                toPos = (BlockPos)var3.next();
                if (this.level.isLoaded(toPos)) {
                    BlockEntity var6 = this.level.getBlockEntity(toPos);
                    if (var6 instanceof ISourceTile) {
                        ISourceTile toTile = (ISourceTile)var6;
                        int var7 = this.transferSource(this, toTile, ratePer);
                        if (var7 > 0) {
                            this.createParticles(this.worldPosition, toPos);
                        }
                    } else {
                        stale.add(toPos);
                    }
                }
            }

            var3 = stale.iterator();

            while(var3.hasNext()) {
                toPos = (BlockPos)var3.next();
                this.toList.remove(toPos);
                this.updateBlock();
            }

        }
    }

}
