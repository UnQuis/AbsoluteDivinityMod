package absdivinity.entities.units;

import mindustry.gen.*;

public class EnergyDroneUnit extends UnitEntity {
    @Override
    public void rawDamage(float amount){
        float maxDmg = maxHealth * 0.05f;
        super.rawDamage(Math.min(amount, maxDmg));
    }
}