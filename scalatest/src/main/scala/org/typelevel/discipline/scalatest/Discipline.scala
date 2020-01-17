package org.typelevel.discipline
package scalatest

import org.scalactic.Prettifier
import org.scalactic.source.Position
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.prop.Configuration
import org.scalatestplus.scalacheck.Checkers

trait Discipline { self: Configuration =>
  def checkAll(name: String, ruleSet: Laws#RuleSet)(implicit config: PropertyCheckConfiguration,
                                                    prettifier: Prettifier,
                                                    pos: Position): Unit
}

trait FlatSpecDiscipline extends Discipline { self: AnyFlatSpecLike with Configuration =>
  final def checkAll(name: String, ruleSet: Laws#RuleSet)(implicit config: PropertyCheckConfiguration,
                                                          prettifier: Prettifier,
                                                          pos: Position): Unit =
    ruleSet.all.properties match {
      case first +: rest =>
        name should first._1 in Checkers.check(first._2)

        for ((id, prop) <- rest)
          it should id in Checkers.check(prop)
    }
}

trait FunSpecDiscipline extends Discipline { self: AnyFunSpecLike with Configuration =>
  final def checkAll(name: String, ruleSet: Laws#RuleSet)(implicit config: PropertyCheckConfiguration,
                                                          prettifier: Prettifier,
                                                          pos: Position): Unit =
    describe(name) {
      for ((id, prop) <- ruleSet.all.properties)
        it(id) {
          Checkers.check(prop)
        }
    }
}

trait FunSuiteDiscipline extends Discipline { self: AnyFunSuiteLike with Configuration =>
  final def checkAll(name: String, ruleSet: Laws#RuleSet)(implicit config: PropertyCheckConfiguration,
                                                          prettifier: Prettifier,
                                                          pos: Position): Unit =
    for ((id, prop) <- ruleSet.all.properties)
      test(s"${name}.${id}") {
        Checkers.check(prop)
      }
}
