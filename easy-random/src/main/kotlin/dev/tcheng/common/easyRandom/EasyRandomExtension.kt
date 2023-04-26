package dev.tcheng.common.easyRandom

import org.jeasy.random.EasyRandom
import kotlin.reflect.KClass
import kotlin.streams.asSequence

fun <T : Any> EasyRandom.nextObject(type: KClass<T>): T = this.nextObject(type.javaObjectType)

fun <T : Any> EasyRandom.objects(type: KClass<T>, size: Int = 2): Sequence<T> =
    this.objects(type.javaObjectType, size).asSequence()

fun EasyRandom.nextString(): String = this.nextObject(String::class)
