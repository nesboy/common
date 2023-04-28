package dev.tcheng.common.scope.manager

import dev.tcheng.common.model.exception.InternalException
import dev.tcheng.common.scope.model.Context
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ContextStorageManagerTest {

    @BeforeEach
    fun setUp() {
        ContextStorageManager.clear()
    }

    @Test
    fun `WHEN context is pushed THEN same context should be returned on peek`() {
        // prepare
        val expectedContext = Context()

        // execute
        ContextStorageManager.push(expectedContext)

        // verify
        assertEquals(expected = expectedContext, actual = ContextStorageManager.peek())
    }

    @Test
    fun `WHEN no context is pushed THEN InternalException should be thrown on pop`() {
        // execute and verify
        assertThrows<InternalException> { ContextStorageManager.pop() }
    }

    @Test
    fun `WHEN no context is pushed THEN null should be returned on peekOrNull`() {
        // execute and verify
        assertNull(ContextStorageManager.peekOrNull())
    }

    @Test
    fun `WHEN contexts are cleared THEN null should be returned on peekOrNull`() {
        // prepare
        ContextStorageManager.push(Context())
        ContextStorageManager.push(Context())

        // execute
        ContextStorageManager.clear()

        // verify
        assertNull(ContextStorageManager.peekOrNull())
    }
}
