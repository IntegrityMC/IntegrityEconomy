/*
 * IntegrityEconomy - Copyright (C) 2026 IntegrityMC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.github.integritymc.handlers;

import com.github.integritymc.Main;
import com.github.integrityupdate.IntegrityUpdater;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinHandler implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Main.getDataManager().createAccount(event.getPlayer(), Main.getInstance().getConfig().getDouble("Economy.first-join-money", 200));
        Main.getCacheManager().playerJoin(event.getPlayer());

        IntegrityUpdater.getUpdateChecker().isLatest(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Main.getCacheManager().playerQuit(event.getPlayer());
    }
}
