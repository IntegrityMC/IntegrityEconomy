/*
 * IntegrityEconomy - Copyright (C) 2026 IntegrityMC
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

package com.github.integritymc.commands;

import com.github.integritymc.CommonUtils;
import com.github.integritymc.Main;
import com.github.integritymc.database.UserProfile;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BalTopCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length > 1) {
            Main.getAdventure().sender(commandSender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.usage-baltop", "{prefix} <gradient:#c2ccff:#d6e6ff>Usage: /baltop [page]</gradient>")
                    .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
            ));
            return true;
        }
        int max_pages = Main.getInstance().getConfig().getInt("Functions.BalTop.max-pages", 10);
        int pages = (strings.length == 0 || Integer.parseInt(strings[0]) == 0) ? 1 : Integer.parseInt(strings[0]);
        List<UserProfile> userProfiles = Main.getDataManager().getAllAccounts().stream().sorted(Comparator.comparing(UserProfile::balance).reversed()).toList();

        if (pages>max_pages) {
            Main.getAdventure().sender(commandSender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.exceeded-page", "{prefix} <gradient:#c2ccff:#d6e6ff>You have exceeded the maximum page!</gradient>")
                    .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
            ));
            return true;
        }

        String header = Main.getInstance().getConfig().getString("Messages.baltop-header", "{prefix} <gradient:#c2ccff:#d6e6ff><b>BALANCE TOP</b> • {page}/{max-pages}</gradient>");
        if (!header.isBlank()) {
            Main.getAdventure().sender(commandSender).sendMessage(MiniMessage.miniMessage().deserialize(header
                    .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                    .replace("{page}", String.valueOf(pages))
                    .replace("{max-pages}", String.valueOf(max_pages))
            ));
        }

        int max_players = userProfiles.size();
        int players = Main.getInstance().getConfig().getInt("Functions.BalTop.player-per-pages", 10);
        for (int i=0; i<players; i++) {
            int user = ((pages-1)*players)+i;
            if (max_players<=user) break;
            UserProfile userProfile = userProfiles.get(user);
            Main.getAdventure().sender(commandSender).sendMessage(MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.baltop-player-format", "<gradient:#c2ccff:#d6e6ff> <color:#8291ff>•</color> {position} <color:#8291ff>→</color> {player} {balance} {currency}</gradient>")
                    .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                    .replace("{position}", String.valueOf(i+1))
                    .replace("{player}", userProfile.playerName())
                    .replace("{balance}", CommonUtils.formatAmount(userProfile.balance()))
                    .replace("{currency}", CommonUtils.currency(userProfile.balance()))
            ));
        }

        String footer = Main.getInstance().getConfig().getString("Messages.baltop-footer", "");
        if (!footer.isBlank()) {
            Main.getAdventure().sender(commandSender).sendMessage(MiniMessage.miniMessage().deserialize(footer
                    .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                    .replace("{page}", String.valueOf(pages))
                    .replace("{max-pages}", String.valueOf(max_pages))
            ));
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            ArrayList<String> list = new ArrayList<>();
            for (int i=1; i<=Main.getInstance().getConfig().getInt("Functions.BalTop.max-pages"); i++) {
                list.add(String.valueOf(i));
            }
            return list;
        }
        return List.of();
    }
}
