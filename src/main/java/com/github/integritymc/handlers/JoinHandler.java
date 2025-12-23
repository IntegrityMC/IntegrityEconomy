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
