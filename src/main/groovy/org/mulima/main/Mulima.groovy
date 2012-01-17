package org.mulima.main

import org.mulima.api.file.Digest
import org.mulima.api.file.DigestService
import org.mulima.api.file.TempDir
import org.mulima.api.library.Library
import org.mulima.api.library.LibraryAlbum
import org.mulima.api.library.LibraryManager
import org.mulima.api.library.LibraryService
import org.mulima.api.library.ReferenceLibrary
import org.mulima.api.service.MulimaService
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
