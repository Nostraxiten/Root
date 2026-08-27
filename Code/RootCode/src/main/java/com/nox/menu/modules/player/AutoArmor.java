package com.nox.menu.modules.player;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.ModeSetting;
import com.nox.menu.core.setting.NumberSetting;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ContainerInput;

public class AutoArmor extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Legit", "Legit", "Silent");
    private final NumberSetting delay = new NumberSetting("Delay Ticks", 5.0, 0.0, 20.0, 1.0);
    private final BooleanSetting ignoreDurability = new BooleanSetting("Ignore Durability", false);
    private final NumberSetting durabilityThreshold = new NumberSetting("Durability %", 20.0, 1.0, 100.0, 1.0);
    private final BooleanSetting repairSwap = new BooleanSetting("Repair Swap", true);

    private int tickCounter = 0;
    private long lastActionTime = 0;

    private static final EquipmentSlot[] ARMOR_SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public AutoArmor() {
        super("AutoArmor", "Auto-equipa la mejor armadura disponible | Auto-equips the best available armor", Category.PLAYER);
        this.addSetting(this.mode);
        this.addSetting(this.delay);
        this.addSetting(this.ignoreDurability);
        this.addSetting(this.durabilityThreshold);
        this.addSetting(this.repairSwap);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) return;
        
        // No equipar si hay contenedores abiertos, a menos que sea el propio inventario
        if (mc.gui.screen() != null && !(mc.gui.screen() instanceof InventoryScreen)) {
            return;
        }

        tickCounter++;
        if (tickCounter < this.delay.getIntValue()) {
            return;
        }

        boolean isLegit = this.mode.is("Legit");
        if (isLegit && System.currentTimeMillis() - lastActionTime < (80 + Math.random() * 100)) {
            return;
        }

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            int bestSlot = findBestArmorForSlot(slot);
            if (bestSlot != -1) {
                equipArmor(bestSlot, slot, isLegit);
                tickCounter = 0;
                lastActionTime = System.currentTimeMillis();
                
                if (isLegit) return; // Solo una acción por tick en modo legit
            }
        }
    }

    private int findBestArmorForSlot(EquipmentSlot slot) {
        int bestSlot = -1;
        double bestScore = -1;
        ItemStack currentArmor = mc.player.getItemBySlot(slot);
        double currentScore = getArmorScore(currentArmor);
        
        boolean currentNeedsRepair = false;
        if (!currentArmor.isEmpty() && currentArmor.isDamageableItem()) {
            double durabilityPct = ((double)(currentArmor.getMaxDamage() - currentArmor.getDamageValue()) / currentArmor.getMaxDamage()) * 100.0;
            if (!this.ignoreDurability.isEnabled() && durabilityPct < this.durabilityThreshold.getValue()) {
                currentNeedsRepair = true;
            }
        }

        // Inventario principal (9 a 35) y Hotbar (0 a 8)
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack.isEmpty() || !(stack.get(DataComponents.EQUIPPABLE) != null)) continue;
            
            Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
            if (equippable == null || equippable.slot() != slot) continue;

            if (!this.ignoreDurability.isEnabled() && stack.isDamageableItem()) {
                double durabilityPct = ((double)(stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage()) * 100.0;
                if (durabilityPct < this.durabilityThreshold.getValue()) {
                    continue;
                }
            }

            double score = getArmorScore(stack);
            
            if (score > bestScore) {
                bestScore = score;
                bestSlot = i;
            }
        }

        if (bestSlot != -1) {
            if (bestScore > currentScore || (currentNeedsRepair && this.repairSwap.isEnabled() && bestScore >= currentScore)) {
                return bestSlot;
            }
        }

        return -1;
    }

    private double getArmorScore(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.get(DataComponents.EQUIPPABLE) != null)) {
            return -1;
        }
        
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) return -1;
        
        double[] score = {0};
        ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (modifiers != null) {
            modifiers.forEach(equippable.slot(), (attribute, modifier) -> {
                if (attribute.equals(Attributes.ARMOR) || 
                    attribute.equals(Attributes.ARMOR_TOUGHNESS)) {
                    score[0] += modifier.amount();
                }
            });
        }
        
        if (stack.isEnchanted()) {
            score[0] += stack.getEnchantments().size() * 1.5; 
        }
        
        return score[0];
    }

    private void equipArmor(int inventorySlot, EquipmentSlot equipmentSlot, boolean isLegit) {
        int syncId = mc.player.inventoryMenu.containerId;

        int screenSlot = inventorySlot < 9 ? inventorySlot + 36 : inventorySlot;

        // Mapeo inverso de slots de equipamiento
        int armorScreenSlot = 8 - equipmentSlot.getIndex();

        ItemStack currentArmor = mc.player.getItemBySlot(equipmentSlot);

        if (!currentArmor.isEmpty()) {
            mc.gameMode.handleContainerInput(syncId, armorScreenSlot, 0, ContainerInput.QUICK_MOVE, mc.player);
            mc.gameMode.handleContainerInput(syncId, screenSlot, 0, ContainerInput.QUICK_MOVE, mc.player);
        } else {
            mc.gameMode.handleContainerInput(syncId, screenSlot, 0, ContainerInput.QUICK_MOVE, mc.player);
        }
    }
}
