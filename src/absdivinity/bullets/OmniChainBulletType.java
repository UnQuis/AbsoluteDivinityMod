package absdivinity.bullets;

import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;

public class OmniChainBulletType extends BulletType{

    public int maxHit = 5;
    public float chainRange = 100f;
    public float thick = 2f;
    public Color chainColor = Color.white;

    protected static Seq<Healthc> hitUnits = new Seq<>();

    public OmniChainBulletType(float damage, float charge, Color color) {
        this.damage = damage;
        this.chainColor = color;
        this.instantDisappear = false;

        this.maxHit = (int)(2 + charge * 12);
        this.chainRange = 80f + (charge * 120f);
        this.thick = 1f + (charge * 2f);

        this.speed = 10f;
        this.lifetime = 20f;
    }

    @Override
    public void draw(Bullet b) {
        Draw.color(chainColor);
        Fill.circle(b.x, b.y, thick * 1.5f);

        Draw.alpha(0.3f);
        Fill.circle(b.x, b.y, thick * 3f);
        Draw.reset();
    }

    @Override
    public void hit (Bullet b, float x, float y) {
        super.hit(b, x, y);

        hitUnits.clear();
        float curX = x, curY = y;

        for(int i = 0; i < maxHit; i++) {
            Teamc target = Units.closestEnemy(b.team, curX, curY, chainRange, u -> !hitUnits.contains(u));

            if(target instanceof Healthc) {
                Healthc unit = (Healthc)target;

                createChainEffect(curX, curY, unit.x(), unit.y(), chainColor, thick);

                unit.damage(damage);
                hitUnits.add(unit);

                curX = unit.x();
                curY = unit.y();
            } else {
                break;
            }
        }
    }


    public void createChainEffect(float x1, float y1, float x2, float y2, Color color, float thick) {

        Effect chainEffect = new Effect(20f, e -> {
            float lastX = x1, lastY = y1;
            int segments = 10;

            float midX = (x1 + x2) / 2f + Mathf.range(15f);
            float midY = (y1 + y2) / 2f + Mathf.range(15f);

            Draw.color(color);
            Lines.stroke(thick * e.fout());

            for (int i = 1; i <= segments; i++) {
                float t = i / (float)segments;
                float px = (1-t)*(1-t)*x1 + 2*(1-t)*t*midX + t*t*x2;
                float py = (1-t)*(1-t)*y1 + 2*(1-t)*t*midY + t*t*y2;

                Lines.line(lastX, lastY, px, py);
                lastX = px; lastY = py;
            }
        });
        
        chainEffect.at(x1, y1);
    }
}