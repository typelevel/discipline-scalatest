---
layout: home

---

# discipline-scalatest - ScalaTest binding for Typelevel Discipline [![Build Status](https://travis-ci.com/rossabaker/discipline-scalatest.svg?branch=master)](https://travis-ci.com/typelevel/discipline-scalatest) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.typelevel/discipline-scalatest_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.typelevel/discipline-scalatest_2.12)

## Quick Start

To use discipline-scalatest in an existing SBT project with Scala 2.11 or later, add the following dependency to your `build.sbt`:

```scala
libraryDependencies ++= Seq(
  "org.typelevel" %% "discipline-scalatest" % "<version>"
)
```

## Mixing in `Discipline`

Suppose we have the following laws for truth. More useful laws are published in projects like [`cats-laws`](https://github.com/typelevel/cats), [`cats-effect-laws`](https://github.com/typelevel/cats-effect), and [`spire-laws`](https://gihtub.com/typelevel/spire).

```scala mdoc
import org.scalacheck.Prop
import org.typelevel.discipline.Laws

object TruthLaws extends Laws {
  def truth = new DefaultRuleSet(
    name = "truth",
    parent = None,
    "true" -> Prop(_ => Prop.Result(status = Prop.True))
  )
}
```

discipline-scalatest provides a `FunSuiteDiscipline` mixin (as well as similar traits for `FlatSpec` and `FunSpec`), whose `checkAll` helper lets us easily check the laws in ScalaTest:

```scala mdoc
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.scalacheck.Checkers
import org.typelevel.discipline.scalatest.FunSuiteDiscipline

class TruthSuite extends AnyFunSuite with FunSuiteDiscipline with Checkers {
  checkAll("Truth", TruthLaws.truth)
}
```

## Compatibility

discipline-scalatest-1.0.0-M1 works with:

* discipline-core-1.x
* scalatest-3.1.0-SNAP13
* scalatestplus-scalacheck-1.0.0-SNAP8

## Roadmap

* We will release discipline-scalatest-1.0.0 once scalatest-3.1.0 and scalatestplus-scalacheck-1.0.0 are final.
