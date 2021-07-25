package io.realworld.utils

/**
 * Simple HQL SELECT Query Builder.
 * The SELECT BNF in HQL:
 * ```
 * select_statement :: =
 *   [select   clause]
 *    from     clause
 *   [where    clause]
 *   [group by clause]
 *   [having   clause]
 *   [order by clause]
 * ```
 * See: https://docs.jboss.org/hibernate/orm/5.5/userguide/html_single/Hibernate_User_Guide.html#hql-select
 */
internal class QueryBuilder(
    private val selectStatements: MutableList<SELECT> = mutableListOf(),
    private val joinStatements: MutableList<JOIN> = mutableListOf(),
    private val whereStatements: MutableList<WHERE> = mutableListOf(),
    private val andStatements: MutableList<AND> = mutableListOf(),
) {
    internal fun add(clause: Clause): QueryBuilder = this.apply {
        when (clause) {
            is SELECT -> selectStatements.add(clause)
            is JOIN -> joinStatements.add(clause)
            is WHERE -> {
                if (whereStatements.size > 0) andStatements.add(AND(clause.value))
                else whereStatements.add(clause)
            }
            is AND -> andStatements.add(clause)
        }
    }

    internal fun add(vararg clauses: Clause): QueryBuilder = this.apply {
        clauses.forEach(::add)
    }

    internal fun addIf(predicate: Boolean, vararg clauses: Clause): QueryBuilder = this.apply {
        if (predicate) clauses.forEach(::add)
    }

    internal fun addIf(predicate: Boolean, vararg clauses: Clause, runnable: () -> Unit): QueryBuilder = this.apply {
        if (predicate) clauses.forEach(::add).apply { runnable() }
    }

    internal fun build(): String = StringBuilder().run {
        check(selectStatements.isNotEmpty()) { "Query builder expects at least one SELECT query (i.e. QueryBuilder.query(SELECT(\"*\"))" }
        append(
            sequenceOf<Clause>()
                .plus(selectStatements)
                .plus(joinStatements)
                .plus(whereStatements)
                .plus(andStatements)
                .joinToString(separator = " ") { it.build() }
        )
        toString()
    }

    override fun toString(): String = build()

    internal interface Clause {
        val keyword: String
        val value: String
        fun build(): String
    }

    internal class SELECT(
        override val value: String
    ) : Clause {
        override val keyword: String = "SELECT"
        override fun build(): String = "$keyword $value"
    }

    internal class JOIN(
        override val value: String
    ) : Clause {
        override val keyword: String = "INNER JOIN"
        override fun build(): String = "$keyword $value"
    }

    internal class WHERE(
        override val value: String
    ) : Clause {
        override val keyword: String = "WHERE"
        override fun build(): String = "$keyword $value"
    }

    internal class AND(
        override val value: String
    ) : Clause {
        override val keyword: String = "AND"
        override fun build(): String = "$keyword $value"
    }
}
