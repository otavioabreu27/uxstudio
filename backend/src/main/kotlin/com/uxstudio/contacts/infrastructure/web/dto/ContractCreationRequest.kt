package com.uxstudio.contacts.infrastructure.web.dto

import com.uxstudio.contacts.domain.models.Contact
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import io.swagger.v3.oas.annotations.media.Schema

/**
 * Data Transfer Object for incoming contact creation requests.
 * Uses Jakarta Validation for structural integrity and
 * SpringDoc for API documentation.
 */
data class ContactRequest(
    @field:NotBlank(message = "Name is required")
    @field:Schema(example = "John Doe")
    val name: String,

    @field:NotBlank(message = "Phone number is required")
    @field:Pattern(
        regexp = """^\+\d{1,3}([\s-]?\d+)+$""",
        message = "Phone must follow international format (e.g., +36 11 345 6789)"
    )
    @field:Schema(example = "+36 11 345 6789")
    val phoneNumber: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    @field:Schema(example = "john.doe@uxstudio.com")
    val email: String,

    @field:Schema(
        description = "Base64 encoded string of the contact image",
        example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEU..."
    )
    val imageBase64: String? = null
) {
    /**
     * Maps the DTO to the pure Domain Model.
     * Note: The imageId is initially set to the base64 string or null;
     * the controller can later overwrite this with a storage ID using .copy()
     * after processing the upload.
     *
     * @return A [Contact] domain object.
     */
    fun toDomain() = Contact(
        id = null,
        imageId = imageBase64,
        name = name,
        phoneNumber = phoneNumber,
        email = email
    )
}