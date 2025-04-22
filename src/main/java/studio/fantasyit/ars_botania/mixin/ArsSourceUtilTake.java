package studio.fantasyit.ars_botania.mixin;

import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.source.SourceProvider;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import studio.fantasyit.ars_botania.api.INoExtractFrom;
import studio.fantasyit.ars_botania.api.IReceiveOrGiveSource;

import java.util.List;

@Mixin(value = SourceUtil.class, remap = false)
public class ArsSourceUtilTake {
    @Inject(at = @At("RETURN"), method = "canTakeSource", cancellable = true)
    private static void canTakeSource(BlockPos pos, Level world, int range, CallbackInfoReturnable<List<ISpecialSourceProvider>> cir) {
        List<ISpecialSourceProvider> list = cir.getReturnValue();
        BlockPos.withinManhattanStream(pos, range, range, range).forEach((b) -> {
            if (world.isLoaded(b)) {
                BlockEntity patt1782$temp = world.getBlockEntity(b);
                if (patt1782$temp instanceof IReceiveOrGiveSource jar && !(jar instanceof INoExtractFrom)) {
                    if (jar.getSource() > 0) {
                        list.add(new SourceProvider(jar, b.immutable()));
                    }
                }
            }
        });
        cir.setReturnValue(list);
    }

    @Inject(at = @At("RETURN"), method = "canGiveSource", cancellable = true)
    private static void canGiveSource(BlockPos pos, Level world, int range, CallbackInfoReturnable<List<ISpecialSourceProvider>> cir) {
        List<ISpecialSourceProvider> list = cir.getReturnValue();
        BlockPos.withinManhattanStream(pos, range, range, range).forEach((b) -> {
            if (world.isLoaded(b)) {
                BlockEntity patt1782$temp = world.getBlockEntity(b);
                if (patt1782$temp instanceof IReceiveOrGiveSource jar  && !(jar instanceof INoExtractFrom)) {
                    if (jar.canAcceptSource()) {
                        list.add(new SourceProvider(jar, b.immutable()));
                    }
                }
            }
        });
        cir.setReturnValue(list);
    }
}
