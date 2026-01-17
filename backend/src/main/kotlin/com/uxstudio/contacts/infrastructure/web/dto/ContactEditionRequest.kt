package com.uxstudio.contacts.infrastructure.web.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

@Schema(description = "Request object for updating an existing contact's information")
data class ContactEditionRequest (

    @field:Schema(
        description = "Optional edited full name of the contact",
        example = "John Doe"
    )
    val editedName: String?,

    @field:Pattern(
        regexp = """^\+\d{1,3}([\s-]?\d+)+$""",
        message = "Phone must follow international format (e.g., +36 11 345 6789)"
    )
    @field:Schema(
        description = "Optional edited contact's phone number in international format, including country code",
        example = "+36 11 345 6789"
    )
    val editedPhoneNumber: String?,

    @field:Email(message = "Invalid email format")
    @field:Schema(
        description = "Optional edited email address of the contact",
        example = "john.doe@uxstudio.com"
    )
    val editedEmail: String?,

    @field:Schema(
        description = "Optional profile image encoded as a Base64 string. Use this to update the contact's photo.",
        example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII="
    )
    val editedImageBase64: String?
)