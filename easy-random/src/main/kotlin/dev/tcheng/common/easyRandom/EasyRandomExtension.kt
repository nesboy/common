package dev.tcheng.common.easyRandom

import org.jeasy.random.EasyRandom
import kotlin.streams.asSequence

inline fun <reified T : Any> EasyRandom.nextObject(): T = this.nextObject(T::class.java)

inline fun <reified T : Any> EasyRandom.objects(size: Int = 2): Sequence<T> =
    this.objects(T::class.java, size).asSequence()

fun EasyRandom.nextString(): String = this.nextObject<String>()
