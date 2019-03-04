package org.mulima.main;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.*;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.api.file.TempDir;
import org.mulima.api.library.LibraryManager;
import org.mulima.api.library.ReferenceLibrary;
import org.mulima.internal.service.DefaultMulimaService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Mulima {
  private static final Logger logger = LogManager.getLogger(Mulima.class);

  public static void main(String[] args) {
    try {
      var cli = new DefaultParser();
      var options = new Options();
      options.addOption("h", "help", false, "Prints this help message");
      options.addOption("l", "list", false, "Lists the libraries currently configured");
      options.addOption("s", "stats", false, "Gives stats on the nunmber of albums in each library");
      options.addOption("u", "update", false, "Updates albums in your destination libraries (implies --process)");
      options.addOption("f", "force", false, "Forces the update on all albums, including up to date (only used with --update)");
      options.addOption("v", "verify", false, "Verifies all album.xml files");
      options.addOption(null, "status", false, "Lists the status of each album");
      options.addOption(null, "fix-meta", false, "Fixes common metadata problems");
      options.addOption(null, "create-stubs", false, "Create stub album.xml files with cue sheet info for albums without metadata");

      var cmd = cli.parse(options, args);

      if (cmd.hasOption("help")) {
        var help = new HelpFormatter();
        help.printHelp("mulima", options);
        return;
      }

      ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
      var service = context.getBean(DefaultMulimaService.class);
      service.setTempDir(new TempDir().newChild("mulima"));

      var manager = context.getBean(LibraryManager.class);

      var refLibs = service.getLibraryService().getRefLibs().stream()
          .filter(lib -> cmd.getArgList().isEmpty() || cmd.getArgList().contains(lib.getName()))
          .collect(Collectors.toSet());
      var destLibs = service.getLibraryService().getDestLibs().stream()
          .filter(lib -> cmd.getArgList().isEmpty() || cmd.getArgList().contains(lib.getName()))
          .collect(Collectors.toSet());

      if (cmd.hasOption("list")) {
        System.out.println("Reference Libraries:");
        System.out.println("--------------------");
        refLibs.forEach(lib -> {
          System.out.println(String.format("%s (%s) - %s", lib.getName(), lib.getFormat(), lib.getRootDir()));
        });
        System.out.println("");
        System.out.println("Destination Libraries:");
        System.out.println("--------------------");
        destLibs.forEach(lib -> {
          System.out.println(String.format("%s (%s) - %s", lib.getName(), lib.getFormat(), lib.getRootDir()));
        });
      } else if (cmd.hasOption("stats")) {
        Stream.concat(refLibs.stream(), destLibs.stream()).forEach(lib -> {
          System.out.println(String.format("%s (%s) - %s", lib.getName(), lib.getFormat(), lib.getRootDir()));
          var outdated = lib.getAll().stream()
              .filter(album -> album.getId() != null && !service.getLibraryService().isUpToDate(album, true))
              .collect(Collectors.toList());
          if (lib instanceof ReferenceLibrary) {
            System.out.println("  New:         " + ((ReferenceLibrary) lib).getNew().size());
          }
          System.out.println("  Out of Date: " + outdated.size());
          System.out.println("  Total:       " + lib.getAll().size());
        });
      } else if (cmd.hasOption("status")) {
        Stream.concat(refLibs.stream(), destLibs.stream()).forEach(lib -> {
          System.out.println(String.format("%s (%s) - %s", lib.getName(), lib.getFormat(), lib.getRootDir()));
          lib.getAll().stream().forEach(album -> {
            var upToDate = service.getLibraryService().isUpToDate(album, true);
            if (album.getId() == null) {
              System.out.println(String.format("  %b - %s (New)", upToDate, album.getDir()));
            } else {
              System.out.println(String.format("  %b - %s", upToDate, album.getName()));
            }
          });
        });
      } else if (cmd.hasOption("update")) {
        manager.processNew();
        manager.update(destLibs);
      } else if (cmd.hasOption("verify")) {
        refLibs.stream()
            .flatMap(lib -> lib.getAll().stream())
            .filter(album -> {
              if (album.getId() == null) {
                return false;
              } else {
                if (album.getAlbum() == null) {
                  return true;
                } else {
                  try {
                    album.getName();
                    return false;
                  } catch (Exception e) {
                    // TODO log
                    return true;
                  }
                }
              }
            }).forEach(album -> {
              System.out.println("Invalid album.xml " + album.getDir());
            });
      } else if (cmd.hasOption("fix-meta")) {
        // FIXME implement


        // refLibs*.all*.each { LibraryAlbum refAlbum ->
        // if (refAlbum.album == null) {
        // return
        // }
        // Album album = refAlbum.album
        // boolean anyFixes = false
        //
        // //look for data tracks (i.e. tracks with no start point)
        // album.discs*.tracks*.retainAll { Track track ->
        // if (track.startPoint == null) {
        // println "Removing data track from ${refAlbum.name}"
        // anyFixes = true
        // return false
        // } else {
        // return true
        // }
        // }
        //
        // //look for any duplicate tracks (i.e. tracks whose names were split across two)
        // album.discs.each { Disc disc ->
        // def cleanTracks = []
        // disc.tracks.each { Track track ->
        // Track otherTrack = cleanTracks.find { it.num == track.num && it.startPoint == track.startPoint }
        // if (otherTrack == null) {
        // cleanTracks << track
        // } else {
        // println "Consolidating tracks on ${refAlbum.name}"
        // assert otherTrack.getAll(GenericTag.TITLE).size() == 1
        // assert track.getAll(GenericTag.TITLE).size() == 1
        // def title = otherTrack.getFirst(GenericTag.TITLE) + track.getFirst(GenericTag.TITLE)
        // otherTrack.remove(GenericTag.TITLE)
        // otherTrack.add(GenericTag.TITLE, title)
        // anyFixes = true
        // }
        // }
        // disc.tracks.removeAll { true }
        // disc.tracks.addAll(cleanTracks)
        // }
        //
        // if (anyFixes) {
        // service.fileService.getComposer(Album).compose(new File(refAlbum.dir, Album.FILE_NAME), album)
        // }
        // }
      } else if (cmd.hasOption("create-stubs")) {
        // FIXME implement

        // refLibs.each { ReferenceLibrary refLib ->
        // refLib.new.each { refAlbum ->
        // Album album = new DefaultAlbum()
        // refAlbum.cueSheets.each { CueSheet cue ->
        // Disc disc = new DefaultDisc(album)
        // disc.add(GenericTag.DISC_NUMBER, Integer.toString(cue.getNum()))
        // cue.getMap().each { GenericTag tag, List values ->
        // if (tag == GenericTag.FILE) {
        // return
        // }
        // disc.addAll(tag, values)
        // }
        // cue.cuePoints.each { CuePoint point ->
        // Track track = new DefaultTrack(disc)
        // track.add(GenericTag.TRACK_NUMBER, Integer.toString(point.getTrack()))
        // track.setStartPoint(point)
        // disc.tracks.add(track)
        // }
        // album.discs.add(disc)
        // }
        //
        // album.tidy()
        // service.getFileService().getComposer(Album.class).compose(new File(refAlbum.getDir(),
        // "album.xml"), album)
        // }
        // }
        // }
      }
    } catch (Throwable e) {
      logger.error("Mulima failed.", e);
      System.exit(1);
    }
  }
}
