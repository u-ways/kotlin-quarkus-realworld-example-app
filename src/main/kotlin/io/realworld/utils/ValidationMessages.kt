package io.realworld.utils

internal class ValidationMessages {
    companion object {
        const val REQUEST_BODY_MUST_NOT_BE_NULL = "request body must not be null"
        const val USERNAME_MUST_MATCH_PATTERN = "username must not be blank, have whitespace or special characters"
        const val EMAIL_MUST_BE_NOT_BLANK = "email must not be blank"
        const val PASSWORD_MUST_BE_NOT_BLANK = "password must not be blank"
        const val SLUG_MUST_MATCH_PATTERN = "Slug must not be blank, have whitespace or special characters"
        const val TITLE_MUST_NOT_BE_BLANK = "Title must not be blank"
    }
}
