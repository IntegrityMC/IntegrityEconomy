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

package com.github.integritymc;

public class CommonUtils {
	public static String currency(double amount) {
		return amount>1 ? Main.getInstance().getConfig().getString("Economy.currencyPlural") : Main.getInstance().getConfig().getString("Economy.currencySingolar");
	}
	
	public static String formatAmount(double balance) {
        int decimalPlaces = Main.getInstance().getConfig().getInt("Economy.decimal-places", 2);
        String formatString = String.format("%%.%df", decimalPlaces);
		return ((balance % 1 == 0) || (decimalPlaces <= 0)) ? String.valueOf((int) balance) : String.format(formatString, balance);
	}
}
