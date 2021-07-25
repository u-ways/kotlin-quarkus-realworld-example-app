package io.realworld.domain.user

import io.realworld.support.factory.UserFactory
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import org.junit.jupiter.api.Test

internal class UserTest {
    @Test
    fun `Verify the equals and hashCode contract`() {
        EqualsVerifier
            .configure().suppress(Warning.SURROGATE_KEY)
            .forClass(User::class.java)
            .withPrefabValues(User::class.java, UserFactory.create(), UserFactory.create())
            .verify()
    }
}
