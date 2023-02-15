package com.xiii.wave.utils.custom;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.xiii.wave.utils.MiscUtils;
import com.xiii.wave.utils.versionutils.VersionUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Equipment {

    private ItemStack[] armorContents = new ItemStack[3];

    private int depthStriderLevel, frostWalkerLevel, soulSpeedLevel;

    private int ticks;

    public void handle(Player player) {

        if (this.ticks++ < 5) return;

        this.armorContents = player.getInventory().getArmorContents();

        boots:
        {

            ItemStack boots = getBoots();

            if (boots == MiscUtils.EMPTY_ITEM) break boots;

            this.depthStriderLevel = boots.getEnchantmentLevel(Enchantment.DEPTH_STRIDER);

            this.frostWalkerLevel = VersionUtils.getServerVersion().isOlderThan(ServerVersion.V_1_13_1) ? 0 : boots.getEnchantmentLevel(Enchantment.FROST_WALKER);

            this.soulSpeedLevel = VersionUtils.getServerVersion().isOlderThan(ServerVersion.V_1_16_1) ? 0 : boots.getEnchantmentLevel(Enchantment.SOUL_SPEED);
        }

        this.ticks = 0;
    }

    public ItemStack getBoots() {
        return this.armorContents[0] != null ? this.armorContents[0] : MiscUtils.EMPTY_ITEM;
    }

    public ItemStack getLeggings() {
        return this.armorContents[1] != null ? this.armorContents[1] : MiscUtils.EMPTY_ITEM;
    }

    public ItemStack getChestplate() {
        return this.armorContents[2] != null ? this.armorContents[2] : MiscUtils.EMPTY_ITEM;
    }

    public ItemStack getHelmet() {
        return this.armorContents[3] != null ? this.armorContents[3] : MiscUtils.EMPTY_ITEM;
    }

    public int getDepthStriderLevel() {
        return depthStriderLevel;
    }

    public int getFrostWalkerLevel() {
        return frostWalkerLevel;
    }

    public int getSoulSpeedLevel() {
        return soulSpeedLevel;
    }

    public ItemStack[] getArmorContents() {
        return armorContents;
    }
}
