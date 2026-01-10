package com.github.integritymc;

public class CommonUtils {
	public static String currency(double amount) {
		return amount>1 ? Main.getInstance().getConfig().getString("Economy.currencyPlural") : Main.getInstance().getConfig().getString("Economy.currencySingolar");
	}
	
	public static String formatAmount(double balance) {
		String format;
		if (balance % 1 == 0)
			format = String.valueOf((int) balance);
		else
			format = String.format("%."+Main.getInstance().getConfig().getInt("Economy.decimal-places", 2)+"f", balance);
			
		return format;
	}
}
