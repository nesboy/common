package dev.tcheng.common.scope.random

import org.jeasy.random.EasyRandomParameters

object EasyRandomParameterFactory {

    fun createDefault(): EasyRandomParameters = EasyRandomParameters()
        .collectionSizeRange(2, 3)
        .overrideDefaultInitialization(true)
}
