package absdivinity.content;

import arc.graphics.Color;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.type.unit.*;
import absdivinity.entities.*;

public class ADUnits {


    public static UnitType
        scrapCrawler, scrapHound,
        voidSpore, voidCreep,
        celestShard, celestDrift;

    public static UnitType
        ironStalker, ironBrute,
        abyssGrub, abyssWalker,
        neutroScout, neutroStriker;

    public static UnitType
        voidReaper, voidJuggernaut,
        ruinGuard, ruinCannon,
        glacierHulk, glacierSpike;

    public static UnitType
        corruptedTitan, corruptedBehemoth,
        diviniteShield, diviniteRampage;

    public static UnitType
        voidWraith, voidPhantom,
        energyHawk, energyFalcon;

    public static UnitType
        abyssSeraph, abyssArchangel;

    public static UnitType
        verdictSentinel,
        verdictAscendant;

    public static void load() {
        VerdictSentinelEntity.typeId = mindustry.gen.EntityMapping.register("absdivinity-verdict-sentinel", VerdictSentinelEntity::new);
        VerdictAscendantEntity.typeId = mindustry.gen.EntityMapping.register("absdivinity-verdict-ascendant", VerdictAscendantEntity::new);

        scrapCrawler = new UnitType("scrap-crawler") {{
            health = 80; speed = 1.8f; hitSize = 8f;
            armor = 0f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(3f, 12f) {{
                    width = 5f; height = 7f; lifetime = 30f;
                    frontColor = Color.valueOf("5f5f5f");
                    backColor  = Color.valueOf("333a41");
                }};
                reload = 25f; x = 3f; y = 2f;
            }});
        }};

        scrapHound = new UnitType("scrap-hound") {{
            health = 140; speed = 1.1f; hitSize = 10f;
            armor = 2f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(2.5f, 20f) {{
                    width = 7f; height = 9f; lifetime = 40f;
                    splashDamage = 8f; splashDamageRadius = 16f;
                    frontColor = Color.valueOf("4a5055");
                    backColor  = Color.valueOf("2e3538");
                }};
                reload = 40f; x = 4f; y = 1f;
            }});
        }};

        voidSpore = new UnitType("void-spore") {{
            health = 60; speed = 2.2f; hitSize = 7f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(3.5f, 8f) {{
                    width = 4f; height = 6f; lifetime = 25f;
                    status = StatusEffects.corroded; statusDuration = 120f;
                    frontColor = Color.valueOf("312e44");
                    backColor  = Color.valueOf("1a1520");
                }};
                reload = 20f; x = 0f; y = 3f;
            }});
        }};

        voidCreep = new UnitType("void-creep") {{
            health = 110; speed = 1.3f; hitSize = 9f;
            armor = 1f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(2f, 15f) {{
                    width = 6f; height = 8f; lifetime = 35f;
                    status = StatusEffects.corroded; statusDuration = 180f;
                    frontColor = Color.valueOf("312e44");
                    backColor  = Color.valueOf("0d0d12");
                }};
                reload = 35f; x = 3f; y = 2f; mirror = true;
            }});
        }};

        celestShard = new UnitType("celest-shard") {{
            health = 70; speed = 2.5f; hitSize = 7f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(4f, 10f) {{
                    width = 4f; height = 6f; lifetime = 22f;
                    frontColor = Color.valueOf("95c2ee");
                    backColor  = Color.valueOf("00aeff");
                }};
                reload = 18f; x = 3f; y = 1f; mirror = true;
            }});
        }};

        celestDrift = new UnitType("celest-drift") {{
            health = 95; speed = 1.7f; hitSize = 8f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(3f, 14f) {{
                    width = 5f; height = 7f; lifetime = 30f;
                    homingPower = 0.05f; homingRange = 80f;
                    frontColor = Color.valueOf("95c2ee");
                    backColor  = Color.valueOf("42f5e6");
                }};
                reload = 28f; x = 4f; y = 2f; mirror = true;
            }});
        }};

        ironStalker = new UnitType("iron-stalker") {{
            health = 280; speed = 1.6f; hitSize = 11f; armor = 3f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(4f, 28f) {{
                    width = 6f; height = 10f; lifetime = 40f;
                    frontColor = Color.valueOf("5f5f5f");
                    backColor  = Color.valueOf("333a41");
                }};
                reload = 30f; x = 5f; y = 2f; mirror = true;
            }});
        }};

        ironBrute = new UnitType("iron-brute") {{
            health = 480; speed = 0.9f; hitSize = 14f; armor = 6f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(3f, 45f) {{
                    width = 9f; height = 13f; lifetime = 45f;
                    splashDamage = 20f; splashDamageRadius = 24f;
                    frontColor = Color.valueOf("4a5055");
                    backColor  = Color.valueOf("2e3538");
                }};
                reload = 55f; x = 6f; y = 2f; mirror = true;
            }});
        }};

        abyssGrub = new UnitType("abyss-grub") {{
            health = 220; speed = 1.9f; hitSize = 10f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(3.5f, 22f) {{
                    width = 5f; height = 8f; lifetime = 32f;
                    status = StatusEffects.corroded; statusDuration = 150f;
                    frontColor = Color.valueOf("312e44");
                    backColor  = Color.valueOf("1a1520");
                }};
                reload = 28f; x = 4f; y = 2f; mirror = true;
            }});
        }};

        abyssWalker = new UnitType("abyss-walker") {{
            health = 380; speed = 1.2f; hitSize = 13f; armor = 2f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(2.8f, 35f) {{
                    width = 7f; height = 10f; lifetime = 38f;
                    status = StatusEffects.corroded; statusDuration = 200f;
                    splashDamage = 12f; splashDamageRadius = 18f;
                    frontColor = Color.valueOf("312e44");
                    backColor  = Color.valueOf("0d0d12");
                }};
                reload = 45f; x = 5f; y = 1f; mirror = true;
            }});
        }};

        neutroScout = new UnitType("neutro-scout") {{
            health = 250; speed = 2.1f; hitSize = 10f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(5f, 24f) {{
                    width = 5f; height = 8f; lifetime = 28f;
                    homingPower = 0.08f; homingRange = 100f;
                    frontColor = Color.valueOf("00aeff");
                    backColor  = Color.valueOf("95c2ee");
                }};
                reload = 22f; x = 4f; y = 2f; mirror = true;
            }});
        }};

        neutroStriker = new UnitType("neutro-striker") {{
            health = 420; speed = 1.4f; hitSize = 12f; armor = 3f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(4f, 40f) {{
                    width = 7f; height = 11f; lifetime = 35f; frontColor = Color.valueOf("00aeff");
                    backColor  = Color.valueOf("bf92f9");
                }};
                reload = 40f; x = 5f; y = 2f; mirror = true;
            }});
        }};

        voidReaper = new UnitType("void-reaper") {{
            health = 700; speed = 1.5f; hitSize = 14f; armor = 4f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(4f, 55f) {{
                    width = 8f; height = 12f; lifetime = 42f;
                    status = StatusEffects.corroded; statusDuration = 240f;
                    frontColor = Color.valueOf("312e44");
                    backColor  = Color.valueOf("1a1520");
                }};
                reload = 35f; x = 6f; y = 2f; mirror = true;
            }});
        }};

        voidJuggernaut = new UnitType("void-juggernaut") {{
            health = 1400; speed = 0.8f; hitSize = 18f; armor = 10f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(3f, 90f) {{
                    width = 11f; height = 16f; lifetime = 50f;
                    splashDamage = 50f; splashDamageRadius = 32f;
                    status = StatusEffects.corroded; statusDuration = 300f;
                    frontColor = Color.valueOf("312e44");
                    backColor  = Color.valueOf("0d0d12");
                }};
                reload = 70f; x = 8f; y = 2f; mirror = true;
            }});
        }};

        ruinGuard = new UnitType("ruin-guard") {{
            health = 900; speed = 1.2f; hitSize = 15f; armor = 7f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(5f, 65f) {{
                    width = 8f; height = 12f; lifetime = 38f;
                    frontColor = Color.valueOf("5f5f5f");
                    backColor  = Color.valueOf("3a3a42");
                }};
                reload = 40f; x = 6f; y = 2f; mirror = true;
            }});
        }};

        ruinCannon = new UnitType("ruin-cannon") {{
            health = 1100; speed = 0.9f; hitSize = 17f; armor = 8f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(4.5f, 120f) {{
                    width = 12f; height = 18f; lifetime = 55f;
                    splashDamage = 70f; splashDamageRadius = 40f;
                    frontColor = Color.valueOf("4a5055");
                    backColor  = Color.valueOf("2e3538");
                }};
                reload = 85f; x = 0f; y = 4f; mirror = false;
            }});
        }};

        glacierHulk = new UnitType("glacier-hulk") {{
            health = 1200; speed = 0.7f; hitSize = 18f; armor = 9f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(3.5f, 80f) {{
                    width = 10f; height = 14f; lifetime = 48f;
                    status = StatusEffects.freezing; statusDuration = 180f;
                    frontColor = Color.valueOf("9bb8cc");
                    backColor  = Color.valueOf("7aa8cc");
                }};
                reload = 60f; x = 7f; y = 2f; mirror = true;
            }});
        }};

        glacierSpike = new UnitType("glacier-spike") {{
            health = 600; speed = 1.8f; hitSize = 13f; armor = 3f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(6f, 50f) {{
                    width = 6f; height = 10f; lifetime = 30f;
                    status = StatusEffects.freezing; statusDuration = 120f;
                    pierce = true; pierceCap = 2;
                    frontColor = Color.valueOf("9bb8cc");
                    backColor  = Color.valueOf("95c2ee");
                }};
                reload = 30f; x = 5f; y = 2f; mirror = true;
            }});
        }};

        corruptedTitan = new UnitType("corrupted-titan") {{
            health = 3500; speed = 1.0f; hitSize = 22f; armor = 14f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(4f, 150f) {{
                    width = 13f; height = 19f; lifetime = 55f;
                    splashDamage = 80f; splashDamageRadius = 45f;
                    status = StatusEffects.corroded; statusDuration = 360f;
                    frontColor = Color.valueOf("312e44");
                    backColor  = Color.valueOf("1a1520");
                }};
                reload = 65f; x = 9f; y = 2f; mirror = true;
            }});
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(2.5f, 40f) {{
                    width = 7f; height = 10f; lifetime = 70f;
                    status = StatusEffects.corroded; statusDuration = 480f;
                    splashDamage = 25f; splashDamageRadius = 28f;
                    frontColor = Color.valueOf("1a1520");
                    backColor  = Color.valueOf("0d0d12");
                }};
                reload = 90f; x = 0f; y = -4f; mirror = false;
            }});
        }};

        corruptedBehemoth = new UnitType("corrupted-behemoth") {{
            health = 6000; speed = 0.6f; hitSize = 28f; armor = 18f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(3.5f, 220f) {{
                    width = 16f; height = 24f; lifetime = 65f;
                    splashDamage = 130f; splashDamageRadius = 60f;
                    status = StatusEffects.corroded; statusDuration = 480f;
                    frontColor = Color.valueOf("312e44");
                    backColor  = Color.valueOf("0d0d12");
                }};
                reload = 100f; x = 10f; y = 2f; mirror = true;
            }});
        }};

        diviniteShield = new UnitType("divinite-shield") {{
            health = 4500; speed = 0.9f; hitSize = 24f; armor = 16f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(5f, 180f) {{
                    width = 12f; height = 18f; lifetime = 50f; frontColor = Color.valueOf("bf92f9");
                    backColor  = Color.valueOf("312e44");
                }};
                reload = 75f; x = 8f; y = 2f; mirror = true;
            }});
        }};

        diviniteRampage = new UnitType("divinite-rampage") {{
            health = 5500; speed = 1.1f; hitSize = 26f; armor = 12f;
            constructor = UnitEntity::create;
            for (int i = -1; i <= 1; i++) {
                final int fi = i;
                weapons.add(new Weapon() {{
                    bullet = new BasicBulletType(5.5f, 130f) {{
                        width = 10f; height = 15f; lifetime = 45f; frontColor = Color.valueOf("bf92f9");
                        backColor  = Color.valueOf("95c2ee");
                    }};
                    reload = 55f; x = fi * 6f; y = 4f; mirror = false;
                }});
            }
        }};

        voidWraith = new UnitType("void-wraith") {{
            health = 1800; speed = 3.5f; hitSize = 14f; armor = 5f;
            flying = true; engineSize = 3f; engineOffset = 7f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(6f, 80f) {{
                    width = 7f; height = 11f; lifetime = 35f;
                    homingPower = 0.12f; homingRange = 180f;
                    status = StatusEffects.corroded; statusDuration = 240f;
                    frontColor = Color.valueOf("312e44");
                    backColor  = Color.valueOf("1a1520");
                }};
                reload = 35f; x = 6f; y = 2f; mirror = true;
            }});
        }};

        voidPhantom = new UnitType("void-phantom") {{
            health = 3000; speed = 2.8f; hitSize = 17f; armor = 8f;
            flying = true; engineSize = 4f; engineOffset = 9f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(5f, 130f) {{
                    width = 10f; height = 15f; lifetime = 42f;
                    splashDamage = 60f; splashDamageRadius = 36f;
                    status = StatusEffects.corroded; statusDuration = 300f;
                    frontColor = Color.valueOf("312e44");
                    backColor  = Color.valueOf("0d0d12");
                }};
                reload = 50f; x = 7f; y = 2f; mirror = true;
            }});
        }};

        energyHawk = new UnitType("energy-hawk") {{
            health = 2200; speed = 4f; hitSize = 13f; armor = 6f;
            flying = true; engineSize = 3f; engineOffset = 8f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(7f, 95f) {{
                    width = 6f; height = 10f; lifetime = 30f;
                    homingPower = 0.15f; homingRange = 200f; frontColor = Color.valueOf("00aeff");
                    backColor  = Color.valueOf("bf92f9");
                }};
                reload = 28f; x = 5f; y = 2f; mirror = true;
            }});
        }};

        energyFalcon = new UnitType("energy-falcon") {{
            health = 3800; speed = 3.2f; hitSize = 16f; armor = 9f;
            flying = true; engineSize = 4f; engineOffset = 10f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(6f, 160f) {{
                    width = 9f; height = 14f; lifetime = 38f; homingPower = 0.1f; homingRange = 220f;
                    frontColor = Color.valueOf("bf92f9");
                    backColor  = Color.valueOf("312e44");
                }};
                reload = 45f; x = 7f; y = 3f; mirror = true;
            }});
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(3f, 100f) {{
                    width = 10f; height = 10f; lifetime = 30f;
                    splashDamage = 120f; splashDamageRadius = 50f;
                    frontColor = Color.valueOf("bf92f9");
                    backColor  = Color.valueOf("95c2ee");
                }};
                reload = 120f; x = 0f; y = -4f; mirror = false;
            }});
        }};

        abyssSeraph = new UnitType("abyss-seraph") {{
            health = 9000; speed = 2.8f; hitSize = 22f; armor = 14f;
            flying = true; engineSize = 5f; engineOffset = 12f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(6.5f, 250f) {{
                    width = 13f; height = 20f; lifetime = 45f;
                    splashDamage = 130f; splashDamageRadius = 55f;
                    status = StatusEffects.corroded; statusDuration = 360f; frontColor = Color.valueOf("312e44");
                    backColor  = Color.valueOf("bf92f9");
                }};
                reload = 55f; x = 9f; y = 3f; mirror = true;
            }});
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(4f, 80f) {{
                    width = 8f; height = 12f; lifetime = 55f;
                    homingPower = 0.15f; homingRange = 280f;
                    status = StatusEffects.corroded; statusDuration = 300f;
                    frontColor = Color.valueOf("1a1520");
                    backColor  = Color.valueOf("0d0d12");
                }};
                reload = 30f; x = 5f; y = -2f; mirror = true;
            }});
        }};

        abyssArchangel = new UnitType("abyss-archangel") {{
            health = 16000; speed = 2.2f; hitSize = 28f; armor = 20f;
            flying = true; engineSize = 6f; engineOffset = 14f;
            constructor = UnitEntity::create;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(7f, 400f) {{
                    width = 18f; height = 28f; lifetime = 58f;
                    splashDamage = 250f; splashDamageRadius = 75f; status = StatusEffects.corroded; statusDuration = 480f;
                    frontColor = Color.valueOf("bf92f9");
                    backColor  = Color.valueOf("312e44");
                }};
                reload = 90f; x = 0f; y = 6f; mirror = false;
            }});
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(5.5f, 160f) {{
                    width = 10f; height = 15f; lifetime = 40f;
                    homingPower = 0.12f; homingRange = 300f; frontColor = Color.valueOf("312e44");
                    backColor  = Color.valueOf("1a1520");
                }};
                reload = 40f; x = 10f; y = 2f; mirror = true;
            }});
        }};

                verdictSentinel = new UnitType("verdict-sentinel") {{
            health = 80000; speed = 0.7f; hitSize = 40f; armor = 30f;
            constructor = VerdictSentinelEntity::new;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(4.5f, 750f) {{
                    width = 26f; height = 40f; lifetime = 72f;
                    splashDamage = 500f; splashDamageRadius = 110f;
                    status = StatusEffects.corroded; statusDuration = 600f;
                    pierce = true; pierceCap = 3;
                    frontColor = Color.valueOf("bf92f9");
                    backColor  = Color.valueOf("1a1520");
                }};
                reload = 110f; x = 0f; y = 10f; mirror = false;
            }});
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(6f, 180f) {{
                    width = 10f; height = 15f; lifetime = 45f;
                    splashDamage = 80f; splashDamageRadius = 40f;
                    status = StatusEffects.corroded; statusDuration = 300f;
                    frontColor = Color.valueOf("5f5f5f");
                    backColor  = Color.valueOf("333a41");
                }};
                reload = 38f; x = 14f; y = 4f; mirror = true;
            }});
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(9f, 140f) {{
                    width = 7f; height = 11f; lifetime = 30f;
                    homingPower = 0.25f; homingRange = 450f;
                    pierce = true; pierceCap = 2;
                    frontColor = Color.valueOf("00aeff");
                    backColor  = Color.valueOf("95c2ee");
                }};
                reload = 18f; x = 9f; y = -4f; mirror = true;
            }});
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(2.5f, 90f) {{
                    width = 14f; height = 14f; lifetime = 90f;
                    splashDamage = 200f; splashDamageRadius = 75f;
                    status = StatusEffects.corroded; statusDuration = 480f;
                    frontColor = Color.valueOf("312e44");
                    backColor  = Color.valueOf("0d0d12");
                }};
                reload = 80f; x = 5f; y = -6f; mirror = true;
            }});
        }};

                verdictAscendant = new UnitType("verdict-ascendant") {{
            health = 120000; speed = 1.8f; hitSize = 50f; armor = 25f;
            flying = true; engineSize = 8f; engineOffset = 18f;
            constructor = VerdictAscendantEntity::new;
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(8.5f, 1000f) {{
                    width = 28f; height = 45f; lifetime = 85f;
                    splashDamage = 700f; splashDamageRadius = 120f;
                    pierce = true; pierceCap = 5;
                    status = StatusEffects.corroded; statusDuration = 720f;
                    frontColor = Color.valueOf("bf92f9");
                    backColor  = Color.valueOf("0d0d12");
                }};
                reload = 130f; x = 0f; y = 12f; mirror = false;
            }});
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(4.5f, 160f) {{
                    width = 8f; height = 13f; lifetime = 65f;
                    homingPower = 0.22f; homingRange = 500f;
                    splashDamage = 80f; splashDamageRadius = 40f;
                    frontColor = Color.valueOf("312e44");
                    backColor  = Color.valueOf("bf92f9");
                }};
                reload = 22f; x = 16f; y = 5f; mirror = true;
            }});
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(0.1f, 30f) {{
                    width = 50f; height = 50f; lifetime = 12f;
                    splashDamage = 350f; splashDamageRadius = 180f;
                    status = StatusEffects.wet; statusDuration = 300f;
                    frontColor = Color.valueOf("0d0d12");
                    backColor  = Color.valueOf("312e44");
                }};
                reload = 200f; x = 0f; y = 0f; mirror = false;
            }});
            weapons.add(new Weapon() {{
                bullet = new BasicBulletType(12f, 350f) {{
                    width = 9f; height = 14f; lifetime = 25f;
                    homingPower = 0.08f; homingRange = 600f;
                    pierce = true; pierceCap = 2;
                    frontColor = Color.valueOf("00aeff");
                    backColor  = Color.valueOf("95c2ee");
                }};
                reload = 45f; x = 10f; y = -3f; mirror = true;
            }});
        }};
    }
}
