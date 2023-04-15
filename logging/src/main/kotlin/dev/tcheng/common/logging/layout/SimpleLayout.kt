package dev.tcheng.common.logging.layout

import dev.tcheng.common.logging.model.SimpleLayoutMode
import org.apache.logging.log4j.core.Layout
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.config.Node
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.config.plugins.PluginAttribute
import org.apache.logging.log4j.core.config.plugins.PluginFactory
import org.apache.logging.log4j.core.layout.AbstractStringLayout
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.Instant as JavaInstant
import org.apache.logging.log4j.core.time.Instant as Log4jInstant

@Plugin(
    name = "SimpleLayout",
    category = Node.CATEGORY,
    elementType = Layout.ELEMENT_TYPE,
    printObject = false
)
class SimpleLayout(private val mode: SimpleLayoutMode) : AbstractStringLayout(Charsets.UTF_8) {

    override fun toSerializable(event: LogEvent) =
        when (mode) {
            SimpleLayoutMode.JSON -> this.createJsonEvent(event)
            else -> this.createHumanReadableEvent(event)
        }

    private fun createHumanReadableEvent(event: LogEvent): String {
        val baseData = listOfNotNull(
            convertInstantToLocalDateTime(event.instant),
            "[${event.level}]",
            event.contextData.getValue("RequestId") ?: null,
            "(${event.threadName})",
            "${event.loggerName}:",
            event.message.formattedMessage
        ).joinToString(separator = " ")

        return listOfNotNull(
            baseData,
            event.thrown?.stackTraceToString()?.trim()
        ).joinToString(separator = "\n", postfix = "\n")
    }

    private fun createJsonEvent(event: LogEvent) = buildString {
        val logData = mutableMapOf<String, Any>(
            "timestamp" to convertInstantToLocalDateTime(event.instant),
            "thread" to event.threadName,
            "level" to event.level,
            "class" to event.loggerName,
            "message" to event.message.formattedMessage
        )

        event.thrown?.let {
            logData["error"] = createThrowableMap(event.thrown)
            it.cause?.let { cause -> logData["errorCause"] = createThrowableMap(cause) }
        }

        logData["contextMap"] = event.contextData.toMap()
        append(createJsonPayload(logData))
        append("\n")
    }

    private fun createJsonPayload(list: List<Map<String, Any>>) =
        buildString {
            append(list.map { createJsonPayload(it) })
        }

    private fun createJsonPayload(map: Map<String, Any>) =
        buildString {
            append("{")
            append(map.entries.joinToString(separator = ",") { createEntry(key = it.key, value = it.value) })
            append("}")
        }

    @Suppress("UNCHECKED_CAST")
    private fun createEntry(key: String, value: Any): String {
        val formattedValue = when (value) {
            is List<*> -> createJsonPayload(value as List<Map<String, Any>>)
            is Map<*, *> -> createJsonPayload(value as Map<String, Any>)
            else -> "\"$value\""
        }
        return "\"$key\":$formattedValue"
    }

    private fun createThrowableMap(throwable: Throwable) = mapOf(
        "name" to throwable.javaClass.name,
        "message" to throwable.message,
        "stackTrace" to throwable.stackTrace.map { createStackTraceElementMap(it) }
    )

    private fun createStackTraceElementMap(element: StackTraceElement) = mapOf(
        "className" to element.className,
        "methodName" to element.methodName,
        "fileName" to element.fileName,
        "lineNumber" to element.lineNumber
    )

    private fun convertInstantToLocalDateTime(instant: Log4jInstant) = LocalDateTime.ofInstant(
        JavaInstant.ofEpochMilli(instant.epochMillisecond),
        ZoneOffset.UTC
    )

    companion object {
        @JvmStatic
        @PluginFactory
        fun createLayout(@PluginAttribute("mode") mode: String?): SimpleLayout =
            SimpleLayout(SimpleLayoutMode.valueOf(mode ?: "JSON"))
    }
}
