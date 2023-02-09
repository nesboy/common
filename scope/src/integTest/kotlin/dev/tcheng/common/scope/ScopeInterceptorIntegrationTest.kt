package dev.tcheng.common.scope

import dev.tcheng.common.model.exception.InternalException
import dev.tcheng.common.scope.manager.ContextStorageManager
import dev.tcheng.common.scope.manager.MetadataManager
import dev.tcheng.common.scope.manager.MetricManager
import dev.tcheng.common.scope.manager.ObjectManager
import org.apache.logging.log4j.ThreadContext
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import systems.uom.unicode.CLDR
import tech.units.indriya.AbstractUnit
import tech.units.indriya.unit.Units
import java.time.DayOfWeek
import javax.measure.MetricPrefix

class ScopeInterceptorIntegrationTest {

    @BeforeEach
    fun setUp() {
        ThreadContext.clearAll()
    }

    @Nested
    inner class SingleScope {

        @Test
        fun `WHEN within Scope THEN Context should be available`() {
            // prepare
            val interceptor = ScopeInterceptor()

            // execute
            interceptor.intercept {
                // verify
                val actualContext = ContextStorageManager.peek()
                assertAll(
                    { assertTrue(actualContext.metrics.isEmpty()) },
                    { assertTrue(actualContext.objects.isEmpty()) },
                    { assertTrue(actualContext.metadata.isEmpty()) },
                    { assertTrue(actualContext.loggerMetadataKeys.isEmpty()) },
                    { assertNotNull(actualContext.startTimestamp) },
                    { assertNull(actualContext.endTimestamp) }
                )
            }
        }

        @Test
        fun `WHEN outside Scope THEN Context should not exist`() {
            // prepare
            val interceptor = ScopeInterceptor()

            // execute
            interceptor.intercept {
                // no-op
            }

            // verify
            assertThrows(InternalException::class.java) { ContextStorageManager.peek() }
        }
    }

    @Nested
    inner class SingleScopeMetadata {

        @Test
        fun `WHEN adding distinct metadata within Scope THEN metadata should be present`() {
            // prepare
            val interceptor = ScopeInterceptor()

            interceptor.intercept {
                // execute
                MetadataManager.apply {
                    addMetadata(key = "TestKey1", value = "TestValue1")
                    addMetadata(key = "TestKey2", value = "TestValue2")
                }

                // verify
                val actualMetadata = MetadataManager.getAllMetadata()
                val expectedMetadata = mapOf(
                    "TestKey1" to "TestValue1",
                    "TestKey2" to "TestValue2"
                )

                assertAll(
                    { assertEquals(expectedMetadata, actualMetadata) },
                    { assertEquals(expectedMetadata, ThreadContext.getContext()) }
                )
            }
        }

        @Test
        fun `WHEN adding metadata within Scope THEN metadata should be absent outside Scope`() {
            // prepare
            val interceptor = ScopeInterceptor()

            interceptor.intercept {
                // execute
                MetadataManager.addMetadata(key = "TestKey", value = "TestValue")
            }

            // verify
            assertFalse(ThreadContext.getContext().containsKey("TestKey"))
        }

        @Test
        fun `WHEN adding metadata with same value within Scope THEN operation should continue`() {
            // prepare
            val interceptor = ScopeInterceptor()

            interceptor.intercept {
                // execute
                MetadataManager.apply {
                    addMetadata(key = "TestKey", value = "TestValue")

                    // verify
                    assertAll(
                        { assertDoesNotThrow { addMetadata(key = "TestKey", value = "TestValue") } },
                        { assertEquals("TestValue", getMetadata("TestKey")) },
                        { assertEquals("TestValue", ThreadContext.get("TestKey")) }
                    )
                }
            }
        }

        @Test
        fun `WHEN adding metadata with different values within Scope THEN InternalException should be thrown`() {
            // prepare
            val interceptor = ScopeInterceptor()

            interceptor.intercept {
                // execute
                MetadataManager.apply {
                    addMetadata(key = "TestKey", value = "TestValue1")

                    // verify
                    assertThrows(InternalException::class.java) { addMetadata(key = "TestKey", value = "TestValue2") }
                }
            }
        }
    }

