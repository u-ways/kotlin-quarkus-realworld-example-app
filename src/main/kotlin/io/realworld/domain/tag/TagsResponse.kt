package io.realworld.domain.tag

import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.annotation.JsonValue
import io.quarkus.runtime.annotations.RegisterForReflection

@JsonRootName("tags")
@RegisterForReflection
data class TagsResponse(
    @JsonValue
    var tags: List<String> = emptyList(),
) {
    companion object {
        @JvmStatic
        fun build(tags: List<Tag>): TagsResponse =
            TagsResponse(tags.map { it.name })
    }

    override fun toString(): String = "TagsRes($tags)"
}
