package com.example.demo.util

import java.lang.reflect.InvocationTargetException
import java.math.BigDecimal


inline fun catchPrintExit(action: () -> Unit) {
    try {
        action()
    } catch (th: Throwable) {
        th.printStackTrace()
        System.exit(1)
    }
}


fun Any.andPrint() {
    println(this.toString())
}

fun String.andPrint() {
    println(this)
}

fun <V, T : Collection<V>> T.andPrint(): T {
    this.forEach { println(it) }
    return this
}

val Any?.isNull: Boolean
    get() = this == null


val Any?.notNull: Boolean
    get() = this != null


val Int.asDec: BigDecimal
    get() = BigDecimal(this)

fun org.hibernate.Session.saveOrUpdateAll(vararg entities: Any) {
    entities.forEach { this.saveOrUpdate(it) }
}

fun Throwable.findRootCause(): Throwable {
    var ex = this
    while (ex.cause.notNull) {
        ex = ex.cause!!
        if (ex is InvocationTargetException && ex.targetException.notNull)
            ex = ex.targetException
    }
    return ex
}

fun <T> Iterable<T>?.asSet(): MutableSet<T> = this?.toMutableSet() ?: mutableSetOf()


inline fun so(action: () -> Unit): Boolean {
    action()
    return true
}
