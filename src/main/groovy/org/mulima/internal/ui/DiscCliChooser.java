/*
 * Copyright 2010-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class DiscCliChooser implements Chooser<Disc> {
  private final CueSheet cue;

  /**
   * Constructs the chooser to be based on the specified cue sheet.
   *
   * @param cue cue sheet to base the choice on
   */
  public DiscCliChooser(CueSheet cue) {
    this.cue = cue;
  }

  /**
   * Will automatically make the choice if the closest disc is within 10. The user will be asked via
   * stdout if the closest is farther than that.
   *
   * @param choices the disc choices
   * @return the chosen disc
   * @see MetadataUtil#discDistance
   */
  @Override
  public Disc choose(List<Disc> choices) {
    System.out.println("***** User Input Requested *****");
    System.out.println("Cue Disc ID: " + cue.getFlat(GenericTag.CDDB_ID));
    System.out.println("Cue Artist: " + cue.getFlat(GenericTag.ARTIST));
    System.out.println("Cue Album: " + cue.getFlat(GenericTag.ALBUM));
    return askUser(choices);
  }

  /**
   * Asks user for his/her choice of best match.
   *
   * @param candidates list of candidate matches
   * @return the user's choice
   */
  private Disc askUser(List<Disc> candidates) {
    for (int i = 0; i < candidates.size(); i++) {
      System.out.println(
          "#" + (i + 1) + ":\tArtist: " + candidates.get(i).getFlat(GenericTag.ARTIST));
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
