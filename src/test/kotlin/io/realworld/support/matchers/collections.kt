package io.realworld.support.matchers

internal fun <T> List<T>.containsInAnyOrder(elements: List<T>): Boolean =
    elements.size == this.size && elements.distinct().all { this.contains(it) }
