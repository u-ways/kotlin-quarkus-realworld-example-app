package io.realworld.domain.tag

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort.ascending
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TagRepository : PanacheRepositoryBase<Tag, String> {
    fun listAllNames(): List<String> = findAll(ascending()).list().map { it.name }
}
