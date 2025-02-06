# Ars Botania
![cover.png](image%2Fcover.png)

Mana interoperability between Botania and Ars Nouveau

## Features
![main.png](image%2Fmain.png)

This mod adds the following features:

+ Ars Nouveau's source jars can bind to Botania's generating flowers/mana spreaders
+ Ars Nouveau's mana relays can transfer mana between mana pools
+ Mana spreaders can shoot mana into source jars
+ Mana tunnels can directly store mana into mana pools
+ Ars Nouveau's spellbooks can use mana items as mana sources (configurable)
+ Botania items can use Ars Nouveau's player mana (configurable)
+ Ars Nouveau's mana recovery can charge Botania's mana items (configurable)
+ AE2's ME source jars/fluix mana pools can access both mana types

## Configuration
```toml
# How much Botania mana equals 1 Ars Nouveau mana for block conversions (e.g. Mana Pool -> Source Jar)
mana_convert = 15.0
# Maximum transfer rate of mana relays between Botania blocks (in Ars Nouveau mana units)
max_rate = 1000

[player_mana_convert]
# Enable player mana conversion
enable = true
# Conversion rate when needing Ars Nouveau mana or storing as Botania mana
a2b = 4.0
# Conversion rate when needing Botania mana or storing as Ars Nouveau mana
b2a = 0.006
# Allow player mana recovery to charge Botania items
charge = true
```