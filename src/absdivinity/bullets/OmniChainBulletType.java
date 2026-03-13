package absdivinity.bullets;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Trail;

/**
 * OmniChainBulletType — спиральная пуля с цепным ударом.
 *
 * Характеристики масштабируются от charge материала:
 *  - количество и размер спиралей
 *  - длина и количество цепных прыжков
 *  - дальность подбора следующей цели
 *
 * Дальность полёта задаётся снаружи (из OmniTurret) и не превышает maxDrawSize.
 */
public class OmniChainBulletType extends BulletType {

    // ── Визуал пули ────────────────────────────────────────────────
    public Color   bulletColor;
    public Color   glowColor;
    public Color   coreColor;         // яркое ядро (белёный bulletColor)

    /** Максимальный drawSize — кап чтобы огромные цепи не тормозили рендер. */
    public float   maxDrawSize    = 320f;

    // ── Спираль ────────────────────────────────────────────────────
    public int     helixCount     = 3;
    public int     helixLength    = 32;
    public float   helixWidth     = 2.4f;
    public float   helixRadius    = 7f;
    public float   helixSpeed     = 8f;

    // ── Ускорение ──────────────────────────────────────────────────
    public float   accelFrom      = 0.1f;
    public float   accelTo        = 0.65f;
    public float   speedBegin     = 2f;

    // ── Цепной удар ────────────────────────────────────────────────
    public int     maxChain       = 4;
    public float   chainRange     = 100f;
    public float   chainDamage    = 0f;
    public float   chainDecay     = 0.80f;   // множитель урона каждого прыжка

    // ── Вспышка при выстреле ───────────────────────────────────────
    /** Продолжительность вспышки-шлейфа при спавне. */
    public float   muzzleFlashDuration = 18f;

    // ── Статика ────────────────────────────────────────────────────
    protected static final Seq<Healthc> hitUnits = new Seq<>();

    // ══════════════════════════════════════════════════════════════
    // Конструктор
    // ══════════════════════════════════════════════════════════════

    /**
     * @param damage       базовый урон
     * @param charge       charge материала (0..∞, обычно 0..3)
     * @param color        базовый цвет пули
     * @param bulletRange  дальность полёта (уже посчитана в OmniTurret)
     * @param maxDrawSz    кап drawSize
     */
    public OmniChainBulletType(float damage, float charge, Color color,
                               float bulletRange, float maxDrawSz) {
        this.damage      = damage;
        this.bulletColor = color.cpy();
        this.maxDrawSize = maxDrawSz;

        // Цепь — масштаб от charge
        float ch          = Mathf.clamp(charge, 0f, 3f) / 3f; // 0..1
        this.maxChain     = (int)(2  + ch * 14);               // 2..16
        this.chainRange   = 80f + ch * 160f;                   // 80..240
        this.chainDamage  = damage * (0.65f + ch * 0.35f);     // 65%..100%

        // Спираль — масштаб от charge
        this.helixCount   = ch < 0.25f ? 2 : (ch < 0.6f ? 3 : 4);
        this.helixRadius  = 5f  + ch * 10f;                    // 5..15
        this.helixWidth   = 1.6f + ch * 2f;                    // 1.6..3.6
        this.helixLength  = (int)(24 + ch * 20);               // 24..44

        // Скорость / время жизни — дальность задана извне
        this.speed        = 11f;
        this.speedBegin   = 2.5f;
        this.lifetime     = bulletRange / this.speed * 1.1f;   // чтобы долетела

        this.shootEffect   = Fx.none;
        this.hitEffect     = Fx.none;
        this.despawnEffect = Fx.none;
        this.trailEffect   = Fx.none;
        this.hittable      = true;
        this.collidesGround = true;
        this.collidesAir    = true;

        // drawSize: chainRange + гало — но не больше капа
        this.drawSize = Math.min(chainRange + helixRadius * 4f, maxDrawSize);
    }