    @Nested
    inner class SingleScopeMetrics {

        @Test
        fun `WHEN adding distinct metrics within Scope THEN metrics should be present`() {
            // prepare
            val interceptor = ScopeInterceptor()

            interceptor.intercept {
                // execute
                MetricManager.apply {
                    addMetric(key = "TestMetric", value = 2.0, unit = MetricPrefix.GIGA(CLDR.BYTE))
                    addCountMetric(key = "TestCountMetric", value = 3.0)
                    addTimedMetric(key = "TestTimeMetric", unit = Units.SECOND) {
                        // no-op
                    }
                }

                // verify
                val actualMetrics = MetricManager.getAllMetrics()
                assertEquals(3, actualMetrics.size)

                val testMetric = actualMetrics["TestMetric"]
                assertNotNull(testMetric)
                assertAll(
                    { assertEquals(MetricPrefix.GIGA(CLDR.BYTE), testMetric!!.unit) },
                    { assertEquals(1, testMetric!!.datapoints.size) },
                    { assertEquals(2.0, testMetric!!.datapoints[0].value) },
                    { assertNotNull(testMetric!!.datapoints[0].timestamp) }
                )

                val testCountMetric = actualMetrics["TestCountMetric"]
                assertNotNull(testCountMetric)
                assertAll(
                    { assertEquals(AbstractUnit.ONE, testCountMetric!!.unit) },
                    { assertEquals(1, testCountMetric!!.datapoints.size) },
                    { assertEquals(3.0, testCountMetric!!.datapoints[0].value) },
                    { assertNotNull(testCountMetric!!.datapoints[0].timestamp) }
                )

                val testTimeMetric = actualMetrics["TestTimeMetric"]
                assertNotNull(testTimeMetric)
                assertAll(
                    { assertEquals(Units.SECOND, testTimeMetric!!.unit) },
                    { assertEquals(1, testTimeMetric!!.datapoints.size) },
                    { assertNotNull(testTimeMetric!!.datapoints[0].value) },
                    { assertNotNull(testTimeMetric!!.datapoints[0].timestamp) }
                )
            }
        }

        @Test
        fun `WHEN adding metrics within same name within Scope THEN metrics should be present`() {
            // prepare
            val interceptor = ScopeInterceptor()

            interceptor.intercept {
                // execute
                MetricManager.apply {
                    addCountMetric(key = "TestMetric", value = 8.0)
                    addCountMetric(key = "TestMetric", value = 3.0)
                }

                // verify
                val actualMetrics = MetricManager.getAllMetrics()
                assertEquals(1, actualMetrics.size)

                val testMetric = actualMetrics["TestMetric"]
                assertNotNull(testMetric)
                assertAll(
                    { assertEquals(AbstractUnit.ONE, testMetric!!.unit) },
                    { assertEquals(2, testMetric!!.datapoints.size) },
                    { assertEquals(8.0, testMetric!!.datapoints[0].value) },
                    { assertNotNull(testMetric!!.datapoints[0].timestamp) },
                    { assertEquals(3.0, testMetric!!.datapoints[1].value) },
                    { assertNotNull(testMetric!!.datapoints[1].timestamp) }
                )
            }
        }
    }

    @Nested
    inner class SingleScopeObjects {

        @Test
        fun `WHEN adding distinct objects within Scope THEN objects should be present`() {
            // prepare
            val interceptor = ScopeInterceptor()

            interceptor.intercept {
                // execute
                ObjectManager.apply {
                    addObject(key = "TestObject1", instance = DayOfWeek.MONDAY)
                    addObject(key = "TestObject2", instance = DayOfWeek.TUESDAY)
                }

                // verify
                val actualObjects = ObjectManager.getAllObjects()
                val expectedObjects: Map<String, Any> = mapOf(
                    "TestObject1" to DayOfWeek.MONDAY,
                    "TestObject2" to DayOfWeek.TUESDAY
                )
                assertEquals(expectedObjects, actualObjects)
            }
        }

        @Test
        fun `WHEN adding object with same value within Scope THEN operation should continue`() {
            // prepare
            val interceptor = ScopeInterceptor()

            interceptor.intercept {
                // execute
                ObjectManager.apply {
                    addObject(key = "TestObject", instance = DayOfWeek.MONDAY)

                    // verify
                    assertAll(
                        { assertDoesNotThrow { addObject(key = "TestObject", instance = DayOfWeek.MONDAY) } },
                        { assertEquals(DayOfWeek.MONDAY, getObject("TestObject")) }
                    )
                }
            }
        }

        @Test
        fun `WHEN adding object with different value within Scope THEN InternalException should be thrown`() {
            // prepare
            val interceptor = ScopeInterceptor()

            interceptor.intercept {
                // execute
                ObjectManager.apply {
                    addObject(key = "TestObject", instance = DayOfWeek.MONDAY)

                    // verify
                    assertThrows(InternalException::class.java) {
                        addObject(key = "TestObject", instance = DayOfWeek.TUESDAY)
                    }
                }
            }
        }
    }
}