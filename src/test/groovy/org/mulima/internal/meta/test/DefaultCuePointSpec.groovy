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

import org.mulima.internal.meta.DefaultCuePoint

import spock.lang.Specification

class DefaultCuePointSpec extends Specification {
  def 'constructor succeeds with valid parameters'() {
    when:
    new DefaultCuePoint(1, 1, '23:32:23')
    then:
    notThrown(IllegalArgumentException)
  }

  def 'constructor fails when track is negative'() {
    when:
    new DefaultCuePoint(-1, 1, '23:32:23')
    then:
    thrown(IllegalArgumentException)
  }

  def 'constructor fails when index is negative'() {
    when:
    new DefaultCuePoint(1, -1, '23:32:23')
    then:
    thrown(IllegalArgumentException)
  }

  def 'constructor fails when minutes are negative'() {
    when:
    new DefaultCuePoint(1, 1, '-23:32:23')
    then:
    thrown(IllegalArgumentException)
  }

  def 'constructor fails when seconds are negative'() {
    when:
    new DefaultCuePoint(1, 1, '23:-32:23')
    then:
    thrown(IllegalArgumentException)
  }

  def 'constructor fails when seconds are greater than 59'() {
    when:
    new DefaultCuePoint(1, 1, '23:60:23')
    then:
    thrown(IllegalArgumentException)
  }

  def 'constructor fails when frames are negative'() {
    when:
    new DefaultCuePoint(1, 1, '23:32:-23')
    then:
    thrown(IllegalArgumentException)
  }

  def 'constructor fails when frames are greates than 74'() {
    when:
    new DefaultCuePoint(1, 1, '23:32:75')
    then:
    thrown(IllegalArgumentException)
  }
}
