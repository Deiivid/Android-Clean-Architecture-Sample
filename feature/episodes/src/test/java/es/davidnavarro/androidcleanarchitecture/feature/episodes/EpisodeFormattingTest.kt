package es.davidnavarro.androidcleanarchitecture.feature.episodes

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class EpisodeFormattingTest {
    @Test
    fun `air date from backend uses the device locale`() {
        assertEquals(
            "2 de diciembre de 2013",
            formatAirDate("December 2, 2013", Locale.forLanguageTag("es-ES"))
        )
    }

    @Test
    fun `unknown date is preserved`() {
        assertEquals("unknown", formatAirDate("unknown", Locale.forLanguageTag("es-ES")))
    }
}
