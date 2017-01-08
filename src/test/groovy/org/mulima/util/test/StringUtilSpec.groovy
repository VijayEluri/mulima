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
package org.mulima.util.test

import org.mulima.util.StringUtil

import spock.lang.Specification

class StringUtilSpec extends Specification {
  def 'levenshteinDistance returns correctly computed result'() {
    expect:
    StringUtil.levenshteinDistance(word1, word2) == distance
    where:
    word1		| word2			| distance
    'kitten'	| 'kitten'		| 0
    'kitten'	| 'mitten'		| 1
    'toilet'	| 'toil'		| 2
    'kitten'	| 'sitting'		| 3
    'Potatoes'	| 'potatoe'		| 2
    'Genesis'	| 'Regenesis'	| 3
    null        | null          | 0
    null        | 'Test'        | 4
    'Testing'   | null          | 7
  }

  def 'join returns all elements contatenated together with the glue text in between'() {
    expect:
    StringUtil.join(array, glue) == result
    where:
    array						| glue		| result
    ['org', 'mulima', 'util']	| '.'		| 'org.mulima.util'
    ['test', 'runn', 'end']	| 'ing '	| 'testing running end'
  }

  def 'makeSafe replaces all invalid characters with underscores'() {
    expect:
    StringUtil.makeSafe(original) == result
    where:
    original		| result
    'Testing\\123'	| 'Testing_123'
    'Blah/456'		| 'Blah_456'
    ':test'			| '_test'
    'google?'		| 'google_'
    'h*m*m**'		| 'h_m_m_'
    'w>hy'			| 'w_hy'
    'wh<o'			| 'wh_o'
    '||or'			| '_or'
    '...And...'     | 'And'
    '   Blah. '     | 'Blah'
  }

  def 'commonString finds the common part of the string from the beginning'() {
    expect:
    StringUtil.commonString(str1, str2) == result
    where:
    str1					| str2						| result
    'The Sane Day (Disc 1)' | 'The Sane Day (Disc 2)'	| 'The Sane Day (Disc '
    'Tomatoe, Potatoe'		| 'Tomatoe, Tomatoe'		| 'Tomatoe, '
    'Orange Juice'			| 'Orange Julius'			| 'Orange Ju'
    'First Year'			| 'Second Year'				| ''
  }
}
