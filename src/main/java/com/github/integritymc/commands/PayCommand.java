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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PayCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // /pay <player> <amount>
        if (!(sender instanceof Player player)) {
            Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize("<red>You cannot execute this command on console!</red>"));
            return true;
        }

        if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[0]);
            double amount;
            try {
                amount = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                Main.getAdventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.invalid-number", "{prefix} <gradient:#c2ccff:#d6e6ff>Invalid number formatting!</gradient>")
                        .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                ));
                return true;
            }
            pay(player, target, amount);
        }

        return false;
    }

    public static void pay(Player sender, Player target, double amount) {
        if (target == null || !target.isOnline()) {
            Main.getAdventure().player(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.player-offline", "{prefix} <gradient:#c2ccff:#d6e6ff>The player is offline!</gradient>")
                    .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
            ));
            return;
        }

        if (sender.getName().equals(target.getName())) {
            Main.getAdventure().player(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.pay-try-self", "{prefix} <gradient:#c2ccff:#d6e6ff>You cannot pay yourself!</gradient>")
                    .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
            ));
            return;
        }

        if (!Main.getEconomy().hasAccount(target)) {
            Main.getAdventure().player(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.player-does-not-exist", "<red>This player doesn't exist!")
                    .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
            ));
            return;
        }

        if (!Main.getEconomy().has(target, amount)) {
            Main.getAdventure().player(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.not-enough", "{prefix} <gradient:#c2ccff:#d6e6ff>{player} does not have money!</gradient>")
                    .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                    .replace("{player}", target.getName())
            ));
            return;
        }

        Main.getEconomy().withdrawPlayer(sender, amount);
        Main.getEconomy().depositPlayer(target, amount);

        Main.getAdventure().player(sender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.pay-self", "{prefix} <gradient:#c2ccff:#d6e6ff>You paid {player} {amount} {currency}!</gradient>")
                .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                .replace("{player}", target.getName())
                .replace("{amount}", CommonUtils.formatAmount(amount))
                .replace("{currency}", CommonUtils.currency(amount))
        ));
        Main.getAdventure().player(target).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.pay-other", "{prefix} <gradient:#c2ccff:#d6e6ff>You received {amount} {currency} from {player}!</gradient>")
                .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                .replace("{player}", sender.getName())
                .replace("{amount}", CommonUtils.formatAmount(amount))
                .replace("{currency}", CommonUtils.currency(amount))
        ));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> playerList = new ArrayList<>();

        if (args.length == 1) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                playerList.add(players.getName());
            }
            return playerList;
        } else if (args.length == 2) {
            return List.of("10", "100", "1000");
        }
        return List.of();
    }
}
