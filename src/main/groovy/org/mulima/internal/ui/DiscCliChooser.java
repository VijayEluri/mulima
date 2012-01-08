/*  
 *  Copyright (C) 2011  Andrew Oberstar.  All rights reserved.
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mulima.internal.ui;

import java.util.List;
import java.util.Scanner;

import org.mulima.api.meta.CueSheet;
import org.mulima.api.meta.Disc;
import org.mulima.api.meta.GenericTag;
import org.mulima.util.MetadataUtil;


/**
 * Chooser for picking discs from the command line.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class DiscCliChooser implements Chooser<Disc> {
	private final CueSheet cue;
	
	/**
	 * Constructs the chooser to be based on
	 * the specified cue sheet.
	 * @param cue cue sheet to base the choice on
	 */
	public DiscCliChooser(CueSheet cue) {
		this.cue = cue;
	}
	
	/**
	 * Will automatically make the choice if the closest disc
	 * is within 10.  The user will be asked via stdout if the
	 * closest is farther than that.
	 * @param choices the disc choices
	 * @return the chosen disc
	 * @see MetadataUtil#discDistance
	 */
	@Override
	public Disc choose(List<Disc> choices) {
		int min = Integer.MAX_VALUE;
		Disc closest = null;
		for (Disc cand : choices) {
			int dist = MetadataUtil.discDistance(cue, cand);
			if (dist < min) {
				min = dist;
				closest = cand;
			}
		}
		
		if (!choices.isEmpty() && min > 10) {
			System.out.println("***** User Input Requested *****");
			System.out.println("Cue Disc ID: " + cue.getFlat(GenericTag.CDDB_ID));
			System.out.println("Cue Artist: " + cue.getFlat(GenericTag.ARTIST));
			System.out.println("Cue Album: " + cue.getFlat(GenericTag.ALBUM));
			return askUser(choices);
		} else {
			return closest;
		}
	}
	
	/**
	 * Asks user for his/her choice of best match.
	 * @param candidates list of candidate matches
	 * @return the user's choice
	 */
	private Disc askUser(List<Disc> candidates) {
		for (int i = 0; i < candidates.size(); i++) {
			System.out.println("#" + (i + 1) + ":\tArtist: "
				+ candidates.get(i).getFlat(GenericTag.ARTIST));
			System.out.println("\tAlbum: " + candidates.get(i).getFlat(GenericTag.ALBUM));
		}
		
		System.out.println("Enter number of desired choice or -1 for none of the above:");
		Scanner console = new Scanner(System.in);
		int num = console.nextInt();
		if (num == -1) {
			return null;
		} else if (num > 0 && num <= candidates.size()) {
			return candidates.get(num - 1);
		} else {
			System.out.println("Invalid choice.");
			return askUser(candidates);
		}
	}
}
