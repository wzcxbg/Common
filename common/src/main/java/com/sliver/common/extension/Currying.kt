package com.sliver.common.extension

class Function1Impl<T1, out R>(private val function: (T1) -> R) {
    operator fun invoke(a1: T1) = function.invoke(a1)
}

class Function2Impl<T1, T2, out R>(private val function: (T1, T2) -> R) {
    operator fun invoke(a1: T1) = Function1Impl<T2, R> { a2 -> function.invoke(a1, a2) }
    operator fun invoke(a1: T1, a2: T2) = function.invoke(a1, a2)
}

class Function3Impl<T1, T2, T3, out R>(private val function: (T1, T2, T3) -> R) {
    operator fun invoke(a1: T1) = Function2Impl<T2, T3, R> { a2, a3 -> function.invoke(a1, a2, a3) }
    operator fun invoke(a1: T1, a2: T2) = Function1Impl<T3, R> { a3 -> function.invoke(a1, a2, a3) }
    operator fun invoke(a1: T1, a2: T2, a3: T3) = function.invoke(a1, a2, a3)
}

class Function4Impl<T1, T2, T3, T4, out R>(private val function: (T1, T2, T3, T4) -> R) {
    operator fun invoke(a1: T1) = Function3Impl<T2, T3, T4, R> { a2, a3, a4 -> function.invoke(a1, a2, a3, a4) }
    operator fun invoke(a1: T1, a2: T2) = Function2Impl<T3, T4, R> { a3, a4 -> function.invoke(a1, a2, a3, a4) }
    operator fun invoke(a1: T1, a2: T2, a3: T3) = Function1Impl<T4, R> { a4 -> function.invoke(a1, a2, a3, a4) }
    operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4) = function.invoke(a1, a2, a3, a4)
}

class Function5Impl<T1, T2, T3, T4, T5, out R>(private val function: (T1, T2, T3, T4, T5) -> R) {
    operator fun invoke(a1: T1) = Function4Impl<T2, T3, T4, T5, R> { a2, a3, a4, a5 -> function.invoke(a1, a2, a3, a4, a5) }
    operator fun invoke(a1: T1, a2: T2) = Function3Impl<T3, T4, T5, R> { a3, a4, a5 -> function.invoke(a1, a2, a3, a4, a5) }
    operator fun invoke(a1: T1, a2: T2, a3: T3) = Function2Impl<T4, T5, R> { a4, a5 -> function.invoke(a1, a2, a3, a4, a5) }
    operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4) = Function1Impl<T5, R> { a5 -> function.invoke(a1, a2, a3, a4, a5) }
    operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5) = function.invoke(a1, a2, a3, a4, a5)
}

class Function6Impl<T1, T2, T3, T4, T5, T6, out R>(private val function: (T1, T2, T3, T4, T5, T6) -> R) {
    operator fun invoke(a1: T1) = Function5Impl<T2, T3, T4, T5, T6, R> { a2, a3, a4, a5, a6 -> function.invoke(a1, a2, a3, a4, a5, a6) }
    operator fun invoke(a1: T1, a2: T2) = Function4Impl<T3, T4, T5, T6, R> { a3, a4, a5, a6 -> function.invoke(a1, a2, a3, a4, a5, a6) }
    operator fun invoke(a1: T1, a2: T2, a3: T3) = Function3Impl<T4, T5, T6, R> { a4, a5, a6 -> function.invoke(a1, a2, a3, a4, a5, a6) }
    operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4) = Function2Impl<T5, T6, R> { a5, a6 -> function.invoke(a1, a2, a3, a4, a5, a6) }
    operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5) = Function1Impl<T6, R> { a6 -> function.invoke(a1, a2, a3, a4, a5, a6) }
    operator fun invoke(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6) = function.invoke(a1, a2, a3, a4, a5, a6)
}

fun <T1, R> ((T1) -> R).currying() = Function1Impl(this)
fun <T1, T2, R> ((T1, T2) -> R).currying() = Function2Impl(this)
fun <T1, T2, T3, R> ((T1, T2, T3) -> R).currying() = Function3Impl(this)
fun <T1, T2, T3, T4, R> ((T1, T2, T3, T4) -> R).currying() = Function4Impl(this)
fun <T1, T2, T3, T4, T5, R> ((T1, T2, T3, T4, T5) -> R).currying() = Function5Impl(this)
fun <T1, T2, T3, T4, T5, T6, R> ((T1, T2, T3, T4, T5, T6) -> R).currying() = Function6Impl(this)

