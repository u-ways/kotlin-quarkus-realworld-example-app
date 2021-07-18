package io.realworld.utils

class ValidationMessages {
    companion object {
        const val REQUEST_BODY_MUST_NOT_BE_NULL = "request body must not be null"
        const val USERNAME_MUST_MATCH_PATTERN = "username must be not be blank, have whitespace or special characters"
        const val EMAIL_MUST_BE_NOT_BLANK = "email must be not blank"
        const val PASSWORD_MUST_BE_NOT_BLANK = "password must be not blank"
    }
}
