package org.typelevel.discipline
package scalatest

import org.scalatest.funsuite.AnyFunSuite

class DummyFunSuite extends AnyFunSuite with Discipline {
  checkAll("Dummy", DummyLaws.dummy)
}
