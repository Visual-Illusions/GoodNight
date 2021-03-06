/*
 * This file is part of GoodNight.
 *
 * Copyright © 2012-2014 Visual Illusions Entertainment
 *
 * GoodNight is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License v3 as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License v3 for more details.
 *
 * You should have received a copy of the GNU General Public License v3 along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.goodnight.canary;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.World;
import net.canarymod.config.Configuration;
import net.visualillusionsent.goodnight.GoodNight;
import net.visualillusionsent.goodnight.VoteCalculator;
import net.visualillusionsent.minecraft.plugin.canary.VisualIllusionsCanaryPlugin;
import net.visualillusionsent.utils.PropertiesFile;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class CanaryGoodNight extends VisualIllusionsCanaryPlugin implements GoodNight {

    private final HashMap<String, VoteCalculator> calculators = new HashMap<String, VoteCalculator>();
    private final HashMap<String, PropertiesFile> world_props = new HashMap<String, PropertiesFile>();
    private final static String GLO = "GLOBAL";

    @Override
    public boolean enable() {
        super.enable();
        try {
            new CanaryGoodNightListener(this);
            getWorldProps(GLO);
            for (String world : Canary.getServer().getWorldManager().getExistingWorlds()) {
                if (world.endsWith("NETHER") || world.endsWith("END")) { // Ignore END and NETHER
                    continue;
                }
                getWorldProps(world.replace("_NORMAL", "")); // Preset properties
            }
        }
        catch (Exception ex) {
            getLogman().error("Failed to enable", ex);
            return false;
        }
        return true;
    }

    @Override
    public final int getWorldUserCount(String world) {
        return Canary.getServer().getWorldManager().getWorld(world, false).getPlayerList().size();
    }

    @Override
    public final void goodMorning(String world) {
        World the_world = Canary.getServer().getWorldManager().getWorld(world, false);
        if (the_world != null) { //Just incase somehow it is null
            the_world.setTime(0);
            the_world.broadcastMessage(getMorningMessage(world));
        }
    }

    @Override
    public final void voteCasted(String user, String world) {
        World the_world = Canary.getServer().getWorldManager().getWorld(world, false);
        if (the_world != null) { //Just incase somehow it is null
            String goodMorning = convertMessage(world, user, getMorningMessage(world));
            for (Player player : the_world.getPlayerList()) {
                player.message(goodMorning);
            }
        }
    }

    @Override
    public final boolean userVote(String user, String world) {
        if (!calculators.containsKey(world)) {
            calculators.put(world, new VoteCalculator(this, world, percentRequired(world), useAdjustedMajority(world)));
        }
        return calculators.get(world).addVote(user);
    }

    @Override
    public final void removeUser(String user, String world) {
        if (calculators.containsKey(world)) {
            calculators.get(world).removeVote(user);
        }
    }

    @Override
    public final void morningClear(String world) {
        if (calculators.containsKey(world)) {
            calculators.get(world).morningClear();
        }
    }

    @Override
    public final String isDayTime(String world, String user) {
        if (getWorldProps(world).getBoolean("use.global")) {
            return convertMessage(world, user, getWorldProps(GLO).getString("day.time"));
        }
        return convertMessage(world, user, getWorldProps(world).getString("day.time"));
    }

    @Override
    public final String alreadyVoted(String world, String user) {
        if (getWorldProps(world).getBoolean("use.global")) {
            return convertMessage(world, user, getWorldProps(GLO).getString("already.voted"));
        }
        return convertMessage(world, user, getWorldProps(world).getString("already.voted"));
    }

    private PropertiesFile getWorldProps(String world) {
        if (!world_props.containsKey(world)) {
            world_props.put(world, Configuration.getPluginConfig(this, world));
            return testWorldProps(world, world_props.get(world));
        }
        return world_props.get(world);
    }

    private byte percentRequired(String world) {
        if (getWorldProps(world).getBoolean("use.global")) {
            return getWorldProps(GLO).getByte("percent.required");
        }
        return getWorldProps(world).getByte("percent.required");
    }

    private boolean useAdjustedMajority(String world) {
        if (getWorldProps(world).getBoolean("use.global")) {
            return getWorldProps(GLO).getBoolean("adjust.majority");
        }
        return getWorldProps(world).getBoolean("adjust.majority");
    }

    private String getMorningMessage(String world) {
        return getWorldProps(world).getString("good.morning.msg");
    }

    private String convertMessage(String world, String user, String message) {
        String msg = message;
        if (world != null) {
            msg = msg.replaceAll(Pattern.quote("${world}"), world.replace("_NORMAL", ""));
        }
        if (user != null) {
            msg = msg.replaceAll(Pattern.quote("${player}"), user);
        }
        msg = msg.replaceAll("&([0-9A-FK-Oa-fk-o])", "\u00A7$1");
        return msg;
    }

    public final boolean worldEnabled(String world) {
        return getWorldProps(world).getBoolean("world.enabled");
    }

    private PropertiesFile testWorldProps(String world, PropertiesFile props_file) {
        boolean hasChange = false;
        if (!world.equals(GLO) && !props_file.containsKey("use.global")) {
            props_file.setBoolean("use.global", true, "Sets whether to use the global configuration for settings. DOES NOT effect world.enabled setting. Default: true");
            hasChange = true;
        }
        if (!world.equals(GLO) && !props_file.containsKey("world.enabled")) {
            props_file.setBoolean("world.enabled", true, "Sets if voting is allowed in the world");
            hasChange = true;
        }
        if (!props_file.containsKey("adjust.majority")) {
            props_file.setBoolean("adjust.majority", true, "Adjust the Percent required for instances where majority does not equal required percent. Default: true");
            hasChange = true;
        }
        if (!props_file.containsKey("percent.required")) {
            props_file.setByte("percent.required", (byte) 66, "Percent of User votes for time change. No decimals. Default: 66 (ie: 66%)");
            hasChange = true;
        }
        else if (props_file.getByte("percent.required") <= 0 || props_file.getByte("percent.required") >= 101) {
            getLogman().warn("Percent Required out of range for World: '" + world + "' (Expected: '1 thru 100' Got: " + props_file.getByte("percent.required") + ") Resetting to default (66)");
            props_file.setByte("percent.required", (byte) 66);
            hasChange = true;
        }
        if (!props_file.containsKey("good.morning.msg")) {
            props_file.setString("good.morning.msg", "&bGood Morning: &6${world}", "The Good Morning message to use.");
            hasChange = true;
        }
        if (!props_file.containsKey("day.time")) {
            props_file.setString("day.time", "&6${player}&c, It is already day in &b${world}", "The message to send for a world already in the Day cycle.");
            hasChange = true;
        }
        if (!props_file.containsKey("already.voted")) {
            props_file.setString("already.voted", "&6${player}&c, You have already voted.", "The message to send for a Player that has already voted.");
            hasChange = true;
        }
        if (hasChange) {
            props_file.save();
        }
        return props_file;
    }

    final void reloadConfigs() {
        synchronized (world_props) {
            for (Map.Entry<String, PropertiesFile> entry : world_props.entrySet()) {
                entry.getValue().reload();
                testWorldProps(entry.getKey(), entry.getValue());
            }
        }
    }
}
