package org.mulima.internal.service

import org.mulima.api.audio.tool.ToolService
import org.mulima.api.file.DigestService
import org.mulima.api.file.FileService
import org.mulima.api.file.TempDir
import org.mulima.api.library.LibraryService
import org.mulima.api.service.MulimaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Default implementation of the MulimaService.  This simply
 * provides properties for each service.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Service
class DefaultMulimaService implements MulimaService {
	TempDir tempDir = null
	@Autowired LibraryService libraryService = null
	@Autowired FileService fileService = null
	@Autowired DigestService digestService = null
	@Autowired ToolService toolService = null
}