fun <T1, R> ((T1) -> R).currying(a1: T1) = this.currying()(a1)
fun <T1, T2, R> ((T1, T2) -> R).currying(a1: T1) = this.currying()(a1)
fun <T1, T2, R> ((T1, T2) -> R).currying(a1: T1, a2: T2) = this.currying()(a1, a2)
fun <T1, T2, T3, R> ((T1, T2, T3) -> R).currying(a1: T1) = this.currying()(a1)
fun <T1, T2, T3, R> ((T1, T2, T3) -> R).currying(a1: T1, a2: T2) = this.currying()(a1, a2)
fun <T1, T2, T3, R> ((T1, T2, T3) -> R).currying(a1: T1, a2: T2, a3: T3) = this.currying()(a1, a2, a3)
fun <T1, T2, T3, T4, R> ((T1, T2, T3, T4) -> R).currying(a1: T1) = this.currying()(a1)
fun <T1, T2, T3, T4, R> ((T1, T2, T3, T4) -> R).currying(a1: T1, a2: T2) = this.currying()(a1, a2)
fun <T1, T2, T3, T4, R> ((T1, T2, T3, T4) -> R).currying(a1: T1, a2: T2, a3: T3) = this.currying()(a1, a2, a3)
fun <T1, T2, T3, T4, R> ((T1, T2, T3, T4) -> R).currying(a1: T1, a2: T2, a3: T3, a4: T4) = this.currying()(a1, a2, a3, a4)
fun <T1, T2, T3, T4, T5, R> ((T1, T2, T3, T4, T5) -> R).currying(a1: T1) = this.currying()(a1)
fun <T1, T2, T3, T4, T5, R> ((T1, T2, T3, T4, T5) -> R).currying(a1: T1, a2: T2) = this.currying()(a1, a2)
fun <T1, T2, T3, T4, T5, R> ((T1, T2, T3, T4, T5) -> R).currying(a1: T1, a2: T2, a3: T3) = this.currying()(a1, a2, a3)
fun <T1, T2, T3, T4, T5, R> ((T1, T2, T3, T4, T5) -> R).currying(a1: T1, a2: T2, a3: T3, a4: T4) = this.currying()(a1, a2, a3, a4)
fun <T1, T2, T3, T4, T5, R> ((T1, T2, T3, T4, T5) -> R).currying(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5) = this.currying()(a1, a2, a3, a4, a5)
fun <T1, T2, T3, T4, T5, T6, R> ((T1, T2, T3, T4, T5, T6) -> R).currying(a1: T1) = this.currying()(a1)
fun <T1, T2, T3, T4, T5, T6, R> ((T1, T2, T3, T4, T5, T6) -> R).currying(a1: T1, a2: T2) = this.currying()(a1, a2)
fun <T1, T2, T3, T4, T5, T6, R> ((T1, T2, T3, T4, T5, T6) -> R).currying(a1: T1, a2: T2, a3: T3) = this.currying()(a1, a2, a3)
fun <T1, T2, T3, T4, T5, T6, R> ((T1, T2, T3, T4, T5, T6) -> R).currying(a1: T1, a2: T2, a3: T3, a4: T4) = this.currying()(a1, a2, a3, a4)
fun <T1, T2, T3, T4, T5, T6, R> ((T1, T2, T3, T4, T5, T6) -> R).currying(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5) = this.currying()(a1, a2, a3, a4, a5)
fun <T1, T2, T3, T4, T5, T6, R> ((T1, T2, T3, T4, T5, T6) -> R).currying(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6) = this.currying()(a1, a2, a3, a4, a5, a6)


fun main() {
    val sum2: (Int, Int) -> Int = { a, b -> a + b }
    sum2.currying()(1, 2).let { println(it) }
    sum2.currying()(1)(2).let { println(it) }

    val sum3: (Int, Int, Int) -> Int = { a, b, c -> a + b + c }
    sum3.currying()(1, 2, 3).let { println(it) }
    sum3.currying()(1)(2, 3).let { println(it) }
    sum3.currying()(1, 2)(3).let { println(it) }
    sum3.currying()(1)(2)(3).let { println(it) }

    val sum4: (Int, Int, Int, Int) -> Int = { a, b, c, d -> a + b + c + d }
    sum4.currying()(1, 2, 3, 4).let { println(it) }
    sum4.currying()(1, 2)(3, 4).let { println(it) }
    sum4.currying()(1)(2, 3, 4).let { println(it) }
    sum4.currying()(1, 2, 3)(4).let { println(it) }
    sum4.currying()(1)(2)(3)(4).let { println(it) }
}
