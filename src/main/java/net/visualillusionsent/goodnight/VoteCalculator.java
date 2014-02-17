/*
 * This file is part of GoodNight.
 *
 * Copyright © 2012-2014 Visual Illusions Entertainment
 *
 * GoodNight is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.goodnight;

import java.util.ArrayList;

public final class VoteCalculator {

    private final GoodNight goodnight;
    private final String world;
    private final byte percentage_required;
    private final boolean majorityAdjust;
    private final ArrayList<String> votes = new ArrayList<String>();

    public VoteCalculator(GoodNight goodnight, String world, byte percentage_required, boolean majorityAdjust) {
        this.goodnight = goodnight;
        this.world = world;
        this.percentage_required = percentage_required;
        this.majorityAdjust = majorityAdjust;
    }

    public final boolean hasVoted(String user) {
        return votes.contains(user);
    }

    public final boolean addVote(String user) {
        if (!votes.contains(user)) {
            votes.add(user);
            goodnight.voteCasted(user, world);
            recalculateVotes();
            return true;
        }
        return false;
    }

    public final void removeVote(String user) {
        if (votes.contains(user)) {
            votes.remove(user);
            recalculateVotes();
        }
    }

    public final void recalculateVotes() {
        byte votePercent = (byte) (((float) votes.size() / (float) goodnight.getWorldUserCount(world)) * 100);
        if (votePercent >= percentage_required || adjustedMajority()) {
            goodnight.goodMorning(world);
            votes.clear();
        }
    }

    public final void morningClear() {
        if (!votes.isEmpty()) {
            votes.clear();
        }
    }

    public final int getVoteCount() {
        return votes.size();
    }

    public final int getVotePercent() {
        return (byte) (((float) votes.size() / (float) goodnight.getWorldUserCount(world)) * 100);
    }

    private boolean adjustedMajority() { //for those times when majority doesnt quite reach the percent required
        if (majorityAdjust) {
            int usercount = goodnight.getWorldUserCount(world);
            if (((byte) ((usercount - 1.0F) / ((float) usercount) * 100)) < percentage_required) {
                return true;
            }
        }
        return false;
    }
}
