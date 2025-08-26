package studio.fantasyit.ars_botania.mixin;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import studio.fantasyit.ars_botania.Config;
import studio.fantasyit.ars_botania.api.IReceiveOrGiveMana;
import studio.fantasyit.ars_botania.data.ConvertEnums;
import studio.fantasyit.ars_botania.utils.DoubleAccumulator;

import java.util.Optional;

@Mixin(value = AbstractSourceMachine.class,remap = false)
public abstract class ArsReceiveOrGiveManaPool extends ModdedTile implements IReceiveOrGiveMana {

    @Shadow public abstract int getMaxSource();

    @Shadow public abstract int getSource();

    @Shadow public abstract int setSource(int source);

    @Shadow public abstract int addSource(int source);

    @Shadow public abstract int removeSource(int source);

    @Shadow private int source;
    @Nullable
    public DoubleAccumulator accumulator;

    public DoubleAccumulator getAccumulator(){
        if (this.accumulator == null)
            this.accumulator = new DoubleAccumulator(ConvertEnums.getWhenManaWasPutIntoOrTakeOutFromSourceContainer());
        return this.accumulator;
    }
    public ArsReceiveOrGiveManaPool(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    @Override
    public boolean isOutputtingPower() {
        return false;
    }

    @Override
    public int getMaxMana() {
        return this.getAccumulator().inner2outer(this.getMaxSource());
    }

    @Override
    public Optional<DyeColor> getColor() {
        return Optional.of(DyeColor.PURPLE);
    }

    @Override
    public void setColor(Optional<DyeColor> optional) {

    }

    @Override
    public Level getManaReceiverLevel() {
        return this.level;
    }

    @Override
    public BlockPos getManaReceiverPos() {
        return this.worldPosition;
    }

    @Override
    public int getCurrentMana() {
        return this.getAccumulator().inner2outer(this.getSource());
    }

    @Override
    public boolean isFull() {
        return this.getSource() >= this.getMaxSource();
    }

    @Override
    public void receiveMana(int i) {
        if(i <= 0){
            this.removeSource(this.getAccumulator().accumulateAndGet(i));
        }else{
            this.addSource(this.getAccumulator().accumulateAndGet(i));
        }
    }

    @Override
    public boolean canReceiveManaFromBursts() {
        return !this.isFull();
    }
}
