package io.realworld.domain.tag

import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.infrastructure.database.Tables.TAG_TABLE
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity(name = TAG_TABLE)
@RegisterForReflection
data class Tag(
    @Id
    @field:NotNull
    @field:Size(min = 0, max = 31)
    var name: String = "",
) {
    override fun toString(): String = "Tag($name)"
}
