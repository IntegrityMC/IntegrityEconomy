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

package com.github.integritymc;

import com.github.integritymc.commands.BalTopCommand;
import com.github.integritymc.commands.BalanceCommand;
import com.github.integritymc.commands.PayCommand;
import com.github.integritymc.commands.admin.EconomyCommand;
import com.github.integritymc.database.CacheManager;
import com.github.integritymc.database.DataManager;
import com.github.integritymc.handlers.InteractionHandler;
import com.github.integritymc.handlers.JoinHandler;
import com.github.integrityupdate.IntegrityUpdater;
import it.mcnaples.schedulerapi.SchedulerAPI;
import it.mcnaples.schedulerapi.scheduler.Scheduler;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Getter private static Main instance;
    @Getter private static BukkitAudiences adventure;
    @Getter private static DataManager dataManager;
    @Getter private static Economy economy;
    @Getter private static Scheduler scheduler;
    @Getter private static CacheManager cacheManager;

    public @NonNull BukkitAudiences adventure() {
        if (adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }

    @Override
    public void onEnable() {
        instance = this;
        adventure = BukkitAudiences.create(this);
        scheduler = SchedulerAPI.init(this);

        saveDefaultConfig();

        IntegrityUpdater.init(this);

        if (getConfig().getBoolean("Settings.updater", true))
            IntegrityUpdater.getUpdateChecker().updateMessage();
        
        if (getConfig().getBoolean("Settings.bstats", true))
            new Metrics(this, 27071);
        
        dataManager = new DataManager();
        dataManager.init();

        Bukkit.getServicesManager().register(Economy.class, new VaultDialer(), instance, ServicePriority.Highest); // Highest priority for Override others plugins
        if (!setupEconomy()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //CACHE MANAGER
        cacheManager = new CacheManager();
        cacheManager.update();

        //REGISTER COMMANDS
        addCommand("balance", new BalanceCommand(), new BalanceCommand());
        addCommand("eco", new EconomyCommand(), new EconomyCommand());
        addCommand("pay", new PayCommand(), new PayCommand());
        addCommand("baltop", new BalTopCommand(), new BalTopCommand());

        //REGISTER EVENTS
        getServer().getPluginManager().registerEvents(new JoinHandler(), this);
        getServer().getPluginManager().registerEvents(new InteractionHandler(), this);
    }

    @Override
    public void onDisable() {
        IntegrityUpdater.terminate();

        cacheManager.terminate();
        dataManager.terminate();
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    private void addCommand(String name, CommandExecutor executor, TabCompleter completer) {
        if (executor != null)
            getCommand(name).setExecutor(executor);

        if (completer != null)
            getCommand(name).setTabCompleter(completer);
    }

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
