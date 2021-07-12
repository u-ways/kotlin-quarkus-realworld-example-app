package io.realworld.domain.tag

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity
@JsonRootName("tag")
@RegisterForReflection
data class Tag(
    @Id
    /**
     * In Kotlin, you will need to add the appropriate Annotation use-site targets.
     * See: https://kotlinlang.org/docs/annotations.html#annotation-use-site-targets
     */
    @field:NotNull
    @field:JsonProperty("name")
    var name: String = "",
) {
    override fun toString(): String = "Tag($name)"
}
