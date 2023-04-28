package dev.tcheng.common.scope.manager

import dev.tcheng.common.easyRandom.nextString
import dev.tcheng.common.model.exception.InternalException
import dev.tcheng.common.scope.model.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ObjectManagerTest {
    private lateinit var random: EasyRandom

    @BeforeEach
    fun setUp() {
        random = EasyRandom()
    }

    @Nested
    inner class AddObject {

        @BeforeEach
        fun setUp() {
            mockkObject(ContextStorageManager)
        }

        @Test
        fun `WHEN key is absent THEN add it context`() {
            // prepare
            val key = random.nextString()
            val expectedInstance = random.nextString()

            val context = Context()
            every { ContextStorageManager.peek() } returns context

            // execute
            ObjectManager.addObject(key, expectedInstance)

            // verify
            assertEquals(expected = expectedInstance, actual = context.objects[key])
        }

        @Test
        fun `WHEN key is present but object is different THEN throw InternalException`() {
            // prepare
            val key = random.nextString()
            val instance = random.nextInt()

            val context = Context(objects = mutableMapOf(key to random.nextDouble()))
            every { ContextStorageManager.peek() } returns context

            // execute and verify
            assertThrows<InternalException> { ObjectManager.addObject(key, instance) }
        }

        @Test
        fun `WHEN key is present and object is same THEN no-op`() {
            // prepare
            val key = random.nextString()
            val instance = random.nextInt()

            val context = Context(objects = mutableMapOf(key to instance))
            every { ContextStorageManager.peek() } returns context

            // execute
            assertDoesNotThrow { ObjectManager.addObject(key, instance) }
        }
    }

    @Nested
    inner class GetObjectOrNull {

        @BeforeEach
        fun setUp() {
            mockkObject(ContextStorageManager)
        }

        @Test
        fun `WHEN key is present and type matches THEN return object`() {
            // prepare
            val key = random.nextString()
            val expectedInstance = random.nextDouble()

            val context = Context(objects = mutableMapOf(key to expectedInstance))
            every { ContextStorageManager.peek() } returns context

            // execute
            val actualInstance = ObjectManager.getObjectOrNull<Double>(key)

            // verify
            assertEquals(expected = expectedInstance, actual = actualInstance)
        }

        @Test
        fun `WHEN key is absent THEN return null`() {
            // prepare
            val key = random.nextString()

            val context = Context()
            every { ContextStorageManager.peek() } returns context

            // execute
            val instance = ObjectManager.getObjectOrNull<Double>(key)

            // verify
            assertNull(instance)
        }

        @Test
        fun `WHEN key is present but type mismatches THEN throw InternalException`() {
            // prepare
            val key = random.nextString()
            val instance = random.nextDouble()

            val context = Context(objects = mutableMapOf(key to instance))
            every { ContextStorageManager.peek() } returns context

            // execute adn verify
            assertThrows<InternalException> { ObjectManager.getObjectOrNull<Int>(key) }
        }
    }

    @Nested
    inner class GetObject {

        @Test
        fun `WHEN key is present and type matches THEN return object`() {
            // prepare
            val key = random.nextString()
            val instanceType = String::class.java

            val underTest = mockk<ObjectManager>()
            val expectedInstance = random.nextString()
            every { underTest.getObject<String>(key) } answers { callOriginal() }
            every { underTest.getObject(key, instanceType) } answers { callOriginal() }
            every { underTest.getObjectOrNull(key, instanceType) } returns expectedInstance

            // execute
            val actualInstance = underTest.getObject<String>(key)

            // verify
            assertEquals(expected = expectedInstance, actual = actualInstance)
        }

        @Test
        fun `WHEN key is absent THEN throw InternalException`() {
            // prepare
            val key = random.nextString()
            val instanceType = String::class.java

            val underTest = mockk<ObjectManager>()
            every { underTest.getObject<String>(key) } answers { callOriginal() }
            every { underTest.getObject(key, instanceType) } answers { callOriginal() }
            every { underTest.getObjectOrNull(key, instanceType) } returns null

            // execute and verify
            assertThrows<InternalException> { underTest.getObject<String>(key) }
        }
    }

    @Nested
    inner class GetAllObjects {

        @Test
        fun `WHEN called THEN return all objects`() {
            // prepare
            val expectedInstances = mutableMapOf<String, Any>(
                random.nextString() to random.nextInt(),
                random.nextString() to random.nextString()
            )
            val context = Context(objects = expectedInstances)

            mockkObject(ContextStorageManager)
            every { ContextStorageManager.peek() } returns context

            // execute
            val actualInstances = ObjectManager.getAllObjects()

            // verify
            assertEquals(expected = expectedInstances, actual = actualInstances)
        }
    }
}
