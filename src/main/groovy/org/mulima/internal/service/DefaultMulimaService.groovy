package org.mulima.internal.service

import org.mulima.api.audio.tool.ToolService
import org.mulima.api.file.DigestService
import org.mulima.api.file.FileService
import org.mulima.api.file.TempDir
import org.mulima.api.library.LibraryService
import org.mulima.api.service.MulimaService

/**
 * Default implementation of the MulimaService.  This simply
 * provides properties for each service.
 * @author Andy
 * @version 0.1.0
 * @since 0.1.0
 */
class DefaultMulimaService implements MulimaService {
	TempDir tempDir = null
	LibraryService libraryService = null
	FileService fileService = null
	DigestService digestService = null
	ToolService toolService = null
}
