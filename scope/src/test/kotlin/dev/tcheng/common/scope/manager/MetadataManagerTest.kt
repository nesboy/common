package dev.tcheng.common.scope.manager

import dev.tcheng.common.easyRandom.nextString
import dev.tcheng.common.model.exception.InternalException
import dev.tcheng.common.scope.model.Context
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import io.mockk.verifyAll
import org.apache.logging.log4j.ThreadContext
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MetadataManagerTest {
    private lateinit var random: EasyRandom

    @BeforeEach
    fun setUp() {
        random = EasyRandom()
    }

    @Nested
    inner class AddMetadata {

        @BeforeEach
        fun setUp() {
            mockkObject(ContextStorageManager)
            mockkStatic(ThreadContext::class)
        }

        @Test
        fun `WHEN metadata is new THEN add to scope and logger metadata`() {
            // prepare
            val key = random.nextString()
            val value = random.nextString()

            val context = Context()
            every { ContextStorageManager.peek() } returns context

            every { ThreadContext.containsKey(key) } returns false
            justRun { ThreadContext.put(key, value) }

            // execute
            MetadataManager.addMetadata(key, value)

            // verify
            assertAll(
                { assertEquals(expected = value, actual = context.metadata[key]) },
                { assertTrue(context.loggerMetadataKeys.contains(key)) }
            )

            verify { ThreadContext.put(key, value) }
        }

        @Test
        fun `WHEN key exists in scope metadata with different value THEN throw InternalException`() {
            // prepare
            val key = random.nextString()
            val value = random.nextString()

            val context = Context(metadata = mutableMapOf(key to random.nextString()))
            every { ContextStorageManager.peek() } returns context

            // execute and verify
            assertThrows<InternalException> { MetadataManager.addMetadata(key, value) }
        }

        @Test
        fun `WHEN key exists in scope metadata and value is same THEN metadata should be added to logger context`() {
            // prepare
            val key = random.nextString()
            val value = random.nextString()

            val context = Context(metadata = mutableMapOf(key to value))
            every { ContextStorageManager.peek() } returns context

            every { ThreadContext.containsKey(key) } returns false
            justRun { ThreadContext.put(key, value) }

            // execute
            MetadataManager.addMetadata(key, value)

            // verify
            assertTrue(context.loggerMetadataKeys.contains(key))

            verify { ThreadContext.put(key, value) }
        }

        @Test
        fun `WHEN key exists in scope and logger metadata with different value THEN throw InternalException`() {
            // prepare
            val key = random.nextString()
            val value = random.nextString()

            val context = Context()
            every { ContextStorageManager.peek() } returns context

            every { ThreadContext.containsKey(key) } returns true
            every { ThreadContext.get(key) } returns random.nextString()

            // execute and verify
            assertThrows<InternalException> { MetadataManager.addMetadata(key, value) }
        }

        @Test
        fun `WHEN key exists in scope and logger metadata THEN no-op`() {
            // prepare
            val key = random.nextString()
            val value = random.nextString()

            val context = Context()
            every { ContextStorageManager.peek() } returns context

            every { ThreadContext.containsKey(key) } returns true
            every { ThreadContext.get(key) } returns value

            // execute and verify
            verify(exactly = 0) { ThreadContext.put(key, value) }
        }
    }

    @Nested
    inner class AddAllMetadata {

        @Test
        fun `WHEN metadata is new THEN add to scope and logger metadata`() {
            // prepare
            val key1 = random.nextString()
            val value1 = random.nextString()

            val key2 = random.nextString()
            val value2 = random.nextString()

            val entries = mapOf(key1 to value1, key2 to value2)

            val underTest = mockk<MetadataManager>()

            every { underTest.addAllMetadata(entries) } answers { callOriginal() }

            justRun {
                underTest.addMetadata(key1, value1)
                underTest.addMetadata(key2, value2)
            }

            // execute
            underTest.addAllMetadata(entries)

            // verify
            verifyAll {
                underTest.addAllMetadata(entries)
                underTest.addMetadata(key1, value1)
                underTest.addMetadata(key2, value2)
            }
        }
    }

    @Nested
    inner class GetMetadataOrNull {

        @BeforeEach
        fun setUp() {
            mockkObject(ContextStorageManager)
        }

        @Test
        fun `WHEN key is present THEN return value`() {
            // prepare
            val key = random.nextString()
            val expectedValue = random.nextString()

            val context = Context(metadata = mutableMapOf(key to expectedValue))

            every { ContextStorageManager.peek() } returns context

            // execute
            val actualValue = MetadataManager.getMetadataOrNull(key)

            // verify
            assertEquals(expected = expectedValue, actual = actualValue)
        }

        @Test
        fun `WHEN key is absent THEN return null`() {
            // prepare
            val key = random.nextString()
            val context = Context(metadata = mutableMapOf())

            every { ContextStorageManager.peek() } returns context

            // execute
            val actualValue = MetadataManager.getMetadataOrNull(key)

            // verify
            assertNull(actualValue)
        }
    }

    @Nested
    inner class GetMetadata {

        @Test
        fun `WHEN key is present THEN return value`() {
            // prepare
            val key = random.nextString()
            val expectedValue = random.nextString()

            val underTest = mockk<MetadataManager>()

            every { underTest.getMetadata(key) } answers { callOriginal() }
            every { underTest.getMetadataOrNull(key) } returns expectedValue

            // execute
            val actualValue = underTest.getMetadata(key)

            // verify
            assertEquals(expected = expectedValue, actual = actualValue)

            verifyAll {
                underTest.getMetadataOrNull(key)
                underTest.getMetadata(key)
            }
        }

        @Test
        fun `WHEN key is absent THEN throw InternalException`() {
            // prepare
            val key = random.nextString()

            val underTest = mockk<MetadataManager>()

            every { underTest.getMetadata(key) } answers { callOriginal() }
            every { underTest.getMetadataOrNull(key) } returns null

            // execute and verify
            assertThrows<InternalException> { underTest.getMetadata(key) }
        }
    }

    @Nested
    inner class GetAllMetadata {

        @Test
        fun `WHEN called THEN return all metadata`() {
            // prepare
            val expectedMetadata = mutableMapOf(random.nextString() to random.nextString())
            val context = Context(metadata = expectedMetadata)

            mockkObject(ContextStorageManager)
            every { ContextStorageManager.peek() } returns context

            // execute
            val actualMetadata = MetadataManager.getAllMetadata()

            // verify
            assertEquals(expected = expectedMetadata, actual = actualMetadata)
        }
    }

    @Nested
    inner class RemoveMetadataFromLogger {

        @Test
        fun `WHEN called THEN remove metadata from ThreadContext`() {
            // prepare
            val keys = random.objects(String::class.java, 5).toList().toSet()

            mockkStatic(ThreadContext::class)
            justRun { ThreadContext.removeAll(keys) }

            // execute
            MetadataManager.removeMetadataFromLogger(keys)

            // verify
            verify { ThreadContext.removeAll(keys) }
        }
    }
}
