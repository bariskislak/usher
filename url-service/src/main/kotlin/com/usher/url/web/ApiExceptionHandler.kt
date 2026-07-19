package com.usher.url.web

import com.usher.url.shortening.api.InvalidOwnerIdException
import com.usher.url.shortening.api.MissingOwnerIdException
import com.usher.url.shortening.application.InvalidOriginalUrlException
import com.usher.url.shortening.application.ShortCodeGenerationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(): ResponseEntity<ApiError> =
        ResponseEntity.badRequest().body(
            ApiError(code = "validation_failed", message = "Request validation failed"),
        )

    @ExceptionHandler(MissingOwnerIdException::class)
    fun handleMissingOwnerId(): ResponseEntity<ApiError> =
        ResponseEntity.badRequest().body(
            ApiError(code = "missing_owner_id", message = "Missing owner id"),
        )

    @ExceptionHandler(InvalidOwnerIdException::class)
    fun handleInvalidOwnerId(): ResponseEntity<ApiError> =
        ResponseEntity.badRequest().body(
            ApiError(code = "invalid_owner_id", message = "Invalid owner id"),
        )

    @ExceptionHandler(InvalidOriginalUrlException::class)
    fun handleInvalidOriginalUrl(): ResponseEntity<ApiError> =
        ResponseEntity.badRequest().body(
            ApiError(code = "invalid_original_url", message = "Original URL must be a valid http or https URL"),
        )

    @ExceptionHandler(ShortCodeGenerationException::class)
    fun handleShortCodeGeneration(): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(
            ApiError(code = "short_code_generation_failed", message = "Could not generate a unique short code"),
        )
}
