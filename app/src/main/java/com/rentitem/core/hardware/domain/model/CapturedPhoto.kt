package com.rentitem.core.hardware.domain.model


data class CapturedPhoto(
    val uri: String,
    val timestamp: Long,
    val isTemporary: Boolean = true
)
