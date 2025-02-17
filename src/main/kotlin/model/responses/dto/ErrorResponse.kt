package org.example.model.responses.dto

data class ErrorResponse(
    val id : String,
    val message : String,
    val description: String = " "
)