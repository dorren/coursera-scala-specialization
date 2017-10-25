import java.text.{BreakIterator, Normalizer}

import calculator._
import bank.{BankAccount, CPA}
import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader

val a = new BankAccount
val b = new BankAccount
val total = CPA.total(List(a, b))
total()

a.deposit(20)
total()



var v1 = Var(1)
var v2 = Var(2)
val signal1 = Signal(v1() + v2())
signal1()

v1() = 3
v2() = 4
signal1()

v2 = Var(100)   // signal now listen for old v2()
v2() = 5
signal1()

// trying map
val m = Map("a" -> 1, "b" -> 2)
val m2 = m.map({case (k, v) => k -> (v + 10)})
val m3 = m.map({case (k, v) => v})
m.get("z")
m - "a"

val zero = 0.0
0 == zero





