/*
 * This file is part of GoodNight.
 *
 * Copyright © 2012 Visual Illusions Entertainment
 *
 * GoodNight is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * GoodNight is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with GoodNight.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.goodnight.canary;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.DimensionType;
import net.canarymod.chat.Colors;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.entity.DimensionSwitchHook;
import net.canarymod.hook.player.DisconnectionHook;
import net.canarymod.hook.world.TimeChangeHook;
import net.canarymod.plugin.PluginListener;
import net.visualillusionsent.utils.VersionChecker;

public final class CanaryGoodNightListener extends VisualIllusionsCanaryPluginInformationCommand implements PluginListener {

    private final CanaryGoodNight goodnight;

    CanaryGoodNightListener(CanaryGoodNight goodnight) throws CommandDependencyException {
        super(goodnight);
        this.goodnight = goodnight;
        Canary.hooks().registerListener(this, goodnight);
        Canary.commands().registerCommands(this, goodnight, false);
    }

    @HookHandler
    public final void dimensionSwitch(DimensionSwitchHook hook) {
        if (hook.getEntity() instanceof Player) {
            goodnight.removeUser(hook.getEntity().getName(), hook.getLocationFrom().getWorld().getFqName());
        }
    }

    @HookHandler
    public final void disconnect(DisconnectionHook hook) {
        goodnight.removeUser(hook.getPlayer().getName(), hook.getPlayer().getWorld().getFqName());
    }

    @HookHandler
    public final void worldTimeChange(TimeChangeHook hook) {
        if (!isNightFall(hook.getWorld().getRelativeTime())) {
            goodnight.morningClear(hook.getWorld().getFqName());
        }
    }

    @Command(aliases = { "goodnight" },
        description = "Casts vote",
        permissions = { "goodnight.vote" },
        toolTip = "/goodnight")
    public final void goodNight(MessageReceiver msgrec, String[] args) {
        if (msgrec instanceof Player) {
            Player player = (Player) msgrec;
            if (player.getWorld().getType() != DimensionType.fromName("NETHER") && player.getWorld().getType() != DimensionType.fromName("END")) {
                if (goodnight.worldEnabled(player.getWorld().getName())) {
                    if (isNightFall(player.getWorld().getRelativeTime())) {
                        if (!goodnight.userVote(player.getName(), player.getWorld().getName())) {
                            msgrec.message(goodnight.alreadyVoted(player.getWorld().getName(), player.getName()));
                        }
                    }
                    else {
                        msgrec.message(goodnight.isDayTime(player.getWorld().getName(), player.getName()));
                    }
                }
                else {
                    msgrec.message("world.disabled");
                }
            }
            else {
                msgrec.notice("This world does not have time.");
            }
        }
    }

    @Command(aliases = { "info" },
        description = "GoodNight Information Command",
        permissions = { "goodnight.info" },
        toolTip = "/goodnight info",
        parent = "goodnight")
    public final void goodNightInfo(MessageReceiver msgrec, String[] args) {
        for (String msg : about) {
            if (msg.equals("$VERSION_CHECK$")) {
                VersionChecker vc = plugin.getVersionChecker();
                Boolean islatest = vc.isLatest();
                if (islatest == null) {
                    msgrec.message(center(Colors.GRAY + "VersionCheckerError: " + vc.getErrorMessage()));
                }
                else if (!islatest) {
                    msgrec.message(center(Colors.GRAY + vc.getUpdateAvailibleMessage()));
                }
                else {
                    msgrec.message(center(Colors.LIGHT_GREEN + "Latest Version Installed"));
                }
            }
            else {
                msgrec.message(msg);
            }
        }
    }

    private final boolean isNightFall(long time) {
        return time >= 11500 && time <= 22009;
    }
}