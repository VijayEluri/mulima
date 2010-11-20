/*  
 *  Copyright (C) 2010  Andrew Oberstar.  All rights reserved.
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

package com.andrewoberstar.library.ui.cli;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.andrewoberstar.library.meta.AlbumUtil;
import com.andrewoberstar.library.meta.CueSheet;
import com.andrewoberstar.library.meta.Disc;
import com.andrewoberstar.library.meta.GenericTag;
import com.andrewoberstar.library.ui.UICallback;

public class ChooseDiscCallback implements UICallback<Disc> {

	@SuppressWarnings("unchecked")
	@Override
	public Disc call(Map<String, Object> parms) {
		CueSheet cue = (CueSheet) parms.get("cue");
		List<Disc> candidates = (List<Disc>) parms.get("candidates");
		
		Disc closest = AlbumUtil.closest(cue, candidates);
		int dist = AlbumUtil.discDistance(cue, closest);
		
		if (candidates.size() > 0 && dist > 10) {
			System.out.println("***** User Input Requested *****");
			System.out.println("Cue Disc ID: " + cue.getTags().getFlat(GenericTag.CDDB_ID));
			System.out.println("Cue Artist: " + cue.getTags().getFlat(GenericTag.ARTIST));
			System.out.println("Cue Album: " + cue.getTags().getFlat(GenericTag.ALBUM));
			return askUser(candidates);
		} else {
			return closest;
		}
	}
	
	private Disc askUser(List<Disc> candidates) {
		for (int i = 0; i < candidates.size(); i++) {
			System.out.println("#" + (i + 1) + ":\tArtist: " + candidates.get(i).getTags().getFlat(GenericTag.ARTIST));
			System.out.println("\tAlbum: " + candidates.get(i).getTags().getFlat(GenericTag.ALBUM));
		}
		
		System.out.println("Enter number of desired choice or -1 for none of the above:");
		Scanner console = new Scanner(System.in);
		int num = console.nextInt();
		if (num == -1) {
			return null;
		} else if (num > 0 && num <= candidates.size()){
			return candidates.get(num - 1);
		} else {
			System.out.println("Invalid choice.");
			return askUser(candidates);
		}
	}

}
