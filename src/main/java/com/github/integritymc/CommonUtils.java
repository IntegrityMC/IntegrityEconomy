/*
 * IntegrityEconomy - Copyright (C) 2026 IntegrityMC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.github.integritymc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class CommonUtils {
	public static String currency(double amount) {
		return amount>1 ? Main.getInstance().getConfig().getString("Economy.currencyPlural") : Main.getInstance().getConfig().getString("Economy.currencySingular");
	}
	
	public static String formatAmount(double balance) {
        int decimalPlaces = Main.getInstance().getConfig().getInt("Economy.decimal-places", 2);
        String formatString = String.format("%%.%df", decimalPlaces);
		return ((balance % 1 == 0) || (decimalPlaces <= 0)) ? String.valueOf((int) balance) : String.format(formatString, balance);
	}

    public static LegacyComponentSerializer legacyBuilder() {
        return LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    }

    public static List<String> convertLore(List<Component> adventureLore) {
        return adventureLore.stream()
                .map(component -> legacyBuilder().serialize(component))
                .collect(Collectors.toList());
    }

    public static ItemStack itemBuilder(Material material, Component name, List<Component> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (name != null) meta.setDisplayName(legacyBuilder().serialize(name));
        if (lore != null) meta.setLore(convertLore(lore));
        item.setItemMeta(meta);
        return item;
    }

    public static List<Component> stringToMiniMessage(List<String> lore) {
        return lore.stream()
                .map(MiniMessage.miniMessage()::deserialize)
                .collect(Collectors.toList());
    }

    public static String processString(String message) {
        return (message != null) ? message : "";
    }
}