    // ══════════════════════════════════════════════════════════════
    // Init
    // ══════════════════════════════════════════════════════════════

    @Override
    public void init() {
        super.init();
        glowColor = bulletColor.cpy().lerp(Color.white, 0.45f);
        coreColor = bulletColor.cpy().lerp(Color.white, 0.85f);
    }

    @Override
    public void init(Bullet b) {
        super.init(b);
        // data[0] = Trail[] спиралей, хранится как Object[]
        // data — просто Trail[]
        Trail[] trails = new Trail[helixCount];
        for (int i = 0; i < helixCount; i++) trails[i] = new Trail(helixLength);
        b.data = trails;
    }

    // ══════════════════════════════════════════════════════════════
    // Update
    // ══════════════════════════════════════════════════════════════

    @Override
    public void update(Bullet b) {
        // Ускорение по Interp
        float t   = Mathf.curve(b.fin(), accelFrom, accelTo);
        float spd = speedBegin + Interp.pow2Out.apply(t) * (speed - speedBegin);
        b.vel.setLength(spd);

        super.update(b);

        if (!(b.data instanceof Trail[])) return;
        Trail[] trails = (Trail[]) b.data;

        float phase = b.time * helixSpeed;
        for (int i = 0; i < helixCount; i++) {
            float ang = phase + i * (360f / helixCount);
            // Спираль расширяется при разгоне — радиус пульсирует
            float r = helixRadius * (0.6f + 0.4f * Mathf.curve(b.fin(), 0f, 0.35f))
                    * (1f + Mathf.absin(b.time, 3.5f, 0.12f));
            float ox = Mathf.cosDeg(ang) * r;
            float oy = Mathf.sinDeg(ang) * r;
            if (trails[i] == null) trails[i] = new Trail(helixLength);
            trails[i].length = helixLength;
            trails[i].update(b.x + ox, b.y + oy, b.fout());
        }
    }

    // ══════════════════════════════════════════════════════════════
    // Removed — трейл-fade при исчезновении
    // ══════════════════════════════════════════════════════════════

    @Override
    public void removed(Bullet b) {
        super.removed(b);
        if (!(b.data instanceof Trail[])) return;
        Trail[] trails = (Trail[]) b.data;
        for (Trail t : trails) {
            if (t != null && t.size() > 0)
                Fx.trailFade.at(b.x, b.y, helixWidth, bulletColor, t.copy());
        }
        // Маленькая вспышка при исчезновении
        spawnDespawnBurst(b.x, b.y);
    }

    // ══════════════════════════════════════════════════════════════
    // Draw
    // ══════════════════════════════════════════════════════════════

