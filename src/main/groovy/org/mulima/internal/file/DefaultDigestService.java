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
package org.mulima.internal.file;

import java.io.File;

import org.mulima.api.file.Digest;
import org.mulima.api.file.DigestService;
import org.mulima.api.library.LibraryAlbum;
import org.springframework.stereotype.Service;

/**
 * Default implementation of a digest service.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Service
public class DefaultDigestService implements DigestService {
  private DigestDao dao = new DigestDao();

  /** {@inheritDoc} */
  @Override
  public Digest create(LibraryAlbum libAlbum) {
    return new DigestBuilder(libAlbum).build();
  }

  /** {@inheritDoc} */
  @Override
  public Digest read(LibraryAlbum libAlbum) {
    return dao.parse(new File(libAlbum.getDir(), Digest.FILE_NAME));
  }

  /** {@inheritDoc} */
  @Override
  public Digest readSource(LibraryAlbum libAlbum) {
    return dao.parse(new File(libAlbum.getDir(), Digest.SOURCE_FILE_NAME));
  }

  /** {@inheritDoc} */
  @Override
  public void write(LibraryAlbum libAlbum, LibraryAlbum sourceAlbum) {
    dao.compose(new File(libAlbum.getDir(), Digest.FILE_NAME), create(libAlbum));
    if (sourceAlbum != null) {
      dao.compose(new File(libAlbum.getDir(), Digest.SOURCE_FILE_NAME), create(sourceAlbum));
    }
  }
}
