package com.usher.auth.web

import com.usher.auth.registration.application.EmailAlreadyRegisteredException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(EmailAlreadyRegisteredException::class)
    fun handleEmailAlreadyRegistered(): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(
            ApiError(code = "email_already_registered", message = "Email is already registered"),
        )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(): ResponseEntity<ApiError> =
        ResponseEntity.badRequest().body(
            ApiError(code = "validation_failed", message = "Request validation failed"),
        )
}
