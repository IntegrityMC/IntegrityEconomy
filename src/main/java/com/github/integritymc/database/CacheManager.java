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

package com.github.integritymc.database;

import com.github.integritymc.Main;
import lombok.Getter;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;

public class CacheManager {
    @Getter private final HashMap<OfflinePlayer, UserProfile> playerCache = new HashMap<>();

    public void playerJoin(OfflinePlayer offlinePlayer) {
        Main.getScheduler().runTaskAsynchronously(() -> {
            if (!Main.getDataManager().existsPlayer(offlinePlayer)) return;

            playerCache.put(offlinePlayer, UserProfile.builder().playerName(offlinePlayer.getName()).balance(Main.getDataManager().getBalance(offlinePlayer)).build());
        });
    }

    public void playerQuit(OfflinePlayer offlinePlayer) {
        Main.getScheduler().runTaskAsynchronously(() -> {
            if (!playerCache.containsKey(offlinePlayer)) return;
            Main.getDataManager().updateBalance(offlinePlayer, getBalance(offlinePlayer));
            playerCache.remove(offlinePlayer);
        });
    }

    public void terminate() {
        for (OfflinePlayer offlinePlayer : playerCache.keySet()) {
            if (!playerCache.containsKey(offlinePlayer)) return;
            Main.getDataManager().updateBalance(offlinePlayer, getBalance(offlinePlayer));
            playerCache.remove(offlinePlayer);
        }
    }

    public void update() {
        Main.getScheduler().runTaskTimerAsynchronously(() -> {
            for (OfflinePlayer offlinePlayer : playerCache.keySet()) {
                Main.getDataManager().updateBalance(offlinePlayer, getBalance(offlinePlayer));
            }
        }, 1, Main.getInstance().getConfig().getLong("Economy.save-minutes")*20*60);
    }

    public double getBalance(OfflinePlayer offlinePlayer) {
        if (!playerCache.containsKey(offlinePlayer)) return 0.0;
        return playerCache.get(offlinePlayer).balance();
    }

    public void updateBalance(OfflinePlayer offlinePlayer, double amount) {
        if (!playerCache.containsKey(offlinePlayer)) return;
        playerCache.put(offlinePlayer, UserProfile.builder().playerName(offlinePlayer.getName()).balance(amount).build());
    }

    public void resetBalance(OfflinePlayer offlinePlayer) {
        if (!playerCache.containsKey(offlinePlayer)) return;
        playerCache.put(offlinePlayer, UserProfile.builder().playerName(offlinePlayer.getName()).balance(0).build());
    }
}
