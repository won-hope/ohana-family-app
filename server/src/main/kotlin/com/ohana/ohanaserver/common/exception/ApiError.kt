package com.ohana.ohanaserver.common.exception

data class FieldError(val field: String, val message: String)

data class ErrorResponse(
    val code: String,
    val message: String,
    val errors: List<FieldError> = emptyList()
)
