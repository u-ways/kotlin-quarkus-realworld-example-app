package io.realworld.support.factory

import io.realworld.domain.tag.Tag
import java.util.UUID.randomUUID

class TagFactory {
    companion object {
        /**
         * Creates a random tag.
         */
        fun create(
            name: String = "TAG-${randomUUID()}".substring(0, 20),
        ): Tag = Tag(name)

        /**
         * Creates X amount of tags with random names
         */
        fun create(
            amount: Int,
        ): List<Tag> = (0 until amount).map { create() }
    }
}
