package io.realworld.domain.tag

import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.infrastructure.database.Tables.TAG_TABLE
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.Size

@Entity(name = TAG_TABLE)
@RegisterForReflection
open class Tag(
    @Id
    @field:Size(min = 0, max = 31)
    open var name: String = "",
) {
    override fun toString(): String = "Tag($name)"

    final override fun hashCode(): Int = name.hashCode()

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Tag) return false
        if (name != other.name) return false
        return true
    }
}
