package org.typelevel.discipline
package scalatest

import org.scalatest.TestRegistration
import org.scalatestplus.scalacheck.Checkers

trait Discipline extends Checkers { self: TestRegistration =>

  def checkAll(name: String, ruleSet: Laws#RuleSet): Unit = {
    for ((id, prop) <- ruleSet.all.properties)
      registerTest(s"${name}.${id}") {
        check(prop)
      }
  }

}
