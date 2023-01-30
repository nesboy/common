package dev.tcheng.common.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class EchoTask : DefaultTask() {
    @get:Input
    abstract val value: Property<String>

    @TaskAction
    fun greet() {
        println(value.get())
    }
}
