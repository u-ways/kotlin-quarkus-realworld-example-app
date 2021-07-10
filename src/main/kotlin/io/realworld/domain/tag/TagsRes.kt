package io.realworld.domain.tag

import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.annotation.JsonValue
import io.quarkus.runtime.annotations.RegisterForReflection

@JsonRootName("tags")
@RegisterForReflection
data class TagsRes(
    @JsonValue
    var tags: List<String> = emptyList(),
) {
    override fun toString(): String = "TagsRes($tags)"
}
