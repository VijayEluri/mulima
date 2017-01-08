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
package org.mulima.internal.library.test

import org.mulima.api.file.CachedDir
import org.mulima.api.file.CachedFile
import org.mulima.api.file.Digest
import org.mulima.api.file.FileService
import org.mulima.api.file.audio.AudioFile
import org.mulima.api.library.Library
import org.mulima.api.library.LibraryAlbum
import org.mulima.api.meta.Album
import org.mulima.internal.library.DefaultLibraryAlbum

import spock.lang.Specification

class DefaultLibraryAlbumSpec extends Specification {
  def 'getId returns null if the digest is null'() {
    given:
    LibraryAlbum album = album(null, null, null, null)
    expect:
    album.digest == null
    album.id == null
  }

  def 'getId returns null if there isn\'t an ID on the digest'() {
    given:
    Digest digest = Mock(Digest)
    LibraryAlbum album = album(null, digest, null, null)
    expect:
    album.digest == digest
    album.id == null
  }

  def 'getId returns the ID'() {
    given:
    Digest digest = Mock(Digest)
    UUID id = new UUID(100L, 5L)
    digest.id >> id
    LibraryAlbum album = album(null, digest, null, null)
    expect:
    album.digest == digest
    album.id == id
  }

  def 'getSourceId returns null if the digest is null'() {
    given:
    LibraryAlbum album = album(null, null, null, null)
    expect:
    album.sourceId == null
  }

  def 'getSourceId returns null if there isn\'t an ID on the digest'() {
    given:
    LibraryAlbum album = album(null, null, Mock(Digest), null)
    expect:
    album.sourceId == null
  }

  def 'getSourceId returns the ID'() {
    given:
    Digest digest = Mock(Digest)
    UUID id = new UUID(100L, 5L)
    digest.id >> id
    LibraryAlbum album = album(null, null, digest, null)
    expect:
    album.sourceId == id
  }

  def 'getAlbum returns null if cached album is null'() {
    given:
    LibraryAlbum album = album(null, null, null, null)
    expect:
    album.album == null
  }

  def 'getAlbum returns album'() {
    given:
    Album meta = Mock(Album)
    LibraryAlbum album = album(meta, null, null, null)
    expect:
    album.album == meta
  }

  def 'getAudioFiles returns null if the cached file returns null'() {
    given:
    LibraryAlbum album = album(null, null, null, null)
    expect:
    album.audioFiles == null
  }

  def 'getAudioFiles returns audio files'() {
    given:
    Set audioFiles = Mock(Set)
    LibraryAlbum album = album(null, null, null, audioFiles)
    expect:
    album.audioFiles == audioFiles
  }

  LibraryAlbum album(Album album, Digest digest, Digest sourceDigest, Set audioFiles) {
    FileService service = Mock(FileService)

    service.createCachedFile(Album, _) >> file(album)
    service.createCachedFile(Digest, { it.name == Digest.FILE_NAME }) >> file(digest)
    service.createCachedFile(Digest, { it.name == Digest.SOURCE_FILE_NAME }) >> file(sourceDigest)
    service.createCachedDir(AudioFile, _) >> dir(audioFiles)

    return new DefaultLibraryAlbum(service, new File('tmp'), Mock(Library))
  }

  CachedFile file(Object obj) {
    CachedFile file = Mock(CachedFile)
    file.value >> obj
    return file
  }

  CachedDir dir(Object obj) {
    CachedDir dir = Mock(CachedDir)
    dir.values >> obj
    return dir
  }
}