    @Override
    public void draw(Bullet b) {
        if (!(b.data instanceof Trail[])) return;
        Trail[] trails = (Trail[]) b.data;

        float fade  = b.fout();
        float fin   = b.fin();
        float pulse = 1f + Mathf.absin(Time.time, 3.5f, 0.22f);
        float accel = Mathf.curve(fin, accelFrom, accelTo); // 0..1 разгон

        float z = Draw.z();

        // ── 1. Трейлы спирали (под пулей) ─────────────────────────
        Draw.z(Layer.bullet - 0.002f);
        for (int i = 0; i < trails.length; i++) {
            Trail t = trails[i];
            if (t == null) continue;
            // Каждая нить чуть светлее или темнее
            Color tc = (i % 2 == 0) ? bulletColor : glowColor;
            t.draw(tc, helixWidth * fade * (0.8f + 0.2f * i));
        }

        // ── 2. Внешнее мягкое гало ────────────────────────────────
        Draw.z(Layer.bullet - 0.001f);
        float haloR = helixRadius * 2.8f * pulse;
        Draw.color(bulletColor.r, bulletColor.g, bulletColor.b, 0.08f * fade);
        Fill.circle(b.x, b.y, haloR * 1.6f);
        Draw.color(bulletColor.r, bulletColor.g, bulletColor.b, 0.18f * fade);
        Fill.circle(b.x, b.y, haloR);

        // ── 3. Средний слой — основная сфера ──────────────────────
        Draw.z(Layer.bullet);
        Draw.color(bulletColor, 0.6f * fade);
        Fill.circle(b.x, b.y, helixRadius * 0.95f * pulse);

        // ── 4. Яркое ядро ─────────────────────────────────────────
        Draw.color(glowColor, 0.85f * fade);
        Fill.circle(b.x, b.y, helixRadius * 0.55f * pulse);
        Draw.color(coreColor, fade);
        Fill.circle(b.x, b.y, helixRadius * 0.28f * pulse);

        // ── 5. Вращающиеся иглы (2 пары, разные скорости) ─────────
        float triSize = helixRadius * 0.9f * fade * pulse;
        float ang1    = b.time * helixSpeed * 0.55f;
        float ang2    = -b.time * helixSpeed * 0.42f + 45f;
        float ang3    = b.time * helixSpeed * 0.3f + 90f;

        Draw.color(bulletColor, 0.85f * fade);
        Drawf.tri(b.x, b.y, triSize * 0.38f, triSize * 2.1f, ang1);
        Drawf.tri(b.x, b.y, triSize * 0.38f, triSize * 2.1f, ang1 + 180f);

        Draw.color(glowColor, 0.65f * fade);
        Drawf.tri(b.x, b.y, triSize * 0.28f, triSize * 1.5f, ang2);
        Drawf.tri(b.x, b.y, triSize * 0.28f, triSize * 1.5f, ang2 + 180f);

        // Третья пара — появляется при charge > 0 (helixCount >= 3)
        if (helixCount >= 3) {
            Draw.color(coreColor, 0.45f * fade);
            Drawf.tri(b.x, b.y, triSize * 0.2f, triSize * 1.1f, ang3);
            Drawf.tri(b.x, b.y, triSize * 0.2f, triSize * 1.1f, ang3 + 180f);
        }

        // ── 6. Кольцо разгона (появляется при ускорении) ──────────
        if (accel < 0.9f) {
            float ringAlpha = (1f - accel) * fade * 0.7f;
            float ringR     = helixRadius * (1.5f + accel * 3f);
            Draw.color(bulletColor, ringAlpha);
            Lines.stroke(helixWidth * 0.7f * (1f - accel));
            Lines.circle(b.x, b.y, ringR);
        }

        // ── 7. Осколки при разгоне (первые 25% жизни) ─────────────
        if (fin < 0.3f) {
            float sparkAlpha = (0.3f - fin) / 0.3f * 0.6f;
            Draw.color(glowColor, sparkAlpha);
            int sparks = helixCount * 2;
            float base = b.time * helixSpeed * 2f;
            for (int i = 0; i < sparks; i++) {
                float sa  = base + i * (360f / sparks);
                float sr  = helixRadius * (1.5f + Mathf.absin(b.time + i, 2f, 0.5f));
                float spx = b.x + Mathf.cosDeg(sa) * sr;
                float spy = b.y + Mathf.sinDeg(sa) * sr;
                Fill.circle(spx, spy, helixWidth * 0.45f * sparkAlpha);
            }
        }

        // ── 8. Динамический свет ──────────────────────────────────
        Drawf.light(b.x, b.y, helixRadius * 5.5f * pulse, bulletColor, 0.75f * fade);

        Draw.reset();
        Draw.z(z);
    }

    // ══════════════════════════════════════════════════════════════
    // Hit — цепной удар
    // ══════════════════════════════════════════════════════════════

