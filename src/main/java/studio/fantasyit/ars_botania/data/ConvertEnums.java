package studio.fantasyit.ars_botania.data;

import studio.fantasyit.ars_botania.Config;

/**
 * Specific Convert Rates That Represents one Inner to X Outer
 */
public class ConvertEnums {
    /**
     * Inner: Source
     * Outer: Mana
     */
    public static double getWhenManaWasPutIntoOrTakeOutFromSourceContainer() {
        return Config.manaConvertA2B;
    }

    /**
     * Inner: Mana
     * Outer: Source
     */
    public static double getWhenSourceWasPutIntoOrTakeOutFromManaContainer() {
        return 1 / Config.manaConvertA2B;
    }

    /**
     * Inner: Mana
     * Outer: Source
     * Finally Costed Unit: Source
     */
    public static double getWhenPlayerCoseBotaniaManaAndWillBeConvertedToPlayerSource() {
        return Config.playerManaConvertB2A;
    }

    /**
     * Inner: Mana
     * Outer: Source
     * Finally Costed Unit: Mana
     */
    public static double getWhenPlayerUsePlayerSourceToFillInMana() {
        return 1 / Config.playerManaConvertA2B;
    }


    /**
     * Inner: Source
     * Outer: Mana
     * Finally Costed Unit: Mana
     */
    public static double getWhenPlayerUsePlayerSourceAndWillBeConvertedToBotaniaMana() {
        return Config.playerManaConvertA2B;
    }

    /**
     * Inner: Source
     * Outer: Mana
     * Finally Costed Unit: Source
     */
    public static double getWhenPlayerReceiveBotaniaManaAndWillBeConvertedToPlayerSource() {
        return 1 / Config.playerManaConvertB2A;
    }
}
