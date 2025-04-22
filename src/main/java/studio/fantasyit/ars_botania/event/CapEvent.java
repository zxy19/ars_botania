package studio.fantasyit.ars_botania.event;


import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import studio.fantasyit.ars_botania.ArsBotania;
import studio.fantasyit.ars_botania.api.IArsManaCap;
import studio.fantasyit.ars_botania.api.IReceiveOrGiveMana;
import studio.fantasyit.ars_botania.mana.FakeManaCap;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.forge.CapabilityUtil;

@Mod.EventBusSubscriber(modid = ArsBotania.MODID)
public class CapEvent {
    @SubscribeEvent
    public static void onCapabilityAttach(AttachCapabilitiesEvent<BlockEntity> event) {
        if (
//                event.getObject().getBlockState().is(BlockRegistry.SOURCE_JAR.get()) &&
                event.getObject() instanceof IReceiveOrGiveMana sourceJar) {
            event.addCapability(new ResourceLocation(ArsBotania.MODID, "ars_botania_source_jar"),
                    CapabilityUtil.makeProvider(BotaniaForgeCapabilities.MANA_RECEIVER, sourceJar));
        }
    }
    @SubscribeEvent
    public static void onCapabilityAttachItem(AttachCapabilitiesEvent<ItemStack> event) {
        if(event.getObject().is(ArsBotania.FAKE_MANA_ITEM.get())){
            event.addCapability(new ResourceLocation(ArsBotania.MODID, "ars_botania_fake_mana_item"),
                    CapabilityUtil.makeProvider(BotaniaForgeCapabilities.MANA_ITEM,new FakeManaCap()));
        }
    }
    @SubscribeEvent
    public static void onServerPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player != null) {
            event.player.getCapability(CapabilityRegistry.MANA_CAPABILITY).ifPresent(manaCap -> {
                if (manaCap instanceof IArsManaCap arsManaCap) {
                    arsManaCap.setPlayer(event.player);
                }
            });
        }
    }
}
