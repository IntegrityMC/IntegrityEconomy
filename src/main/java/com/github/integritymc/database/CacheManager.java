/*
 * IntegrityEconomy - Copyright (C) 2026 IntegrityMC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.github.integritymc.database;

import com.github.integritymc.Main;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;

public class CacheManager {
    @Getter private final HashMap<OfflinePlayer, UserProfile> playerCache = new HashMap<>();

    public CacheManager() {
        Bukkit.getOnlinePlayers().forEach(this::playerJoin);
    }

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
        if (!playerCache.containsKey(offlinePlayer)) return Main.getDataManager().getBalance(offlinePlayer);
        return playerCache.get(offlinePlayer).balance();
    }

    public void updateBalance(OfflinePlayer offlinePlayer, double amount) {
        if (!playerCache.containsKey(offlinePlayer)) {
            if (!Main.getDataManager().existsPlayer(offlinePlayer)) return;
            Main.getDataManager().updateBalance(offlinePlayer, amount);
            return;
        }
        playerCache.put(offlinePlayer, UserProfile.builder().playerName(offlinePlayer.getName()).balance(amount).build());
    }

    public void resetBalance(OfflinePlayer offlinePlayer) {
        if (!playerCache.containsKey(offlinePlayer)) {
            if (!Main.getDataManager().existsPlayer(offlinePlayer)) return;
            Main.getDataManager().updateBalance(offlinePlayer, 0);
            return;
        }
        playerCache.put(offlinePlayer, UserProfile.builder().playerName(offlinePlayer.getName()).balance(0).build());
    }

    public boolean existPlayer(OfflinePlayer offlinePlayer) {
        if (!playerCache.containsKey(offlinePlayer))
            return Main.getDataManager().existsPlayer(offlinePlayer);
        return true;
    }
}