    @Override
    public void hit(Bullet b, float x, float y) {
        super.hit(b, x, y);

        // Вспышка в точке попадания
        spawnHitBurst(x, y);

        hitUnits.clear();
        float cx = x, cy = y;
        float dmg = chainDamage;

        for (int i = 0; i < maxChain; i++) {
            final float fcx = cx, fcy = cy;
            Teamc target = Units.closestEnemy(b.team, fcx, fcy, chainRange,
                u -> u instanceof Healthc && !hitUnits.contains((Healthc) u));

            if (target instanceof Healthc) {
                Healthc unit = (Healthc) target;
                spawnChainArc(fcx, fcy, unit.x(), unit.y(), dmg, i);
                unit.damage(dmg);
                hitUnits.add(unit);
                cx  = unit.x();
                cy  = unit.y();
                dmg *= chainDecay;
            } else break;
        }
    }

    // ══════════════════════════════════════════════════════════════
    // Эффекты
    // ══════════════════════════════════════════════════════════════

    /** Вспышка при попадании первой пули. */
    void spawnHitBurst(float x, float y) {
        final Color c  = bulletColor;
        final Color gc = glowColor;
        final Color cc = coreColor;
        final float hw = helixWidth;
        final float hr = helixRadius;

        new Effect(38f, e -> {
            float p = e.fin();

            // Взрывное кольцо
            Draw.color(c, e.fout() * 0.9f);
            Lines.stroke(hw * 1.5f * e.fout());
            Lines.circle(e.x, e.y, hr * (1.5f + p * 5f));

            // Второе кольцо (чуть позже)
            if (p > 0.15f) {
                float p2 = (p - 0.15f) / 0.85f;
                Draw.color(gc, (1f - p2) * 0.6f);
                Lines.stroke(hw * e.fout());
                Lines.circle(e.x, e.y, hr * (1f + p2 * 3.5f));
            }

            // Яркий центр
            Draw.color(cc, e.fout() * (1f - p * 0.6f));
            Fill.circle(e.x, e.y, hr * 1.4f * e.fout());

            Draw.color(c, e.fout() * 0.5f);
            Fill.circle(e.x, e.y, hr * 2.5f * e.fout());

            // Иглы разлёта
            int spikes = 6 + (int)(maxChain / 3);
            for (int i = 0; i < spikes; i++) {
                float ang = e.rotation + i * (360f / spikes) + p * 45f;
                float len = hr * (2f + p * 4f) * e.fout();
                float sx  = e.x + Mathf.cosDeg(ang) * len;
                float sy  = e.y + Mathf.sinDeg(ang) * len;
                Draw.color(c, e.fout() * 0.75f);
                Drawf.tri(sx, sy, hw * 0.6f, hw * 3f, ang);
            }

            // Частицы
            Angles.randLenVectors(e.id, 10, hr * (1f + p * 3f),
                (ox, oy) -> {
                    Draw.color(gc, e.fout() * 0.8f);
                    Fill.circle(e.x + ox, e.y + oy, hw * 0.5f * e.fout());
                });

            Drawf.light(e.x, e.y, hr * 8f * e.fout(), c, 0.8f * e.fout());
        }).at(x, y, Mathf.random(360f));
    }

    /** Вспышка при исчезновении (промах). */
    void spawnDespawnBurst(float x, float y) {
        final Color c  = bulletColor;
        final float hw = helixWidth;
        final float hr = helixRadius;

        new Effect(22f, e -> {
            float p = e.fin();
            Draw.color(c, e.fout() * 0.7f);
            Lines.stroke(hw * e.fout());
            Lines.circle(e.x, e.y, hr * (0.8f + p * 2.5f));
            Angles.randLenVectors(e.id, 5, hr * (0.5f + p * 2f),
                (ox, oy) -> {
                    Draw.color(glowColor, e.fout() * 0.6f);
                    Fill.circle(e.x + ox, e.y + oy, hw * 0.4f * e.fout());
                });
            Drawf.light(e.x, e.y, hr * 4f * e.fout(), c, 0.5f * e.fout());
        }).at(x, y);
    }

