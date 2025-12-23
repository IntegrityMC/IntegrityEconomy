/*
 * IntegrityEconomy - Copyright (C) 2025 IntegrityMC
 *
 * This file is part of IntegrityEconomy.
 *
 * IntegrityEconomy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * IntegrityEconomy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with IntegrityEconomy.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.integritymc.commands.admin;

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
        // /eco <give/reset/remove> <player> [amount]
        if (!sender.hasPermission("integrity.eco")) {
            Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.no-permission")));
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);

            if (!Main.getEconomy().hasAccount(player)) {
                Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.player-does-not-exist")));
                return true;
            }
            
            Main.getCacheManager().resetBalance(player);
            Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.eco-reset")
                .replace("{player}", player.getName())
            ));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.invalid-number", "<red>Invalid number formatting!")));
                return true;
            }

            if (!Main.getEconomy().hasAccount(player)) {
                Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.player-does-not-exist")));
                return true;
            }

            Main.getEconomy().depositPlayer(player, amount);
            Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.eco-give")
                .replace("{player}", player.getName())
                .replace("{amount}", String.valueOf(amount))
            ));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.invalid-number", "<red>Invalid number formatting!")));
                return true;
            }

            if (!Main.getEconomy().hasAccount(player)) {
                Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.player-does-not-exist")));
                return true;
            }

            if (!Main.getEconomy().has(player, amount)) {
                Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.not-enough")
                    .replace("{player}", player.getName())
                ));
                return true;
            }

            Main.getEconomy().withdrawPlayer(player, amount);
            Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.eco-remove")
                .replace("{player}", player.getName())
                .replace("{amount}", String.valueOf(amount))
            ));
        } else {
            Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.usage-eco")));
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> playerList = new ArrayList<>();
        if (!sender.hasPermission("integrity.eco"))
            return List.of();
        
        if (args.length == 1) {
            return List.of("give", "remove", "reset");
        } else if (args.length == 2) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                playerList.add(players.getName());
            }
            return playerList;
        }
        
        return List.of();
    }
}
