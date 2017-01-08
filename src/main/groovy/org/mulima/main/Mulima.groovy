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
package org.mulima.main

import java.io.File;

import org.mulima.api.file.TempDir
import org.mulima.api.library.Library
import org.mulima.api.library.LibraryAlbum
import org.mulima.api.library.LibraryManager
import org.mulima.api.library.ReferenceLibrary
import org.mulima.api.meta.Album
import org.mulima.api.meta.CuePoint
import org.mulima.api.meta.CueSheet
import org.mulima.api.meta.Disc
import org.mulima.api.meta.GenericTag
import org.mulima.api.meta.Track
import org.mulima.api.service.MulimaService
import org.mulima.internal.meta.DefaultAlbum
import org.mulima.internal.meta.DefaultDisc
import org.mulima.internal.meta.DefaultTrack
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.context.support.FileSystemXmlApplicationContext

class Mulima {
  static void main(String[] args) {
    CliBuilder cli = new CliBuilder(usage:'mulima [options] [libraryName]...', header:'Options:')
    cli.with {
      h longOpt:'help', 'Prints this help message'
      l longOpt:'list', 'Lists the libraries currently configured.'
      s longOpt:'stats', 'Gives stats on the number of albums in each library.'
      p longOpt:'process', 'Process new albums to generate album.xml files. (affects all ref libs)'
      u longOpt:'update', 'Updates albums in your destination libraries. (implies --process)'
      f longOpt:'force', 'Forces the update on all albums, including up to date. (only used with --update)'
      v longOpt:'verify', 'Verifies all album.xml files.'
      _ longOpt:'no-prompt', 'Will not prompt user to choose if algorithm is unsure.'
      _ longOpt:'status', 'Lists the status of each album.'
      _ longOpt:'fix-meta', 'Fixes common metadata problems.'
      _ longOpt:'create-stubs', 'Creates stub album.xml files with cue sheet info for albums without metadata'
    }

    def options = cli.parse(args)
    if (!options || options.h || (options.f && !options.u)) {
      cli.usage()
      return
    }

    def configFile = System.properties['mulima.configurationFile']
    ApplicationContext context
    if (configFile == null) {
      context = new ClassPathXmlApplicationContext('spring-context.xml')
    } else {
      context = new FileSystemXmlApplicationContext(configFile)
    }

    MulimaService service = context.getBean(MulimaService.class)
    service.tempDir = new TempDir().newChild('mulima')

    LibraryManager manager = context.getBean(LibraryManager.class)

    def filter = {
      options.arguments().contains(it.name) || options.arguments().empty
    }

    def refLibs = service.libraryService.refLibs.findAll(filter)
    def destLibs = service.libraryService.destLibs.findAll(filter)

    if (options.l) {
      println 'Reference Libraries:'
      println '--------------------'
      refLibs.each {
        println formatLib(it)
      }
      println ''
      println 'Destination Libraries:'
      println '----------------------'
      destLibs.each {
        println formatLib(it)
      }
    } else if (options.s) {
      (refLibs + destLibs).each { Library lib ->
        def outdated = lib.all.findAll { LibraryAlbum album ->
          album.id != null && !service.libraryService.isUpToDate(album, true)
        }
        println formatLib(lib)
        if (lib instanceof ReferenceLibrary) {
          println "  New:          ${lib.new.size()}"
        }
        println "  Out of Date:  ${outdated.size()}"
        println "  Total:        ${lib.all.size()}"
      }
    } else if (options.status) {
      (refLibs + destLibs).each { Library lib ->
        println formatLib(lib)
        lib.all.each { LibraryAlbum album ->
          boolean upToDate = service.libraryService.isUpToDate(album, true)
          println '\t' + formatAlbum(album, upToDate)
        }
      }
    } else if (options.p) {
      manager.processNew(!options.'no-prompt')
    } else if (options.u) {
      manager.processNew(!options.'no-prompt')
      manager.update(destLibs)
    } else if (options.v) {
      refLibs*.all*.each { LibraryAlbum refAlbum ->
        if (refAlbum.id != null) {
          if (refAlbum.album == null) {
            println "Invalid album.xml ${refAlbum.dir}"
          } else {
            try {
              refAlbum.name
            } catch (Exception e) {
              println "Invalid album.xml ${refAlbum.dir}: ${e.message}"
            }
          }
        }
      }
    } else if (options.'fix-meta') {
      refLibs*.all*.each { LibraryAlbum refAlbum ->
        if (refAlbum.album == null) {
          return
        }
        Album album = refAlbum.album
        boolean anyFixes = false

        //look for data tracks (i.e. tracks with no start point)
        album.discs*.tracks*.retainAll { Track track ->
          if (track.startPoint == null) {
            println "Removing data track from ${refAlbum.name}"
            anyFixes = true
            return false
          } else {
            return true
          }
        }

        //look for any duplicate tracks (i.e. tracks whose names were split across two)
        album.discs.each { Disc disc ->
          def cleanTracks = []
          disc.tracks.each { Track track ->
            Track otherTrack = cleanTracks.find { it.num == track.num && it.startPoint == track.startPoint }
            if (otherTrack == null) {
              cleanTracks << track
            } else {
              println "Consolidating tracks on ${refAlbum.name}"
              assert otherTrack.getAll(GenericTag.TITLE).size() == 1
              assert track.getAll(GenericTag.TITLE).size() == 1
              def title = otherTrack.getFirst(GenericTag.TITLE) + track.getFirst(GenericTag.TITLE)
              otherTrack.remove(GenericTag.TITLE)
              otherTrack.add(GenericTag.TITLE, title)
              anyFixes = true
            }
          }
          disc.tracks.removeAll { true }
          disc.tracks.addAll(cleanTracks)
        }

        if (anyFixes) {
          service.fileService.getComposer(Album).compose(new File(refAlbum.dir, Album.FILE_NAME), album)
        }
      }
    } else if (options.'create-stubs') {
      refLibs.each { ReferenceLibrary refLib ->
        refLib.new.each { refAlbum ->
          Album album = new DefaultAlbum()
          refAlbum.cueSheets.each { CueSheet cue ->
            Disc disc = new DefaultDisc(album)
            disc.add(GenericTag.DISC_NUMBER, Integer.toString(cue.getNum()))
            cue.getMap().each { GenericTag tag, List values ->
              disc.addAll(tag, values)
            }
            cue.cuePoints.each { CuePoint point ->
              Track track = new DefaultTrack(disc)
              track.add(GenericTag.TRACK_NUMBER, Integer.toString(point.getTrack()))
              track.setStartPoint(point)
              disc.tracks.add(track)
            }
            album.discs.add(disc)
          }

          album.tidy()
          service.getFileService().getComposer(Album.class).compose(new File(refAlbum.getDir(), "album.xml"), album)
        }
      }
    }
  }

  private static String formatLib(Library lib) {
    return "${lib.name} (${lib.format}) - ${lib.rootDir}"
  }

  private static String formatAlbum(LibraryAlbum album, boolean upToDate) {
    if (album.id == null) {
      return "${upToDate} - ${album.dir.canonicalPath - album.lib.rootDir.canonicalPath} (New)"
    } else {
      return "${upToDate} - ${album.name}"
    }
  }
}
