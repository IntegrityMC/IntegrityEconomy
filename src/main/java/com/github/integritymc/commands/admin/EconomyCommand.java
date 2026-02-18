/*
 * IntegrityEconomy - Copyright (C) 2026 IntegrityMC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.github.integritymc.commands.admin;

import com.github.integritymc.CommonUtils;
import com.github.integritymc.Main;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EconomyCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        // /eco <give/reset/remove/set> <player> [amount]
        if (!sender.hasPermission("integrity.eco")) {
            Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.no-permission", "{prefix} <gradient:#c2ccff:#d6e6ff>You don't have the permission!</gradient>")
                    .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
            ));
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            String playerName = player.getName();

            if (playerName == null || !Main.getEconomy().hasAccount(player)) {
                Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.player-does-not-exist", "{prefix} <gradient:#c2ccff:#d6e6ff>This player does not exist!</gradient>")
                        .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                ));
                return true;
            }
            
            Main.getCacheManager().resetBalance(player);
            Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.eco-reset", "{prefix} <gradient:#c2ccff:#d6e6ff>{player}'s balance has been resetted</gradient>")
                    .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                    .replace("{player}", playerName)
            ));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            String playerName = player.getName();
            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.invalid-number", "{prefix} <gradient:#c2ccff:#d6e6ff>Invalid number formatting!</gradient>")
                        .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                ));
                return true;
            }

            if (playerName == null || !Main.getEconomy().hasAccount(player)) {
                Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.player-does-not-exist", "{prefix} <gradient:#c2ccff:#d6e6ff>This player does not exist!</gradient>")
                        .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                ));
                return true;
            }

            Main.getEconomy().depositPlayer(player, amount);
            Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.eco-give", "{prefix} <gradient:#c2ccff:#d6e6ff>You gave {player} {amount} {currency}!</gradient>")
                    .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                    .replace("{player}", playerName)
                    .replace("{amount}", CommonUtils.formatAmount(amount))
                    .replace("{currency}", CommonUtils.currency(amount))
            ));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            String playerName = player.getName();
            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.invalid-number", "{prefix} <gradient:#c2ccff:#d6e6ff>Invalid number formatting!</gradient>")
                        .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                ));
                return true;
            }

            if (playerName == null || !Main.getEconomy().hasAccount(player)) {
                Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.player-does-not-exist", "{prefix} <gradient:#c2ccff:#d6e6ff>This player does not exist!</gradient>")
                        .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                ));
                return true;
            }

            if (!Main.getEconomy().has(player, amount)) {
                Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.not-enough", "{prefix} <gradient:#c2ccff:#d6e6ff>{player} does not have money!</gradient>")
                        .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                        .replace("{player}", playerName)
                ));
                return true;
            }

            Main.getEconomy().withdrawPlayer(player, amount);
            Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.eco-remove", "{prefix} <gradient:#c2ccff:#d6e6ff>You took {amount} {currency} from {player}!</gradient>")
                    .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                    .replace("{player}", playerName)
                    .replace("{amount}", CommonUtils.formatAmount(amount))
                    .replace("{currency}", CommonUtils.currency(amount))
            ));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            String playerName = player.getName();
            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.invalid-number", "{prefix} <gradient:#c2ccff:#d6e6ff>Invalid number formatting!</gradient>")
                        .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                ));
                return true;
            }

            if (playerName == null || !Main.getEconomy().hasAccount(player)) {
                Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.player-does-not-exist", "{prefix} <gradient:#c2ccff:#d6e6ff>This player does not exist!</gradient>")
                        .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                ));
                return true;
            }

            Main.getCacheManager().updateBalance(player, amount);
            Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.eco-set", "{prefix} <gradient:#c2ccff:#d6e6ff>You set {amount} {currency} for {player}!</gradient>")
                    .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                    .replace("{player}", playerName)
                    .replace("{amount}", CommonUtils.formatAmount(amount))
                    .replace("{currency}", CommonUtils.currency(amount))
            ));
        } else {
            Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.usage-eco", "{prefix} <gradient:#c2ccff:#d6e6ff>Usage: /eco <give/remove/reset/set> <player> [amount]</gradient>")
                    .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
            ));
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> playerList = new ArrayList<>();
        if (!sender.hasPermission("integrity.eco"))
            return List.of();
        
        if (args.length == 1) {
            return List.of("give", "remove", "reset", "set");
        } else if (args.length == 2) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                playerList.add(players.getName());
            }
            return playerList;
        }
        
        return List.of();
    }
}
