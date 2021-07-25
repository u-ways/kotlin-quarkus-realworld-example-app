package io.realworld.domain.tag

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import org.junit.jupiter.api.Test

internal class TagTest {
    @Test
    fun `Verify the equals and hashCode contract`() {
        EqualsVerifier
            .configure().suppress(Warning.SURROGATE_KEY)
            .forClass(Tag::class.java)
            .verify()
    }
}
