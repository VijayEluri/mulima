package org.mulima.main

import org.mulima.api.library.Library
import org.mulima.api.library.LibraryAlbum
import org.mulima.api.library.LibraryManager
import org.mulima.api.meta.GenericTag
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
			context = new ClassPathXmlApplicationContext('applicationContext.xml')
		} else {
			context = new FileSystemXmlApplicationContext(configFile)
		}
		LibraryManager manager = context.getBean('libManager', LibraryManager.class)
		
		def filter = {
			options.arguments().contains(it.name) || options.arguments().empty
		}
		
		def refLibs = manager.refLibs.findAll(filter)
		def destLibs = manager.destLibs.findAll(filter)
		
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
			(refLibs + destLibs).each { lib ->
				println formatLib(lib)
				lib.scanAlbums()
				lib.all.each { album ->
					boolean upToDate = true
					if (album.sourceDigest != null) {
						def source = manager.getAlbum(album.sourceDigest.id)
						upToDate = source.isUpToDate() 
					}
					if (upToDate) {
						upToDate = album.isUpToDate(false)
					}
					
					println '\t' + formatAlbum(album, upToDate)
				}
			}
		} else if (options.p) {
			manager.scanAll()
			manager.processNew()
		} else if (options.u) {
			manager.scanAll()
			manager.processNew()
			manager.updateLibs(destLibs)
		}
	}
	
	private static String formatLib(Library lib) {
		return "${lib.name} (${lib.type}) - ${lib.rootDir}"
	}
	
	private static String formatAlbum(LibraryAlbum album, boolean upToDate) {
		if (album.album == null) {
			return "${album.dir.canonicalPath - album.lib.rootDir.canonicalPath} (New) - ${upToDate}"
		} else {
			return "${album.album.getFlat(GenericTag.ARTIST)} - ${album.album.getFlat(GenericTag.ALBUM)} - ${upToDate}"
		}
	}
}
