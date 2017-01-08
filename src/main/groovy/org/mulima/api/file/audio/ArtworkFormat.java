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
package org.mulima.api.file.audio;

import java.io.File;

import org.springframework.util.StringUtils;

public enum ArtworkFormat {
  JPEG("jpg", false),
  PNG("png", true);

  private final String ext;
  private final boolean lossless;

  /**
   * Constructs an audio file type from an extension.
   *
   * @param ext the extension
   */
  private ArtworkFormat(String ext, boolean lossless) {
    this.ext = ext;
    this.lossless = lossless;
  }

  /**
   * Gets the file extension used for this type.
   *
   * @return file extension
   */
  public String getExtension() {
    return ext;
  }

  public boolean isLossless() {
    return lossless;
  }

  /**
   * Tests a file to see if it is of the same type. Uses the file extension.
   *
   * @param file file to test.
   * @return <code>true</code> if of the same type, <code>false</code> otherwise
   */
  public boolean isFormatOf(File file) {
    String extension = StringUtils.getFilenameExtension(file.getAbsolutePath());
    return this.getExtension().equals(extension);
  }

  /**
   * Gets the file type of a given file.
   *
   * @param file the file to get the type of
   * @return the type of the file
   */
  public static ArtworkFormat valueOf(File file) {
    String extension = StringUtils.getFilenameExtension(file.getAbsolutePath());
    for (ArtworkFormat type : ArtworkFormat.values()) {
      if (type.getExtension().equals(extension)) {
        return type;
      }
    }
    throw new IllegalArgumentException("No type with extension \"" + extension + "\" exists.");
  }
}
