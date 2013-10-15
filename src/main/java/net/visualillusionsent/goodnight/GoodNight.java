/*
 * This file is part of GoodNight.
 *
 * Copyright © 2012-2013 Visual Illusions Entertainment
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
package net.visualillusionsent.goodnight;

public interface GoodNight {

    int getWorldUserCount(String world);

    void goodMorning(String world);

    void voteCasted(String user, String world);

    boolean userVote(String user, String world);

    void removeUser(String user, String world);

    void morningClear(String world);

    String isDayTime(String world, String user);

    String alreadyVoted(String world, String user);
}
