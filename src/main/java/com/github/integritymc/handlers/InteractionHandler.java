/*
 * IntegrityEconomy - Copyright (C) 2026 IntegrityMC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.github.integritymc.handlers;

import com.github.integritymc.CommonUtils;
import com.github.integritymc.IntegrityGUI;
import com.github.integritymc.Main;
import com.github.integritymc.commands.PayCommand;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import de.rapha149.signgui.exception.SignGUIVersionException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.Collections;
import java.util.List;

public class InteractionHandler implements Listener {
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!Main.getInstance().getConfig().getBoolean("Functions.FastPay.enabled")) return;
        if (!(event.getRightClicked() instanceof Player target)) return;

        Player sender = event.getPlayer();
        if (sender.getInventory().getItemInMainHand().getType() != Material.AIR) return;

        IntegrityGUI gui = new IntegrityGUI(Main.getInstance(), sender, CommonUtils.legacyBuilder().serialize(
                MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Functions.FastPay.title", "{prefix} Fast Pay → {player}")
                        .replace("{prefix}", Main.getInstance().getConfig().getString("Messages.prefix", "<color:#8291ff><b>IE</b></color>"))
                        .replace("{player}", target.getName())
                )
        ), 5, false);
        gui.fillBorder(CommonUtils.itemBuilder(Material.GRAY_STAINED_GLASS_PANE, Component.empty(), null));

        List<Integer> slotsPay = List.of(12,13,14,21,22,23,30,31,32);
        for (int slot : slotsPay) {
            gui.setItem(slot, CommonUtils.itemBuilder(
                    Material.LIME_STAINED_GLASS_PANE,
                    MiniMessage.miniMessage().deserialize(Main.getInstance().getConfig().getString("Messages.fastpay-pay-title", "<gradient:#c2ccff:#d6e6ff><b>SEND MONEY</b></gradient>")),
                    CommonUtils.stringToMiniMessage(Main.getInstance().getConfig().getStringList("Messages.fastpay-pay-lore"))
            ), clickType -> {
                gui.close();
                diplaySign(sender, target);
            });
        }

        gui.open();
    }

    private void diplaySign(Player sender, Player target) {
        try {
            List<String> lines = Main.getInstance().getConfig().getStringList("Messages.fastpay-sign");
            SignGUI sign = SignGUI.builder()
                    .setLines(
                            CommonUtils.processString(lines.get(0)),
                            CommonUtils.processString(lines.get(1)),
                            CommonUtils.processString(lines.get(2)),
                            CommonUtils.processString(lines.get(3))
                    )
                    .setType(Material.OAK_SIGN)
                    .setHandler((p, result) -> {
                        int inputLine = Main.getInstance().getConfig().getInt("Functions.FastPay.input-line", 0);
                        String input = result.getLineWithoutColor(inputLine);
                        double inputDouble = inputDouble(input);

                        if (input.isEmpty() || inputDouble == -1 || !Main.getEconomy().has(sender, inputDouble))
                            return List.of(SignGUIAction.displayNewLines(
                                    CommonUtils.processString(lines.get(0)),
                                    CommonUtils.processString(lines.get(1)),
                                    CommonUtils.processString(lines.get(2)),
                                    CommonUtils.processString(lines.get(3))
                            ));

                        if (inputDouble == 0)
                            return Collections.emptyList();

                        PayCommand.pay(sender, target, inputDouble);
                        return Collections.emptyList();
                    })
                    .build();
            sign.open(sender);
        } catch (SignGUIVersionException e) {
            throw new RuntimeException(e);
        }
    }

    private double inputDouble(String input) {
        try {
            double inputInt = input.isEmpty() ? -1 : Integer.parseInt(input);
            return (inputInt < 0) ? -1 : inputInt;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
