package studio.fantasyit.ars_botania.mana;

import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.common.capability.ManaCap;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSyncPlayerCap;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateMana;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.fantasyit.ars_botania.ArsBotania;
import studio.fantasyit.ars_botania.Config;
import studio.fantasyit.ars_botania.api.IArsManaCap;
import studio.fantasyit.ars_botania.utils.DoubleAccumulator;
import vazkii.botania.api.mana.ManaItem;

public class FakeManaCap implements ManaItem {
    @Nullable
    public DoubleAccumulator accumulator;

    public DoubleAccumulator getAccumulator() {
        if (this.accumulator == null)
            this.accumulator = new DoubleAccumulator(Config.playerManaConvertA2B);
        return this.accumulator;
    }

    @Nullable
    public DoubleAccumulator storeAccumulator;

    public DoubleAccumulator getStoreAccumulator() {
        if (this.storeAccumulator == null)
            this.storeAccumulator = new DoubleAccumulator(1/Config.playerManaConvertB2A);
        return this.storeAccumulator;
    }

    IManaCap cap;
    Player ars_botania$player;

    public FakeManaCap() {
    }

    public void setPlayer(Player player) {
        this.ars_botania$player = player;
    }

    public void setCap(@NotNull IManaCap cap) {
        this.cap = cap;
    }

    @Override
    public int getMana() {
        if (cap == null) return 0;
        return (int) getAccumulator().inner2outer(((IArsManaCap) cap).ars_botania$getRawMana());
    }

    @Override
    public int getMaxMana() {
        if (cap == null) return 0;
        return (int) getAccumulator().inner2outer(((IArsManaCap) cap).ars_botania$getRawMaxMana());
    }

    @Override
    public void addMana(int i) {
        if (i < 0) {
            int realAdd = getAccumulator().takeAndGetCost(-i);
            cap.removeMana(realAdd);
        } else {
            if(Config.playerManaRecoveryChargeItem) {
                int realAdd = getStoreAccumulator().accumulateAndGet(i);
                cap.addMana(realAdd);
            }
        }
        syncCap();
    }

    @Override
    public boolean canReceiveManaFromPool(BlockEntity blockEntity) {
        return false;
    }

    @Override
    public boolean canReceiveManaFromItem(ItemStack itemStack) {
        return !itemStack.is(ArsBotania.FAKE_MANA_ITEM.get());
    }

    @Override
    public boolean canExportManaToPool(BlockEntity blockEntity) {
        return false;
    }

    @Override
    public boolean canExportManaToItem(ItemStack itemStack) {
        return !itemStack.is(ArsBotania.FAKE_MANA_ITEM.get());
    }

    @Override
    public boolean isNoExport() {
        return false;
    }

    protected void syncCap() {
        if (this.ars_botania$player instanceof ServerPlayer sp && this.cap != null) {
            Networking.sendToPlayerClient(new PacketUpdateMana(
                    ((IArsManaCap) cap).ars_botania$getRawMana(),
                    ((IArsManaCap) cap).ars_botania$getRawMaxMana(),
                    cap.getGlyphBonus(),
                    cap.getBookTier()
            ), sp);
        }
    }
}
