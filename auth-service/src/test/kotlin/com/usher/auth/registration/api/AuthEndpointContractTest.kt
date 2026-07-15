package com.usher.auth.registration.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.usher.auth.authentication.application.GetCurrentUserService
import com.usher.auth.authentication.application.LoginUserService
import com.usher.auth.authentication.application.RefreshAuthTokensService
import com.usher.auth.registration.application.RegisterUserService
import com.usher.auth.test.FakeRefreshTokenRepository
import com.usher.auth.test.FakeUserRepository
import com.usher.auth.token.JwtAccessTokenService
import com.usher.auth.token.RefreshTokenHasher
import com.usher.auth.token.RefreshTokenIssuer
import com.usher.auth.user.User
import com.usher.auth.web.ApiExceptionHandler
import org.hamcrest.Matchers.blankOrNullString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class AuthEndpointContractTest {
    private val objectMapper = jacksonObjectMapper()
    private val passwordEncoder = BCryptPasswordEncoder()
    private val clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
    private lateinit var userRepository: FakeUserRepository
    private lateinit var refreshTokenRepository: FakeRefreshTokenRepository
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        userRepository = FakeUserRepository()
        refreshTokenRepository = FakeRefreshTokenRepository()
        val refreshTokenHasher = RefreshTokenHasher()
        val accessTokenService = JwtAccessTokenService(
            secret = "test-secret-must-be-at-least-32-bytes-long",
            expiresInSeconds = 900,
            clock = clock,
        )
        val refreshTokenIssuer = RefreshTokenIssuer(
            refreshTokenRepository = refreshTokenRepository,
            refreshTokenHasher = refreshTokenHasher,
            clock = clock,
            expiresInSeconds = 604800,
        )
        val registerUserService = RegisterUserService(userRepository, passwordEncoder)
        val loginUserService = LoginUserService(
            userRepository = userRepository,
            passwordEncoder = passwordEncoder,
            accessTokenService = accessTokenService,
            refreshTokenIssuer = refreshTokenIssuer,
        )
        val refreshAuthTokensService = RefreshAuthTokensService(
            refreshTokenRepository = refreshTokenRepository,
            refreshTokenHasher = refreshTokenHasher,
            refreshTokenIssuer = refreshTokenIssuer,
            accessTokenService = accessTokenService,
            userRepository = userRepository,
            clock = clock,
        )
        val getCurrentUserService = GetCurrentUserService(
            accessTokenService = accessTokenService,
            userRepository = userRepository,
        )

        mockMvc = MockMvcBuilders
            .standaloneSetup(
                AuthController(
                    registerUserService = registerUserService,
                    loginUserService = loginUserService,
                    refreshAuthTokensService = refreshAuthTokensService,
                    getCurrentUserService = getCurrentUserService,
                ),
            )
            .setControllerAdvice(ApiExceptionHandler())
            .build()
    }

    @Test
    fun `POST auth register creates a user`() {
        mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = json(
                "email" to "user@example.com",
                "password" to "secure-password",
            )
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { value(not(blankOrNullString())) }
            jsonPath("$.email") { value("user@example.com") }
            jsonPath("$.role") { value("USER") }
            jsonPath("$.createdAt") { value(not(blankOrNullString())) }
        }
    }

    @Test
    fun `POST auth login returns access and refresh tokens for valid credentials`() {
        existingUser(email = "user@example.com", password = "secure-password")

        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = json(
                "email" to "user@example.com",
                "password" to "secure-password",
            )
        }.andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { value(not(blankOrNullString())) }
            jsonPath("$.refreshToken") { value(not(blankOrNullString())) }
            jsonPath("$.tokenType") { value("Bearer") }
            jsonPath("$.expiresIn") { value(900) }
        }
    }

    @Test
    fun `POST auth login rejects wrong password`() {
        existingUser(email = "user@example.com", password = "secure-password")

        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = json(
                "email" to "user@example.com",
                "password" to "wrong-password",
            )
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.code") { value("invalid_credentials") }
        }
    }

    @Test
    fun `POST auth refresh rotates refresh token and returns new tokens`() {
        existingUser(email = "user@example.com", password = "secure-password")
        val refreshToken = login(email = "user@example.com", password = "secure-password").refreshToken

        mockMvc.post("/auth/refresh") {
            contentType = MediaType.APPLICATION_JSON
            content = json("refreshToken" to refreshToken)
        }.andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { value(not(blankOrNullString())) }
            jsonPath("$.refreshToken") { value(not(blankOrNullString())) }
            jsonPath("$.refreshToken") { value(not(refreshToken)) }
            jsonPath("$.tokenType") { value("Bearer") }
            jsonPath("$.expiresIn") { value(900) }
        }
    }

    @Test
    fun `POST auth refresh rejects an invalid refresh token`() {
        mockMvc.post("/auth/refresh") {
            contentType = MediaType.APPLICATION_JSON
            content = json("refreshToken" to "invalid-refresh-token")
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.code") { value("invalid_refresh_token") }
        }
    }

    @Test
    fun `POST auth refresh rejects a reused refresh token`() {
        existingUser(email = "user@example.com", password = "secure-password")
        val refreshToken = login(email = "user@example.com", password = "secure-password").refreshToken

        mockMvc.post("/auth/refresh") {
            contentType = MediaType.APPLICATION_JSON
            content = json("refreshToken" to refreshToken)
        }.andExpect {
            status { isOk() }
        }

        mockMvc.post("/auth/refresh") {
            contentType = MediaType.APPLICATION_JSON
            content = json("refreshToken" to refreshToken)
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.code") { value("invalid_refresh_token") }
        }
    }

    @Test
    fun `GET auth me returns the current authenticated user`() {
        val user = existingUser(email = "user@example.com", password = "secure-password")
        val accessToken = login(email = "user@example.com", password = "secure-password").accessToken

        mockMvc.get("/auth/me") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(user.id.toString()) }
            jsonPath("$.email") { value("user@example.com") }
            jsonPath("$.role") { value("USER") }
            jsonPath("$.createdAt") { value(not(blankOrNullString())) }
        }
    }

    @Test
    fun `GET auth me rejects missing access token`() {
        mockMvc.get("/auth/me")
            .andExpect {
                status { isUnauthorized() }
                jsonPath("$.code") { value("missing_access_token") }
            }
    }

    private fun existingUser(email: String, password: String): User =
        userRepository.save(
            User(
                email = email,
                passwordHash = passwordEncoder.encode(password),
            ),
        )

    private fun login(email: String, password: String): LoginResponse {
        val response = mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = json(
                "email" to email,
                "password" to password,
            )
        }.andReturn().response.contentAsString
        val json = objectMapper.readTree(response)

        return LoginResponse(
            accessToken = json.get("accessToken").asText(),
            refreshToken = json.get("refreshToken").asText(),
        )
    }

    private fun json(vararg fields: Pair<String, Any>): String =
        objectMapper.writeValueAsString(mapOf(*fields))

    private data class LoginResponse(
        val accessToken: String,
        val refreshToken: String,
    )
}
