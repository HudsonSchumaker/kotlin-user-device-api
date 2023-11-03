package com.schumaker.api.exception

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

data class ErrorDTO(
    val title: String?,
    val path: String,
    val status: HttpStatus,
    val timestamp: LocalDateTime = LocalDateTime.now(),
)