    /**
     * Дуга цепного удара от (x1,y1) к (x2,y2).
     * hopIndex влияет на толщину, изгиб и количество паразитных искр.
     */
    void spawnChainArc(float x1, float y1, float x2, float y2, float dmg, int hopIndex) {
        final Color c  = bulletColor;
        final Color gc = glowColor;
        final Color cc = coreColor;
        final float hw = helixWidth;
        final float hr = helixRadius;

        // Два промежуточных контрольных точки для кубической кривой Безье —
        // каждый прыжок гнётся сильнее предыдущего
        float jit  = 22f + hopIndex * 12f;
        float midX1 = (x1 * 2 + x2) / 3f + Mathf.range(jit);
        float midY1 = (y1 * 2 + y2) / 3f + Mathf.range(jit);
        float midX2 = (x1 + x2 * 2) / 3f + Mathf.range(jit);
        float midY2 = (y1 + y2 * 2) / 3f + Mathf.range(jit);

        float thick = hw * (1.3f - hopIndex * 0.09f);

        new Effect(32f + hopIndex * 4f, e -> {
            float p   = e.fin();
            float fot = e.fout();

            // ── Главная дуга ───────────────────────────────────────
            // Нарисуем три параллельных нити для объёма
            int segs = 16;
            for (int layer = 0; layer < 3; layer++) {
                float layerThick = thick * (1f - layer * 0.28f) * fot;
                Color layerCol   = layer == 0 ? c : (layer == 1 ? gc : cc);
                float layerAlpha = (layer == 0 ? 0.9f : layer == 1 ? 0.6f : 0.35f) * fot;
                float offset     = (layer - 1) * (thick * 0.5f); // смещение нити

                Draw.color(layerCol, layerAlpha);
                Lines.stroke(layerThick);

                float lx = x1, ly = y1;
                for (int i = 1; i <= segs; i++) {
                    float t  = i / (float) segs;
                    float mt = 1f - t;
                    // Кубический Безье
                    float px = mt*mt*mt*x1 + 3*mt*mt*t*midX1 + 3*mt*t*t*midX2 + t*t*t*x2;
                    float py = mt*mt*mt*y1 + 3*mt*mt*t*midY1 + 3*mt*t*t*midY2 + t*t*t*y2;
                    // Боковое смещение перпендикулярно сегменту
                    float dx = px - lx, dy = py - ly;
                    float len = Mathf.sqrt(dx*dx + dy*dy);
                    if (len > 0.001f) {
                        float nx = -dy / len * offset;
                        float ny =  dx / len * offset;
                        Lines.line(lx + nx, ly + ny, px + nx, py + ny);
                    }
                    lx = px; ly = py;
                }
            }

            // ── Искры вдоль дуги ───────────────────────────────────
            if (p < 0.5f && hopIndex < 4) {
                float spark = (0.5f - p) / 0.5f;
                Angles.randLenVectors(e.id + hopIndex * 100, 3 + hopIndex,
                    hr * (0.5f + p * 2f),
                    (ox, oy) -> {
                        Draw.color(gc, spark * fot * 0.9f);
                        Fill.circle(
                            (x1 + x2) / 2f + ox,
                            (y1 + y2) / 2f + oy,
                            hw * 0.55f * spark
                        );
                    });
            }

            // ── Кольцо на цели (второй конец дуги) ────────────────
            if (p > 0.2f) {
                float rp = (p - 0.2f) / 0.8f;
                Draw.color(c, (1f - rp) * fot * 0.8f);
                Lines.stroke(thick * 0.6f * fot);
                Lines.circle(x2, y2, hr * (0.5f + rp * 2f));

                Draw.color(gc, (1f - rp) * fot * 0.5f);
                Fill.circle(x2, y2, hr * 0.8f * (1f - rp) * fot);
            }

            // ── Свет вдоль всей дуги ──────────────────────────────
            Drawf.light(x1, y1, x2, y2, thick * 4f, c, 0.55f * fot);
            Drawf.light(x2, y2, hr * 5f * fot, gc, 0.5f * fot);

        }).at(x1, y1);
    }
}