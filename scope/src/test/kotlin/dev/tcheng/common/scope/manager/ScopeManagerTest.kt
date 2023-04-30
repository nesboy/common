package dev.tcheng.common.scope.manager

import dev.tcheng.common.easyRandom.nextObject
import dev.tcheng.common.scope.model.Context
import dev.tcheng.common.scope.random.EasyRandomParameterFactory
import io.mockk.Called
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import tech.units.indriya.AbstractUnit
import java.time.Instant
import javax.measure.Unit
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class ScopeManagerTest {
    private lateinit var random: EasyRandom

    @BeforeEach
    fun setUp() {
        val randomParameters = EasyRandomParameterFactory.createDefault()
            .randomize(Unit::class.java) { AbstractUnit.ONE }
        random = EasyRandom(randomParameters)
        mockkObject(ContextStorageManager, MetadataManager)

        mockkStatic(Instant::class)
        every { Instant.now() } returns DEFAULT_TIMESTAMP
    }

    @Nested
    inner class StartScope {

        @Test
        fun `WHEN previous Context is absent THEN only new Context should be created`() {
            // prepare
            every { ContextStorageManager.peekOrNull() } returns null

            val context = Context(startTimestamp = DEFAULT_TIMESTAMP)
            justRun { ContextStorageManager.push(context) }

            // execute
            ScopeManager.startScope()

            // verify
            verify {
                ContextStorageManager.push(context)
                MetadataManager wasNot Called
            }
        }

        @Test
        fun `WHEN previous Context has metadata THEN they should be added to new Context`() {
            // prepare
            val previousContext = random.nextObject<Context>()
            every { ContextStorageManager.peekOrNull() } returns previousContext

            val context = Context(startTimestamp = DEFAULT_TIMESTAMP)
            justRun { ContextStorageManager.push(context) }

            justRun { MetadataManager.addAllMetadata(previousContext.metadata) }

            // execute
            ScopeManager.startScope()

            // verify
            verify {
                ContextStorageManager.push(context)
                MetadataManager.addAllMetadata(previousContext.metadata)
            }
        }
    }

    @Nested
    inner class EndScope {

        @Test
        fun `WHEN called THEN tear down current scope`() {
            // prepare
            val context = random.nextObject<Context>()
            every { ContextStorageManager.pop() } returns context

            justRun { MetadataManager.removeMetadataFromLogger(metadataKeys = context.loggerMetadataKeys) }

            // execute
            val actualContext = ScopeManager.endScope()

            // verify
            val expectedContext = context.copy(endTimestamp = DEFAULT_TIMESTAMP)
            assertEquals(expected = expectedContext, actual = actualContext)

            verify {
                MetadataManager.removeMetadataFromLogger(metadataKeys = context.loggerMetadataKeys)
            }
        }
    }

    private companion object {
        val DEFAULT_TIMESTAMP: Instant = Instant.now().plusSeconds(60)
    }
}
