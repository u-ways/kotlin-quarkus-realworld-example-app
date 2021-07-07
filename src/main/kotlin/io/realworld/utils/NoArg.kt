package io.realworld.utils

/**
 * A no argument decorator that is used to decorate classes eligible for noArg initialisation.
 * It is mainly used to solve constructor hell issues. (i.e. JPA constructor hell)
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class NoArg
