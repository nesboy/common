package dev.tcheng.common.scope.multithread

import dev.tcheng.common.easyRandom.nextObject
import dev.tcheng.common.scope.ScopeInterceptor
import dev.tcheng.common.scope.manager.MetadataManager
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.verify
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class ScopeAwareExecutorTest {
    @MockK
    private lateinit var mockDelegate: ExecutorService

    @MockK
    private lateinit var mockInterceptor: ScopeInterceptor

    private lateinit var underTest: ScopeAwareExecutorService
    private lateinit var random: EasyRandom

    @BeforeEach
    fun setUp() {
        underTest = ScopeAwareExecutorService(mockDelegate, mockInterceptor)
        random = EasyRandom()
        mockkObject(MetadataManager)
    }

    @Test
    fun `WHEN execute is called THEN delegate should execute ScopeAwareRunnable`() {
        // prepare
        val command = Runnable { }

        val parentMetadata = mutableMapOf("key" to "value")
        every { MetadataManager.getAllMetadata() } returns parentMetadata

        val runnableSlot = slot<ScopeAwareRunnable>()
        justRun { mockDelegate.execute(capture(runnableSlot)) }

        // execute
        underTest.execute(command)

        // verify
        runnableSlot.captured.let {
            assertAll(
                { assertEquals(expected = mockInterceptor, actual = it.scopeInterceptor) },
                { assertEquals(expected = parentMetadata, actual = it.parentMetadata) },
                { assertEquals(expected = command, actual = it.command) }
            )
        }
    }

    @Test
    fun `WHEN shutdown is called THEN delegate should execute shutdown`() {
        // prepare
        justRun { mockDelegate.shutdown() }

        // execute
        underTest.shutdown()

        // verify
        verify { mockDelegate.shutdown() }
    }

    @Test
    fun `WHEN shutdownNow is called THEN delegate should execute shutdownNow`() {
        // prepare
        val expectedPendingTasks = mutableListOf(Runnable {})
        every { mockDelegate.shutdownNow() } returns expectedPendingTasks

        // execute
        val actualPendingTasks = underTest.shutdownNow()

        // verify
        assertEquals(expected = expectedPendingTasks, actual = actualPendingTasks)
    }

    @Test
    fun `WHEN isShutdown is called THEN delegate should execute isShutdown`() {
        // prepare
        val expectedIsShutdown = random.nextBoolean()
        every { mockDelegate.isShutdown } returns expectedIsShutdown

        // execute
        val actualIsShutdown = underTest.isShutdown

        // verify
        assertEquals(expected = expectedIsShutdown, actual = actualIsShutdown)
    }

    @Test
    fun `WHEN isTerminated is called THEN delegate should execute isTerminated`() {
        // prepare
        val expectedIsTerminated = random.nextBoolean()
        every { mockDelegate.isTerminated } returns expectedIsTerminated

        // execute
        val actualIsTerminated = underTest.isTerminated

        // verify
        assertEquals(expected = expectedIsTerminated, actual = actualIsTerminated)
    }

    @Test
    fun `WHEN awaitTermination is called THEN delegate should execute awaitTermination`() {
        // prepare
        val timeout = random.nextLong()
        val unit = random.nextObject<TimeUnit>()

        val expectedIsTerminated = random.nextBoolean()
        every { mockDelegate.awaitTermination(timeout, unit) } returns expectedIsTerminated

        // execute
        val actualIsTerminated = underTest.awaitTermination(timeout, unit)

        // verify
        assertEquals(expected = expectedIsTerminated, actual = actualIsTerminated)
    }
}
