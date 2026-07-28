package es.davidnavarro.androidcleanarchitecture.feature.episodes

import kotlin.test.Test
import kotlin.test.assertEquals

class EpisodeFormattingTest {
    @Test
    fun `air date from backend uses the device locale`() {
        assertEquals(
            "2 de diciembre de 2013",
            formatAirDate("December 2, 2013", "es-ES")
        )
    }

    @Test
    fun `unknown date is preserved`() {
        assertEquals("unknown", formatAirDate("unknown", "es-ES"))
    }
}
