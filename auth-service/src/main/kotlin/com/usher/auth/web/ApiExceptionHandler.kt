package com.usher.auth.web

import com.usher.auth.authentication.application.InvalidAccessTokenException
import com.usher.auth.authentication.application.InvalidCredentialsException
import com.usher.auth.authentication.application.InvalidRefreshTokenException
import com.usher.auth.authentication.application.MissingAccessTokenException
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

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ApiError(code = "invalid_credentials", message = "Invalid credentials"),
        )

    @ExceptionHandler(InvalidRefreshTokenException::class)
    fun handleInvalidRefreshToken(): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ApiError(code = "invalid_refresh_token", message = "Invalid refresh token"),
        )

    @ExceptionHandler(MissingAccessTokenException::class)
    fun handleMissingAccessToken(): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ApiError(code = "missing_access_token", message = "Missing access token"),
        )

    @ExceptionHandler(InvalidAccessTokenException::class)
    fun handleInvalidAccessToken(): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ApiError(code = "invalid_access_token", message = "Invalid access token"),
        )
}
