package io.realworld.domain.profile

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import org.junit.jupiter.api.Test

internal class FollowRelationshipTest {
    @Test
    fun `verify the equals and hashCode contract`() {
        EqualsVerifier
            .configure().suppress(Warning.SURROGATE_KEY)
            .forClass(FollowRelationship::class.java)
            .verify()
    }
}