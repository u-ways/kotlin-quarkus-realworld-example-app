package io.realworld.domain.tag

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TagRepository : PanacheRepositoryBase<Tag, String>
