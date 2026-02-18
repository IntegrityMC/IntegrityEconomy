/*
 * IntegrityEconomy - Copyright (C) 2026 IntegrityMC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.github.integritymc;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;

import java.util.List;

public class VaultDialer extends AbstractEconomy {
	@Override
	public boolean isEnabled() {
        return true;
	}

	@Override
	public String getName() {
        return "IntegrityEconomy";
	}

	@Override
	public boolean hasBankSupport() {
        return false;
	}

	@Override
	public int fractionalDigits() {
        return 0;
	}

	@Override
	public String format(double amount) {
        return Main.getInstance().getConfig().getString("")
            .replace("{rounded-money}", String.valueOf(Math.round(amount))
            .replace("{raw-money}", String.valueOf(amount))
            .replace("{currency}", dynamicCurrency(amount)));
	}

    private String dynamicCurrency(double amount) {
        return (amount == 1) ? currencyNameSingular() : currencyNamePlural();
    }

	@Override
	public String currencyNamePlural() {
        return Main.getInstance().getConfig().getString("Economy.currencyPlural");
	}

	@Override
	public String currencyNameSingular() {
		return Main.getInstance().getConfig().getString("Economy.currencySingular");
	}

	@Override
	public boolean hasAccount(String playerName) {
		return Main.getCacheManager().existPlayer(Bukkit.getOfflinePlayer(playerName));
	}

	@Override
	public boolean hasAccount(String playerName, String worldName) {
        return Main.getCacheManager().existPlayer(Bukkit.getOfflinePlayer(playerName));
	}

	@Override
	public double getBalance(String playerName) {
		return Main.getCacheManager().getBalance(Bukkit.getOfflinePlayer(playerName));
	}

	@Override
	public double getBalance(String playerName, String world) {
        return Main.getCacheManager().getBalance(Bukkit.getOfflinePlayer(playerName));
	}

	@Override
	public boolean has(String playerName, double amount) {
		return (amount < getBalance(Bukkit.getOfflinePlayer(playerName)));
	}

	@Override
	public boolean has(String playerName, String worldName, double amount) {
		return (amount < getBalance(Bukkit.getOfflinePlayer(playerName)));
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		if (!has(playerName, amount)) {
			return new EconomyResponse(amount, getBalance(playerName), ResponseType.FAILURE, "Player don't have enough money");
		}

        Main.getCacheManager().updateBalance(Bukkit.getOfflinePlayer(playerName), getBalance(playerName)-amount);
		return new EconomyResponse(amount, getBalance(playerName), ResponseType.SUCCESS, "Removed moneys from the player");
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
		if (!has(playerName, amount)) {
			return new EconomyResponse(amount, getBalance(playerName), ResponseType.FAILURE, "Player don't have enough money");
		}

        Main.getCacheManager().updateBalance(Bukkit.getOfflinePlayer(playerName), getBalance(playerName)-amount);
		return new EconomyResponse(amount, getBalance(playerName), ResponseType.SUCCESS, "Removed moneys from the player");
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
        Main.getCacheManager().updateBalance(Bukkit.getOfflinePlayer(playerName), getBalance(playerName)+amount);
		return new EconomyResponse(amount, getBalance(playerName), ResponseType.SUCCESS, "Added moneys from the player");
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        Main.getCacheManager().updateBalance(Bukkit.getOfflinePlayer(playerName), getBalance(playerName)+amount);
		return new EconomyResponse(amount, getBalance(playerName), ResponseType.SUCCESS, "Added moneys from the player");
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Not implemented");
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Not implemented");
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Not implemented");
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Not implemented");
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Not implemented");
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Not implemented");
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Not implemented");
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Not implemented");
	}

	@Override
	public List<String> getBanks() {
		return null;
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(String playerName, String worldName) {
		return false;
	}

}
