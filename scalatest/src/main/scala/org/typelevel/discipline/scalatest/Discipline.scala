package org.typelevel.discipline
package scalatest

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatestplus.scalacheck.Checkers

trait Discipline {
  def checkAll(name: String, ruleSet: Laws#RuleSet): Unit
}

trait FlatSpecDiscipline extends Discipline { self: AnyFlatSpecLike =>
  final def checkAll(name: String, ruleSet: Laws#RuleSet): Unit =
    ruleSet.all.properties match {
      case first +: rest =>
        name should first._1 in Checkers.check(first._2)

        for ((id, prop) <- rest)
          it should id in Checkers.check(prop)
    }
}

trait FunSpecDiscipline extends Discipline { self: AnyFunSpecLike =>
  final def checkAll(name: String, ruleSet: Laws#RuleSet): Unit =
    describe(name) {
      for ((id, prop) <- ruleSet.all.properties)
        it(id) {
          Checkers.check(prop)
        }
    }
}

trait FunSuiteDiscipline extends Discipline { self: AnyFunSuiteLike =>
  final def checkAll(name: String, ruleSet: Laws#RuleSet): Unit =
    for ((id, prop) <- ruleSet.all.properties)
      test(s"${name}.${id}") {
        Checkers.check(prop)
      }
}
