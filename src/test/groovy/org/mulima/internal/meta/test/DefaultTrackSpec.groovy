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
package org.mulima.internal.meta.test

import org.mulima.api.meta.Metadata
import org.mulima.api.meta.Track
import org.mulima.api.meta.test.TrackSpec
import org.mulima.internal.meta.DefaultTrack

class DefaultTrackSpec extends TrackSpec {
  def setupSpec() {
    factory.registerImplementation(Metadata, DefaultTrack)
    factory.registerImplementation(Track, DefaultTrack)
  }
}
