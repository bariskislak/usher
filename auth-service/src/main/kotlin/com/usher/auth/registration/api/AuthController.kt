package com.usher.auth.registration.api

import com.usher.auth.registration.application.RegisterUserCommand
import com.usher.auth.registration.application.RegisterUserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val registerUserService: RegisterUserService,
) {
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<RegisterResponse> {
        val registeredUser = registerUserService.register(
            RegisterUserCommand(email = request.email, password = request.password),
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(
            RegisterResponse(
                id = registeredUser.id,
                email = registeredUser.email,
                role = registeredUser.role,
                createdAt = registeredUser.createdAt,
            ),
        )
    }
}
