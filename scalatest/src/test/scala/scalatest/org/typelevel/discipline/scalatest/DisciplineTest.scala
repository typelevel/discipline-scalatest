/*
 * Copyright (c) 2019 Typelevel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.typelevel.discipline
package scalatest

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.Checkers

trait DummyBase extends Discipline with Checkers {
  checkAll("Dummy", DummyLaws.dummy)
}

class DummyFlatSpec extends AnyFlatSpec with DummyBase with FlatSpecDiscipline
class DummyFreeSpec extends AnyFreeSpec with DummyBase with FreeSpecDiscipline
class DummyFunSpec extends AnyFunSpec with DummyBase with FunSpecDiscipline
class DummyFunSuite extends AnyFunSuite with DummyBase with FunSuiteDiscipline
class DummyWordSpec extends AnyWordSpec with DummyBase with WordSpecDiscipline
