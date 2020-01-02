package org.typelevel.discipline
package scalatest

import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatestplus.scalacheck.Checkers

trait Discipline extends Checkers { self: AnyFunSuiteLike =>

  def checkAll(name: String, ruleSet: Laws#RuleSet): Unit = {
    for ((id, prop) <- ruleSet.all.properties)
      test(s"${name}.${id}") {
        check(prop)
      }
  }

}
