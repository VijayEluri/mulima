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
package org.mulima.internal.file.audio.test

import org.mulima.internal.file.audio.DefaultTrackFile

import spock.lang.Specification

class DefaultDiscTrackSpec extends Specification {
  def 'constructor works with positive disc number and positive track number'() {
    when:
    new DefaultTrackFile(new File('dummy.wav'), discNum, trackNum)
    then:
    notThrown(IllegalArgumentException)
    where:
    discNum	| trackNum
    0		| 0
    1		| 5
    5		| 1
    100		| 1000
  }

  def 'constructor fails with null file'() {
    when:
    new DefaultTrackFile(null, 1, 1)
    then:
    thrown(NullPointerException)
  }

  def 'constructor fails with invalid extension on file'() {
    when:
    new DefaultTrackFile(new File('blah.zzz'), 1, 1)
    then:
    thrown(IllegalArgumentException)
  }

  def 'constructor fails with negative discNum'() {
    when:
    new DefaultTrackFile(new File('dummy.wav'), discNum, 1)
    then:
    thrown(IllegalArgumentException)
    where:
    discNum << [-1, -5, -100]
  }

  def 'constructor fails with negative trackNum'() {
    when:
    new DefaultTrackFile(new File('dummy.wav'), 1, trackNum)
    then:
    thrown(IllegalArgumentException)
    where:
    trackNum << [-1, -5, -100]
  }
}
