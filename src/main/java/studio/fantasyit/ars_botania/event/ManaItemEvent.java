package studio.fantasyit.ars_botania.event;

import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import studio.fantasyit.ars_botania.ArsBotania;
import studio.fantasyit.ars_botania.Config;
import studio.fantasyit.ars_botania.api.IArsManaCap;
import studio.fantasyit.ars_botania.mana.FakeManaCap;
import vazkii.botania.api.mana.ManaItem;
import vazkii.botania.api.mana.ManaItemsEvent;
import vazkii.botania.xplat.XplatAbstractions;

@Mod.EventBusSubscriber
public class ManaItemEvent {
    @SubscribeEvent
    public static void ars_botania$onManaItemEvent(ManaItemsEvent event) {
        if(!Config.playerManaConvertEnable) return;
        ItemStack itemStack = ArsBotania.FAKE_MANA_ITEM.get().getDefaultInstance();
        ManaItem manaItem = XplatAbstractions.INSTANCE.findManaItem(itemStack);
        Player player = event.getPlayer();
        if(manaItem instanceof FakeManaCap cap)
            player.getCapability(CapabilityRegistry.MANA_CAPABILITY).ifPresent(manaCap -> {
                cap.setCap(manaCap);
                cap.setPlayer(player);
            });
        event.getItems().add(itemStack);
    }
}
