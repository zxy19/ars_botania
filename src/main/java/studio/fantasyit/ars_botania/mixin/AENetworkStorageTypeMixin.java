package studio.fantasyit.ars_botania.mixin;

import appbot.ae2.ManaKey;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.me.storage.NetworkStorage;
import com.llamalad7.mixinextras.sugar.Local;
import gripe._90.arseng.me.key.SourceKey;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Deque;

@Mixin(value = NetworkStorage.class, remap = false)
public abstract class AENetworkStorageTypeMixin {

    @Unique
    private static final ThreadLocal<Deque<AEKey>> KEY_DEP = new ThreadLocal<>();

    @Shadow
    public abstract long insert(AEKey what, long amount, Actionable type, IActionSource src);

    @Shadow
    public abstract long extract(AEKey what, long amount, Actionable mode, IActionSource source);

    @Shadow
    protected abstract void surface(Actionable type);

    @Shadow
    protected abstract boolean diveList(Actionable type);

    @ModifyVariable(at = @At(value = "INVOKE", target = "Lappeng/me/storage/NetworkStorage;surface(Lappeng/api/config/Actionable;)V", shift = At.Shift.BEFORE), method = "insert", name = "remaining")
    public long insert(long remaining, @Local(argsOnly = true) AEKey what, @Local(argsOnly = true) Actionable type, @Local(argsOnly = true) IActionSource src) {

        if (remaining > 0) {
            if (KEY_DEP.get() == null) {
                KEY_DEP.set(new java.util.ArrayDeque<>());
            }
            if (KEY_DEP.get().contains(what)) {
                return remaining;
            }
            KEY_DEP.get().push(what);
            this.surface(type);
            long ret = remaining;
            if (what == ManaKey.KEY) {
                ret = remaining - this.insert(SourceKey.KEY, remaining, type, src);
            } else if (what == SourceKey.KEY) {
                ret = remaining - this.insert(ManaKey.KEY, remaining, type, src);
            }
            this.diveList(type);

            KEY_DEP.get().pop();
            return ret;
        }
        return remaining;
    }


    @ModifyVariable(at = @At(value = "INVOKE", target = "Lappeng/me/storage/NetworkStorage;surface(Lappeng/api/config/Actionable;)V", shift = At.Shift.BEFORE), method = "extract", name = "extracted")
    public long extract(long extracted, @Local(argsOnly = true) long amount, @Local(argsOnly = true) AEKey what, @Local(argsOnly = true) Actionable type, @Local(argsOnly = true) IActionSource src) {
        if (amount - extracted > 0) {
            if (KEY_DEP.get() == null) {
                KEY_DEP.set(new java.util.ArrayDeque<>());
            }
            if (KEY_DEP.get().contains(what)) {
                return extracted;
            }
            KEY_DEP.get().push(what);
            this.surface(type);
            long ret = extracted;
            if (what == ManaKey.KEY) {
                ret = extracted + this.extract(SourceKey.KEY, amount - extracted, type, src);
            } else if (what == SourceKey.KEY) {
                ret = extracted + this.extract(ManaKey.KEY, amount - extracted, type, src);
            }
            this.diveList(type);
            KEY_DEP.get().pop();
            return ret;
        }
        return extracted;
    }
}
