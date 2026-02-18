/*
 * IntegrityEconomy - Copyright (C) 2026 IntegrityMC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.github.integritymc.commands;

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

public class BalanceCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0 && (sender instanceof Player player)) {
            Main.getAdventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.your-balance", "{prefix} <gradient:#c2ccff:#d6e6ff>You have {balance} {currency}</gradient>")
                    .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                    .replace("{balance}", CommonUtils.formatAmount(Main.getEconomy().getBalance(player)))
                    .replace("{currency}", CommonUtils.currency(Main.getEconomy().getBalance(player)))
            ));
        } else if (args.length == 1) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
            String playerName = player.getName();

            if (playerName == null) {
                Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.player-does-not-exist", "{prefix} <gradient:#c2ccff:#d6e6ff>This player does not exist!</gradient>")
                        .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                ));
                return true;
            }

            Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.other-balance", "{prefix} <gradient:#c2ccff:#d6e6ff>{player} have {balance} {currency}</gradient>")
                    .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                    .replace("{player}", playerName)
                    .replace("{balance}", CommonUtils.formatAmount(Main.getEconomy().getBalance(player)))
                    .replace("{currency}", CommonUtils.currency(Main.getEconomy().getBalance(player)))
            ));
        } else {
            Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.usage-balance", "{prefix} <gradient:#c2ccff:#d6e6ff>Usage: /balance [player]</gradient>")
                    .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
            ));
        }
        
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> playerList = new ArrayList<>();
        if (args.length == 1) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                playerList.add(players.getName());
            }
            return playerList;
        }
        
        return List.of();
    }
}
