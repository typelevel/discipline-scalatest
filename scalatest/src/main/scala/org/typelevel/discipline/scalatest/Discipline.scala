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

  /**
   * Convert from our configuration type to the one required by `Checkers`.
   *
   * We don't extend `Checkers` because we want to leave the user as much
   * control as possible over available testing styles. Unfortunately we need
   * this conversion because ScalaTest defines `PropertyCheckConfiguration`
   * as a case class in the `Configuration` trait.
   */
  final protected[this] def convertConfiguration(
    config: PropertyCheckConfiguration
  ): Checkers.PropertyCheckConfiguration =
    Checkers.PropertyCheckConfiguration(
      config.minSuccessful,
      config.maxDiscardedFactor,
      config.minSize,
      config.sizeRange,
      config.workers
    )

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
        name should first._1 in Checkers.check(first._2)(convertConfiguration(config), prettifier, pos)

        for ((id, prop) <- rest)
          it should id in Checkers.check(prop)(convertConfiguration(config), prettifier, pos)
    }
}

trait FunSpecDiscipline extends Discipline { self: AnyFunSpecLike with Configuration =>
  final def checkAll(name: String, ruleSet: Laws#RuleSet)(implicit config: PropertyCheckConfiguration,
                                                          prettifier: Prettifier,
                                                          pos: Position): Unit =
    describe(name) {
      for ((id, prop) <- ruleSet.all.properties)
        it(id) {
          Checkers.check(prop)(convertConfiguration(config), prettifier, pos)
        }
    }
}

trait FunSuiteDiscipline extends Discipline { self: AnyFunSuiteLike with Configuration =>
  final def checkAll(name: String, ruleSet: Laws#RuleSet)(implicit config: PropertyCheckConfiguration,
                                                          prettifier: Prettifier,
                                                          pos: Position): Unit =
    for ((id, prop) <- ruleSet.all.properties)
      test(s"${name}.${id}") {
        Checkers.check(prop)(convertConfiguration(config), prettifier, pos)
      }
}
