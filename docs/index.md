# discipline-scalatest

ScalaTest binding for Typelevel Discipline

## Quick Start

```scala
libraryDependencies ++= Seq(
  "org.typelevel" %% "discipline-scalatest" % "@VERSION@"
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
