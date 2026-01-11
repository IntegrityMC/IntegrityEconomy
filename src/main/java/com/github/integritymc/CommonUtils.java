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
