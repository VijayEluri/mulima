package org.mulima.main

import org.mulima.api.audio.AudioFormat
import org.mulima.api.file.Digest
import org.mulima.api.file.TempDir
import org.mulima.api.library.Library
import org.mulima.api.library.LibraryAlbum
import org.mulima.api.library.LibraryAlbumFactory
import org.mulima.api.library.LibraryManager
import org.mulima.api.meta.Album
import org.mulima.api.meta.CueSheet
import org.mulima.api.service.MulimaService
import org.mulima.internal.file.DigestDao
import org.mulima.internal.library.DefaultLibrary
import org.mulima.internal.library.DefaultLibraryAlbumFactory
import org.mulima.internal.library.DefaultReferenceLibrary
import org.mulima.internal.meta.AlbumXmlDao
import org.mulima.internal.meta.CueSheetParser
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.context.support.FileSystemXmlApplicationContext

class Mulima {
	static void main(String[] args) {
		CliBuilder cli = new CliBuilder(usage:'mulima [options] [libraryName]...', header:'Options:')
		cli.with {
			h longOpt:'help', 'Prints this help message'
			l longOpt:'list', 'Lists the libraries currently configured.'
			s longOpt:'status', 'Lists the status of each album.'
			p longOpt:'process', 'Process new albums to generate album.xml files. (affects all ref libs)'
			u longOpt:'update', 'Updates albums in your destination libraries. (implies --process)'
			f longOpt:'force', 'Forces the update on all albums, including up to date. (only used with --update)'
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
		
		File rootDir = new File('C:/Users/Andy/Desktop/Mulima')
		LibraryAlbumFactory albumFactory = new DefaultLibraryAlbumFactory(service.fileService)
		service.libraryService.refLibs = [new DefaultReferenceLibrary(albumFactory, 'Lossless Images', new File(rootDir, 'Beardfish'), AudioFormat.FLAC)
			/*, new DefaultReferenceLibrary(albumFactory, 'MP3 Reference', new File(rootDir, 'BeardfishMP3'), AudioFormat.MP3)*/] as Set
		service.libraryService.destLibs = [new DefaultLibrary(albumFactory, 'Lossless', new File(rootDir, 'BeardfishFlacLib'), AudioFormat.FLAC),
			new DefaultLibrary(albumFactory, 'iTunes', new File(rootDir, 'BeardfishAacLib'), AudioFormat.AAC)] as Set
		
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
				println formatLib(lib)
				lib.all.each { LibraryAlbum album ->
					boolean upToDate = service.libraryService.isUpToDate(album, true)
					println '\t' + formatAlbum(album, upToDate)
				}
			}
		} else if (options.p) {
			manager.processNew()
		} else if (options.u) {
			manager.processNew()
			manager.update(destLibs)
		}
	}
	
	private static String formatLib(Library lib) {
		return "${lib.name} (${lib.format}) - ${lib.rootDir}"
	}
	
	private static String formatAlbum(LibraryAlbum album, boolean upToDate) {
		if (album.album == null) {
			return "${album.dir.canonicalPath - album.lib.rootDir.canonicalPath} (New) - ${upToDate}"
		} else {
			return "${album.album.name} - ${upToDate}"
		}
	}
}
