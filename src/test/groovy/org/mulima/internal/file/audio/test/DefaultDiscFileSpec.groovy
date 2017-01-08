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

import org.mulima.internal.file.audio.DefaultDiscFile

import spock.lang.Specification

class DefaultDiscFileSpec extends Specification {
  def 'constructor works with positive disc number'() {
    when:
    new DefaultDiscFile(new File('dummy.wav'), discNum)
    then:
    notThrown(IllegalArgumentException)
    where:
    discNum << [0, 1, 2, 3, 1000]
  }

  def 'constructor fails with null file'() {
    when:
    new DefaultDiscFile(null, 1)
    then:
    thrown(NullPointerException)
  }

  def 'constructor fails with invalid extension on file'() {
    when:
    new DefaultDiscFile(new File('blah.zzz'), 1)
    then:
    thrown(IllegalArgumentException)
  }

  def 'constructor fails with negative discNum'() {
    when:
    new DefaultDiscFile(new File('dummy.wav'), discNum)
    then:
    thrown(IllegalArgumentException)
    where:
    discNum << [-1, -5, -100]
  }
}
