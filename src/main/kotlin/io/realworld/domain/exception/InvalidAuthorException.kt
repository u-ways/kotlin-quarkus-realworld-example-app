package io.realworld.domain.exception

import io.quarkus.security.UnauthorizedException

class InvalidAuthorException : UnauthorizedException("LoggedIn User is not the Article's author")
