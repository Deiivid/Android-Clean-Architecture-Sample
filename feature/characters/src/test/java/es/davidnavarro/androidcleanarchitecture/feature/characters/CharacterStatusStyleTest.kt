package es.davidnavarro.androidcleanarchitecture.feature.characters

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class CharacterStatusStyleTest {
    @Test
    fun `maps backend statuses to distinct biometric treatments`() {
        val alive = characterStatusStyle("Alive")
        val dead = characterStatusStyle("Dead")
        val unknown = characterStatusStyle("unknown")

        assertEquals(CharacterStatusKind.ALIVE, alive.kind)
        assertEquals(CharacterStatusKind.DEAD, dead.kind)
        assertEquals(CharacterStatusKind.UNKNOWN, unknown.kind)
        assertNotEquals(alive.color, dead.color)
        assertNotEquals(dead.color, unknown.color)
        assertNotEquals(unknown.color, alive.color)
    }

    @Test
    fun `maps unexpected status to unknown treatment`() {
        assertEquals(CharacterStatusKind.UNKNOWN, characterStatusStyle("missing").kind)
    }
}
