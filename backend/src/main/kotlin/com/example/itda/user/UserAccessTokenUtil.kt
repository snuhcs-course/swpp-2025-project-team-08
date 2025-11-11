package com.example.itda.user

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.nio.charset.StandardCharsets
import java.util.Date

object UserAccessTokenUtil {
    private val SECRET_KEY =
        run {
            val secretString =
                System.getProperty("JWT_SECRET_KEY")
                    ?: System.getenv("JWT_SECRET_KEY")
                    ?: throw IllegalStateException("JWT_SECRET_KEY is not set!")
            Keys.hmacShaKeyFor(secretString.toByteArray(StandardCharsets.UTF_8))
        }
    private const val ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 // 1 day
    private const val REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7 // 7 days

    fun generateAccessToken(id: String): String {
        val now = Date()
        val expiryDate = Date(now.time + ACCESS_TOKEN_EXPIRATION_TIME)
        return Jwts.builder()
            .signWith(SECRET_KEY)
            .setSubject(id)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .compact()
    }

    fun generateRefreshToken(id: String): String {
        val now = Date()
        val expiryDate = Date(now.time + REFRESH_TOKEN_EXPIRATION_TIME)
        return Jwts.builder()
            .signWith(SECRET_KEY)
            .setSubject(id)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .compact()
    }

    fun validateAccessTokenGetUserId(accessToken: String): String? {
        return try {
            val claims =
                Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(accessToken)
                    .body
            if (claims.expiration.before(Date())) null else claims.subject
        } catch (e: Exception) {
            println("Token validation failed. Please try again.")
            null
        }
    }

    fun getAccessTokenExpirationSeconds(): Long {
        return ACCESS_TOKEN_EXPIRATION_TIME / 1000L
    }
}
