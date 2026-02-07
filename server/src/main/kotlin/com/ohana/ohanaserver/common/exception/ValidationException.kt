package com.ohana.ohanaserver.common.exception

class ValidationException(val errors: List<FieldError>) : RuntimeException("Validation failed")
