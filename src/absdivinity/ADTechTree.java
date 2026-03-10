package absdivinity.content;

import mindustry.content.*;
import mindustry.game.Objectives.Research;
import mindustry.type.*;
import mindustry.content.TechTree.TechNode;
import absdivinity.*;
import absdivinity.items.ADItems;
import arc.struct.Seq;

public class ADTechTree {
    public static void load(){

        // Находим узел coreNucleus в дереве Serpulo
        TechNode parent = TechTree.all.find(n -> n.content == Blocks.coreNucleus);
        if(parent == null) return; // защита на случай если не найден

        // Создаём корень ветки мода вручную
        TechNode energyCoreNode = new TechNode(parent, AbsDivinity.energyCore, ItemStack.with(
            Items.thorium, 200,
            Items.surgeAlloy, 150
        ));
        energyCoreNode.objectives = Seq.with(new Research(Blocks.coreNucleus));
        parent.children.add(energyCoreNode);

        // --- Ветка добычи ---
        TechNode rayDrillNode = new TechNode(energyCoreNode, AbsDivinity.rayDrill, ItemStack.with(
            Items.titanium, 150,
            Items.thorium, 80
        ));
        rayDrillNode.objectives = Seq.with(new Research(AbsDivinity.energyCore));
        energyCoreNode.children.add(rayDrillNode);

        TechNode rangeDrillNode = new TechNode(rayDrillNode, AbsDivinity.rangeDrill, ItemStack.with(
            Items.surgeAlloy, 100,
            Items.phaseFabric, 80
        ));
        rangeDrillNode.objectives = Seq.with(new Research(AbsDivinity.rayDrill));
        rayDrillNode.children.add(rangeDrillNode);

        // --- Ветка турелей ---
        TechNode omniTurretNode = new TechNode(energyCoreNode, AbsDivinity.omniTurret, ItemStack.with(
            Items.surgeAlloy, 100,
            Items.silicon, 150
        ));
        omniTurretNode.objectives = Seq.with(new Research(AbsDivinity.energyCore));
        energyCoreNode.children.add(omniTurretNode);

        TechNode baHionNode = new TechNode(omniTurretNode, AbsDivinity.baHion, ItemStack.with(
            Items.surgeAlloy, 200,
            Items.phaseFabric, 100
        ));
        baHionNode.objectives = Seq.with(new Research(AbsDivinity.omniTurret));
        omniTurretNode.children.add(baHionNode);

        // --- Ветка спецблоков ---
        TechNode outpostNode = new TechNode(energyCoreNode, AbsDivinity.outpostNode, ItemStack.with(
            Items.surgeAlloy, 180,
            Items.silicon, 400
        ));
        outpostNode.objectives = Seq.with(new Research(AbsDivinity.energyCore));
        energyCoreNode.children.add(outpostNode);

        TechNode endgameOdNode = new TechNode(outpostNode, AbsDivinity.endgameOd, ItemStack.with(
            Items.surgeAlloy, 100,
            Items.phaseFabric, 150
        ));
        endgameOdNode.objectives = Seq.with(new Research(AbsDivinity.outpostNode));
        outpostNode.children.add(endgameOdNode);

        // Регистрируем все узлы в глобальном списке
        TechTree.all.addAll(
            energyCoreNode, rayDrillNode, rangeDrillNode,
            omniTurretNode, baHionNode,
            outpostNode, endgameOdNode
        );
    }
}