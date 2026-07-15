package com.usher.auth.registration.api

import com.usher.auth.authentication.api.AuthTokenResponse
import com.usher.auth.authentication.api.LoginRequest
import com.usher.auth.authentication.api.MeResponse
import com.usher.auth.authentication.api.RefreshRequest
import com.usher.auth.authentication.application.GetCurrentUserService
import com.usher.auth.authentication.application.LoginUserCommand
import com.usher.auth.authentication.application.LoginUserService
import com.usher.auth.authentication.application.RefreshAuthTokensCommand
import com.usher.auth.authentication.application.RefreshAuthTokensService
import com.usher.auth.registration.application.RegisterUserCommand
import com.usher.auth.registration.application.RegisterUserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val registerUserService: RegisterUserService,
    private val loginUserService: LoginUserService,
    private val refreshAuthTokensService: RefreshAuthTokensService,
    private val getCurrentUserService: GetCurrentUserService,
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

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): AuthTokenResponse {
        val tokens = loginUserService.login(
            LoginUserCommand(email = request.email, password = request.password),
        )

        return AuthTokenResponse(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            expiresIn = tokens.expiresIn,
        )
    }

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody request: RefreshRequest): AuthTokenResponse {
        val tokens = refreshAuthTokensService.refresh(
            RefreshAuthTokensCommand(refreshToken = request.refreshToken),
        )

        return AuthTokenResponse(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            expiresIn = tokens.expiresIn,
        )
    }

    @GetMapping("/me")
    fun me(@RequestHeader("Authorization", required = false) authorizationHeader: String?): MeResponse {
        val user = getCurrentUserService.getCurrentUser(authorizationHeader)

        return MeResponse(
            id = user.id,
            email = user.email,
            role = user.role,
            createdAt = user.createdAt,
        )
    }
}
