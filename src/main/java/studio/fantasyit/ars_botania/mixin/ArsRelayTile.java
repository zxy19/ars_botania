package studio.fantasyit.ars_botania.mixin;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import studio.fantasyit.ars_botania.api.ICustomTick;
import studio.fantasyit.ars_botania.api.IReceiveOrGiveSource;

@Mixin(value = com.hollingsworth.arsnouveau.common.block.tile.RelayTile.class, remap = false)
public abstract class ArsRelayTile extends AbstractSourceMachine implements ITickable, ICustomTick {
    @Shadow
    public abstract int getMaxDistance();

    @Shadow
    private BlockPos toPos;


    @Shadow
    public abstract boolean setTakeFrom(BlockPos pos);

    @Shadow public boolean disabled;

    @Shadow private BlockPos fromPos;

    @Shadow public abstract boolean setSendTo(BlockPos pos);

    public ArsRelayTile(BlockEntityType<?> manaTile, BlockPos pos, BlockState state) {
        super(manaTile, pos, state);
    }

    @Inject(at = @At("RETURN"), method = "setSendTo", cancellable = true)
    public void ars_botania$setSendTo(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            if (this.level != null && !(BlockUtil.distanceFrom(pos, this.worldPosition) > (double) this.getMaxDistance()) && !pos.equals(this.getBlockPos()) && this.level.getBlockEntity(pos) instanceof IReceiveOrGiveSource) {
                this.toPos = pos;
                this.updateBlock();
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "onFinishedConnectionLast")
    public void ars_botania$onFinishedConnectionLast(BlockPos storedPos, LivingEntity storedEntity, Player playerEntity, CallbackInfo ci) {
        if (this.level != null && storedPos != null && !storedPos.equals(this.getBlockPos()) && !(this.level.getBlockEntity(storedPos) instanceof com.hollingsworth.arsnouveau.common.block.tile.RelayTile) && this.level.getBlockEntity(storedPos) instanceof IReceiveOrGiveSource) {
            if (this.setTakeFrom(storedPos.immutable())) {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.take", DominionWand.getPosString(storedPos)));
            } else {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.fail"));
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "onFinishedConnectionFirst", cancellable = true)
    public void ars_botania$onFinishedConnectionFirst(BlockPos storedPos, LivingEntity storedEntity, Player playerEntity, CallbackInfo ci) {
        if (storedPos != null && !this.level.isClientSide && !storedPos.equals(this.getBlockPos()) && this.level.getBlockEntity(storedPos) instanceof IReceiveOrGiveSource) {
            if (this.setSendTo(storedPos.immutable())) {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.send", new Object[]{DominionWand.getPosString(storedPos)}));
                ParticleUtil.beam(storedPos, this.worldPosition, this.level);
            } else {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.fail"));
            }

        }
    }

    public void ars_botania$customTick() {
        if (!this.level.isClientSide && !this.disabled) {
            if (this.level.getGameTime() % 20L == 0L) {
                ISourceTile toTile;
                if (this.fromPos != null && this.level.isLoaded(this.fromPos)) {
                    if (!(this.level.getBlockEntity(this.fromPos) instanceof ISourceTile)) {
                        this.fromPos = null;
                        this.updateBlock();
                        return;
                    }

                    BlockEntity var2 = this.level.getBlockEntity(this.fromPos);
                    if (var2 instanceof ISourceTile) {
                        toTile = (ISourceTile)var2;
                        if (this.transferSource(toTile, this) > 0) {
                            this.updateBlock();
                            ParticleUtil.spawnFollowProjectile(this.level, this.fromPos, this.worldPosition, this.getColor());
                        }
                    }
                }

                if (this.toPos != null && this.level.isLoaded(this.toPos)) {
                    if (!(this.level.getBlockEntity(this.toPos) instanceof ISourceTile)) {
                        this.toPos = null;
                        this.updateBlock();
                        return;
                    }

                    toTile = (ISourceTile)this.level.getBlockEntity(this.toPos);
                    if (this.transferSource(this, toTile) > 0) {
                        ParticleUtil.spawnFollowProjectile(this.level, this.worldPosition, this.toPos, this.getColor());
                    }
                }

            }
        }
    }
}
