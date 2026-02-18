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
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class DataManager {
    @Getter private Connection connection;
    private final Object sqliteSupport = new Object();
    private String typeData;

    public void init() {
        typeData = Main.getInstance().getConfig().getString("Database.type", "local");
        Main.getInstance().getLogger().log(Level.INFO, "Loading database! -> Pending");

        if (typeData.equalsIgnoreCase("local"))
            local();
        else if (typeData.equalsIgnoreCase("mysql"))
            mySql();
        else
            invalid();
    }

    @SneakyThrows
    public void terminate() {
        if (getConnection() != null && !getConnection().isClosed())
            getConnection().close();
    }

    @SneakyThrows
    private void local() {
        Main.getInstance().getLogger().log(Level.INFO, "Database loaded successfully! -> sqlite");
        Class.forName("org.sqlite.JDBC");

        File file = new File(Main.getInstance().getDataFolder(), "storage.db");

        if (!file.exists())
            file.createNewFile();

        String jdbcUrl = "jdbc:sqlite:" + file.getAbsolutePath();
        connection = DriverManager.getConnection(jdbcUrl);
        createTables();
    }

    @SneakyThrows
    private void mySql() {
        Main.getInstance().getLogger().log(Level.INFO, "Database loaded successfully! -> mysql");
        Class.forName("com.mysql.cj.jdbc.Driver");

        String host = Main.getInstance().getConfig().getString("Database.host");
        int port = Main.getInstance().getConfig().getInt("Database.port");
        String database = Main.getInstance().getConfig().getString("Database.database");
        String username = Main.getInstance().getConfig().getString("Database.username");
        String password = Main.getInstance().getConfig().getString("Database.password");
        boolean useSSL = Main.getInstance().getConfig().getBoolean("Database.useSSL", false);

        String jdbcUrl = String.format(
                "jdbc:mysql://%s:%d/%s?useSSL=%b&useUnicode=true&characterEncoding=utf8&autoReconnect=true",
                host, port, database, useSSL
        );

        this.connection = DriverManager.getConnection(jdbcUrl, username, password);
        createTables();
    }

    private void invalid() {
        Main.getInstance().getLogger().log(Level.SEVERE, "Database loading failed! -> invalid type");
        Bukkit.getPluginManager().disablePlugin(Main.getInstance());
    }

    @SneakyThrows
    public void createTables() {
        Statement statement = getConnection().createStatement();

        statement.execute(
                "CREATE TABLE IF NOT EXISTS "+Main.getInstance().getConfig().getString("Database.table-name", "integrity_economy")+"(" +
                        "name varchar(36) DEFAULT NULL, " +
                        "balance DOUBLE NOT NULL DEFAULT 0)"
        );
        statement.close();
    }

    //FUNCTIONS
    @SneakyThrows
    public boolean existsPlayer(OfflinePlayer player) {
        synchronized (sync()) {
            PreparedStatement statement = getConnection().prepareStatement(
                    "SELECT * FROM "+Main.getInstance().getConfig().getString("Database.table-name", "integrity_economy")+" WHERE name = ?"
            );

            statement.setString(1, player.getName());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getString("name") != null;
            }

            resultSet.close();
            statement.close();
        }
        return false;
    }

    @SneakyThrows
    public void createAccount(OfflinePlayer player, double amount) {
        synchronized (sync()) {
            if (existsPlayer(player)) return;

            String table = Main.getInstance().getConfig().getString("Database.table-name", "integrity_economy");
            String sql = "INSERT INTO " + table + " (name, balance) VALUES (?, ?)";

            PreparedStatement statement = getConnection().prepareStatement(sql);

            statement.setString(1, player.getName());
            statement.setDouble(2, amount);
            statement.executeUpdate();

            statement.close();
        }
    }

    @SneakyThrows
    public double getBalance(OfflinePlayer player) {
        synchronized (sync()) {
            String table = Main.getInstance().getConfig().getString("Database.table-name", "integrity_economy");
            String sql = "SELECT balance FROM " + table + " WHERE name = ?";
            double balance = 0.0;

            PreparedStatement statement = getConnection().prepareStatement(sql);

            statement.setString(1, player.getName());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                balance = rs.getDouble("balance");
            }

            statement.close();
            return balance;
        }
    }

    @SneakyThrows
    public void updateBalance(OfflinePlayer player, double amount) {
        synchronized (sync()) {
            String table = Main.getInstance().getConfig().getString("Database.table-name", "integrity_economy");
            String sql = "UPDATE " + table + " SET balance = ? WHERE name = ?";

            PreparedStatement statement = getConnection().prepareStatement(sql);

            statement.setDouble(1, amount);
            statement.setString(2, player.getName());

            statement.executeUpdate();
            statement.close();
        }
    }

    @SneakyThrows
    public List<UserProfile> getAllAccounts() {
        synchronized (sync()) {
            String table = Main.getInstance().getConfig().getString("Database.table-name", "integrity_economy");
            String sql = "SELECT name, balance FROM "+table;

            PreparedStatement statement = getConnection().prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            ArrayList<UserProfile> userProfiles = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString("name");
                double balance = rs.getDouble("balance");

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
                if (Main.getCacheManager().getPlayerCache().containsKey(offlinePlayer))
                    balance = Main.getCacheManager().getBalance(offlinePlayer);

                userProfiles.add(UserProfile.builder().playerName(name).balance(balance).build());
            }
            statement.close();
            return userProfiles;
        }
    }

    private Object sync() {
        boolean useLock = typeData.equalsIgnoreCase("local");
        return useLock ? sqliteSupport : new Object();
    }
}
