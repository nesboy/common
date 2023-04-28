package dev.tcheng.common.scope.manager

import dev.tcheng.common.easyRandom.nextObject
import dev.tcheng.common.easyRandom.nextString
import dev.tcheng.common.model.exception.InternalException
import dev.tcheng.common.scope.model.Context
import dev.tcheng.common.scope.model.Metric
import dev.tcheng.common.scope.model.MetricDatapoint
import dev.tcheng.common.scope.random.EasyRandomParameterFactory
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import systems.uom.unicode.CLDR
import tech.units.indriya.AbstractUnit
import tech.units.indriya.unit.Units
import java.time.Duration
import java.time.Instant
import javax.measure.MetricPrefix
import javax.measure.Unit
import javax.measure.quantity.Dimensionless
import javax.measure.quantity.Time
import kotlin.test.assertEquals

class MetricManagerTest {
    private lateinit var random: EasyRandom

    @BeforeEach
    fun setUp() {
        val randomParameters = EasyRandomParameterFactory.createDefault()
            .randomize(Unit::class.java) { DEFAULT_UNIT }
        random = EasyRandom(randomParameters)
    }

    @Nested
    inner class AddMetricDoubleValue {

        @BeforeEach
        fun setUp() {
            mockkObject(ContextStorageManager)

            mockkStatic(Instant::class)
            every { Instant.now() } returns DEFAULT_TIMESTAMP
        }

        @Test
        fun `WHEN key is absent THEN new entry should be added`() {
            // prepare
            val key = random.nextString()
            val value = random.nextDouble()

            val context = Context()
            every { ContextStorageManager.peek() } returns context

            // execute
            MetricManager.addMetric(key, value, DEFAULT_UNIT)

            // verify
            val expectedMetrics = mutableMapOf(
                key to Metric(
                    datapoints = mutableListOf(MetricDatapoint(value, DEFAULT_TIMESTAMP)),
                    unit = DEFAULT_UNIT
                )
            )
            assertEquals(expected = expectedMetrics, actual = context.metrics)
        }

        @Test
        fun `WHEN key exists but input unit is different THEN throw InternalException`() {
            // prepare
            val key = random.nextString()
            val value = random.nextDouble()

            val context = Context(
                metrics = mutableMapOf(
                    key to random.nextObject<Metric>()
                )
            )
            every { ContextStorageManager.peek() } returns context

            // execute and verify
            assertThrows<InternalException> { MetricManager.addMetric(key, value, unit = MetricPrefix.GIGA(CLDR.BYTE)) }
        }

        @Test
        fun `WHEN key exists and input unit matches THEN append value`() {
            // prepare
            val key = random.nextString()
            val value = random.nextDouble()

            val existingMetrics = mutableMapOf(
                key to random.nextObject<Metric>()
            )
            val context = Context(
                metrics = existingMetrics.toMutableMap() // create copy
            )
            every { ContextStorageManager.peek() } returns context

            // execute
            MetricManager.addMetric(key, value, DEFAULT_UNIT)

            // verify
            existingMetrics[key]!!.datapoints.add(MetricDatapoint(value, DEFAULT_TIMESTAMP))
            assertEquals(expected = existingMetrics, actual = context.metrics)
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class AddMetricBooleanValue {

        @ParameterizedTest(name = "[{index}] booleanValue={0}, doubleValue={1}")
        @MethodSource("booleanValueParameters")
        fun `WHEN adding metric THEN boolean value should be converted to equivalent double`(
            value: Boolean,
            doubleValue: Double
        ) {
            // prepare
            val key = random.nextString()

            val underTest = mockk<MetricManager>()
            every { underTest.addMetric(key, value, DEFAULT_UNIT) } answers { callOriginal() }
            justRun { underTest.addMetric(key, doubleValue, DEFAULT_UNIT) }

            // execute
            underTest.addMetric(key, value, DEFAULT_UNIT)

            // verify
            verify { underTest.addMetric(key, doubleValue, DEFAULT_UNIT) }
        }

        fun booleanValueParameters() = listOf(
            arguments(true, 1.0),
            arguments(false, 0.0)
        )
    }

    @Nested
    inner class AddCountMetric {

        @Test
        fun `WHEN called with value THEN add metric with value`() {
            // prepare
            val key = random.nextString()
            val value = random.nextDouble()

            val underTest = mockk<MetricManager>()
            every { underTest.addCountMetric(key, value) } answers { callOriginal() }
            justRun { underTest.addMetric(key, value, DEFAULT_UNIT) }

            // execute
            underTest.addCountMetric(key, value)

            // verify
            verify { underTest.addMetric(key, value, DEFAULT_UNIT) }
        }

        @Test
        fun `WHEN called without value THEN add metric with default value`() {
            // prepare
            val key = random.nextString()

            val underTest = mockk<MetricManager>()
            every { underTest.addCountMetric(key) } answers { callOriginal() }
            justRun { underTest.addMetric(key, 1.0, DEFAULT_UNIT) }

            // execute
            underTest.addCountMetric(key)

            // verify
            verify { underTest.addMetric(key, 1.0, DEFAULT_UNIT) }
        }
    }

    @Nested
    inner class AddTimedMetric {

        @BeforeEach
        fun setUp() {
            mockkStatic(Instant::class)
            val endTime = DEFAULT_TIMESTAMP.plus(TIMESTAMP_DIFFERENTIAL)
            every { Instant.now() } returns DEFAULT_TIMESTAMP andThen endTime
        }

        @Test
        fun `WHEN called with unit THEN add metric with unit`() {
            // prepare
            val key = random.nextString()

            val underTest = mockk<MetricManager>()
            every {
                underTest.addTimedMetric(
                    eq(key),
                    eq(DEFAULT_TIME_UNIT),
                    captureLambda<() -> Int>()
                )
            } answers {
                callOriginal()
                lambda<() -> Int>().captured.invoke()
            }
            justRun {
                underTest.addMetric(
                    key,
                    value = TIMESTAMP_DIFFERENTIAL.toSeconds().toDouble(),
                    DEFAULT_TIME_UNIT
                )
            }

            // execute
            val expectedResult = random.nextInt()
            val actualResult = underTest.addTimedMetric(key, DEFAULT_TIME_UNIT) { expectedResult }

            // verify
            assertEquals(expected = expectedResult, actual = actualResult)

            verify {
                underTest.addMetric(
                    key,
                    value = TIMESTAMP_DIFFERENTIAL.toSeconds().toDouble(),
                    DEFAULT_TIME_UNIT
                )
            }
        }

        @Test
        fun `WHEN called without unit THEN add metric with default unit`() {
            // prepare
            val key = random.nextString()

            val underTest = mockk<MetricManager>()
            every {
                underTest.addTimedMetric(
                    eq(key),
                    operation = captureLambda<() -> Int>()
                )
            } answers {
                callOriginal()
                lambda<() -> Int>().captured.invoke()
            }
            justRun {
                underTest.addMetric(
                    key,
                    value = TIMESTAMP_DIFFERENTIAL.toMillis().toDouble(),
                    unit = MetricPrefix.MILLI(Units.SECOND)
                )
            }

            // execute
            val expectedResult = random.nextInt()
            val actualResult = underTest.addTimedMetric(key) { expectedResult }

            // verify
            assertEquals(expected = expectedResult, actual = actualResult)

            verify {
                underTest.addMetric(
                    key,
                    value = TIMESTAMP_DIFFERENTIAL.toMillis().toDouble(),
                    unit = MetricPrefix.MILLI(Units.SECOND)
                )
            }
        }
    }

    @Nested
    inner class GetAllMetrics {

        @Test
        fun `WHEN called THEN return all recorded metrics`() {
            // prepare
            val context = random.nextObject<Context>()

            mockkObject(ContextStorageManager)
            every { ContextStorageManager.peek() } returns context

            // execute
            val actualMetrics = MetricManager.getAllMetrics()

            // verify
            assertEquals(expected = context.metrics, actual = actualMetrics)
        }
    }

    private companion object {
        val DEFAULT_UNIT: Unit<Dimensionless> = AbstractUnit.ONE
        val DEFAULT_TIME_UNIT: Unit<Time> = Units.SECOND
        val DEFAULT_TIMESTAMP: Instant = Instant.now()
        val TIMESTAMP_DIFFERENTIAL: Duration = Duration.ofSeconds(5L)
    }
}
