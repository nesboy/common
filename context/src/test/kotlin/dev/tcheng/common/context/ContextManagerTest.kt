package dev.tcheng.common.context

import dev.tcheng.common.model.exception.InternalException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.util.concurrent.Executors
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ContextManagerTest {

    @BeforeEach
    fun setUp() {
        ContextManager.clear()
    }

    @Nested
    inner class PutAndGet {
        @Test
        fun `WHEN correct type is provided THEN return expected value `() {
            // prepare
            val expectedValue = 1
            ContextManager.put(key = DayOfWeek.FRIDAY, value = expectedValue)

            // execute
            val actualValue = ContextManager.get<Int>(key = DayOfWeek.FRIDAY)

            // verify
            assertEquals(expected = expectedValue, actual = actualValue)
        }

        @Test
        fun `WHEN incorrect type is provided THEN throw InternalException value`() {
            // prepare
            ContextManager.put(key = DayOfWeek.FRIDAY, value = 1)

            // execute and verify
            assertFailsWith(exceptionClass = InternalException::class) {
                ContextManager.get<String>(DayOfWeek.FRIDAY)
            }
        }

        @Test
        fun `WHEN non-existent key is provided THEN return null`() {
            // execute and verify
            assertNull(actual = ContextManager.get<String>(DayOfWeek.FRIDAY))
        }
    }

    @Nested
    inner class PutAndGetRequired {
        @Test
        fun `WHEN correct type is provided THEN return expected value `() {
            // prepare
            val expectedValue = 1
            ContextManager.put(key = DayOfWeek.FRIDAY, value = expectedValue)

            // execute
            val actualValue = ContextManager.getRequired<Int>(key = DayOfWeek.FRIDAY)

            // verify
            assertEquals(expected = expectedValue, actual = actualValue)
        }

        @Test
        fun `WHEN incorrect type is provided THEN throw InternalException value`() {
            // prepare
            ContextManager.put(key = DayOfWeek.FRIDAY, value = 1)

            // execute and verify
            assertFailsWith(exceptionClass = InternalException::class) {
                ContextManager.getRequired<String>(DayOfWeek.FRIDAY)
            }
        }

        @Test
        fun `WHEN non-existent key is provided THEN throw InternalException`() {
            // execute and verify
            assertFailsWith(exceptionClass = InternalException::class) {
                ContextManager.getRequired<String>(DayOfWeek.FRIDAY)
            }
        }
    }

    @Nested
    inner class PutAllAndGetContext {
        @Test
        fun `WHEN multiple keys are put THEN all should be present in map`() {
            // prepare
            val expectedValue: Map<Enum<*>, Any> = mapOf(
                DayOfWeek.MONDAY to 1,
                DayOfWeek.TUESDAY to 2,
                DayOfWeek.WEDNESDAY to 3
            )
            ContextManager.putAll(contextEntries = expectedValue)

            // execute
            val actualValue = ContextManager.getContext()

            // verify
            assertEquals(expected = expectedValue, actual = actualValue)
        }
    }

    @Nested
    inner class MultiThread {

        @Test
        fun `WHEN getting key put in another thread THEN return null`() {
            // prepare
            val executorService = Executors.newFixedThreadPool(1)
            ContextManager.put(key = DayOfWeek.FRIDAY, value = 1)

            // execute
            executorService.submit {
                val actualValue = ContextManager.get<Int>(key = DayOfWeek.FRIDAY)

                // verify
                assertNull(actual = actualValue)
            }.get()
        }
    }
}
