package org.typelevel.discipline
package scalatest

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.scalacheck.Checkers

trait DummyBase extends Discipline with Checkers {
  checkAll("Dummy", DummyLaws.dummy)
}

class DummyFlatSpec extends AnyFlatSpec with DummyBase with FlatSpecDiscipline
class DummyFunSpec extends AnyFunSpec with DummyBase with FunSpecDiscipline
class DummyFunSuite extends AnyFunSuite with DummyBase with FunSuiteDiscipline
