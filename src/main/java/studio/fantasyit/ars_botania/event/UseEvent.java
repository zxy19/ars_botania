package studio.fantasyit.ars_botania.event;

import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import studio.fantasyit.ars_botania.ArsBotania;
import vazkii.botania.common.item.BotaniaItems;

@Mod.EventBusSubscriber(modid = ArsBotania.MODID)
public class UseEvent {
    @SubscribeEvent
    public static void onUse(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            if (event.getItem().is(BotaniaItems.manaCookie)) {
                sp.addEffect(
                        new MobEffectInstance(
                                ModPotions.MANA_REGEN_EFFECT.get(),
                                3 * 60 * 20,
                                1
                        ));
            }
        }
    }
}
