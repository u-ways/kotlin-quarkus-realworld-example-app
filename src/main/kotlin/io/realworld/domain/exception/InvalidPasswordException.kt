package io.realworld.domain.exception

import io.quarkus.security.ForbiddenException

class InvalidPasswordException : ForbiddenException("Invalid password")
