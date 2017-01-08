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
import java.io.FileFilter;

import org.mulima.api.file.audio.AudioFormat;

/**
 * A filter to select only leaf directories.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class LeafDirFilter implements FileFilter {
  /**
   * Only accepts directories that have no child directories.
   *
   * @param file the file to test
   * @return {@code true} if the file is a leaf dir, {@code false} otherwise
   */
  @Override
  public boolean accept(File file) {
    if (file.isDirectory()) {
      boolean anyAudioFiles = false;
      for (File child : file.listFiles()) {
        if (child.isDirectory()) {
          return false;
        } else if (!anyAudioFiles && AudioFormat.isAudioFile(child)) {
          anyAudioFiles = true;
        }
      }
      return anyAudioFiles;
    } else {
      return false;
    }
  }
}
