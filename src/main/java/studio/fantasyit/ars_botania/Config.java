package studio.fantasyit.ars_botania;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = ArsBotania.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.ConfigValue<Double> MANA_CONVERT = BUILDER
            .comment("One source mana(Ars) to X Mana(Bot)")
            .define("mana_convert", 15.0);

    private static final ForgeConfigSpec.BooleanValue PLAYER_MANA_CONVERT_ENABLE = BUILDER
            .comment("Enable player mana convert")
            .define("player_mana_convert.enable", false);
    private static final ForgeConfigSpec.BooleanValue PLAYER_MANA_RECOVERY_CHARGE_ITEM = BUILDER
            .comment("Allow charging items using player's mana recovery")
            .define("player_mana_convert.charge", false);
    private static final ForgeConfigSpec.ConfigValue<Double> PLAYER_MANA_CONVERT_A2B = BUILDER
            .comment("One player mana(Ars) to X Mana(Bot) [Referred when receiving/extracting ars-nouveau mana]")
            .define("player_mana_convert.a2b", 4.0);
    private static final ForgeConfigSpec.ConfigValue<Double> PLAYER_MANA_CONVERT_B2A = BUILDER
            .comment("One Mana(Bot) to X player mana(Ars) [Referred when receiving/extracting botania mana]")
            .define("player_mana_convert.b2a", 0.006);
    private static final ForgeConfigSpec.ConfigValue<Integer> MAX_RATE = BUILDER
            .comment("Max rate of mana transfer")
            .define("max_rate", 1000);
    static final ForgeConfigSpec SPEC = BUILDER.build();


    public static double manaConvertA2B;
    //Used when required unit is Mana
    public static double playerManaConvertA2B;
    //Used when required unit is Source
    public static double playerManaConvertB2A;
    public static boolean playerManaConvertEnable;
    public static boolean playerManaRecoveryChargeItem;
    public static int maxRate;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        manaConvertA2B = MANA_CONVERT.get();
        playerManaConvertA2B = PLAYER_MANA_CONVERT_A2B.get();
        playerManaConvertB2A = PLAYER_MANA_CONVERT_B2A.get();
        playerManaConvertEnable = PLAYER_MANA_CONVERT_ENABLE.get();
        playerManaRecoveryChargeItem = PLAYER_MANA_RECOVERY_CHARGE_ITEM.get();
        maxRate = MAX_RATE.get();
    }
}
