package studio.fantasyit.ars_botania.api;

import net.minecraft.world.entity.player.Player;

public interface IArsManaCap {
    void setPlayer(Player player);

    Player getPlayer();

    double ars_botania$getRawMana();

    int ars_botania$getRawMaxMana();
}
