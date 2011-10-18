package org.mulima.internal.service

import org.mulima.api.audio.tool.ToolService
import org.mulima.api.file.DigestService
import org.mulima.api.file.FileService
import org.mulima.api.file.TempDir
import org.mulima.api.library.LibraryService
import org.mulima.api.service.MulimaService

class DefaultMulimaService implements MulimaService {
	TempDir tempDir = null
	LibraryService libraryService = null
	FileService fileService = null
	DigestService digestService = null
	ToolService toolService = null
}
