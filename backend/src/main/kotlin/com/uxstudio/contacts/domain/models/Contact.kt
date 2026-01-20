package com.uxstudio.contacts.domain.models

/**
 * Core Domain model representing a Contact.
 *
 * This class enforces business invariants during initialization. It ensures that
 * no Contact object can exist in an invalid state within the system's business logic.
 *
 * @property id The domain-level unique identifier.
 * @property imageId Optional reference to an associated image asset.
 * @property name The contact's name (must not be blank).
 * @property phoneNumber The contact's phone number following international standards.
 * @property email A valid email address containing the '@' symbol.
 */
data class Contact(
    val id: String? = null,
    val imageId: String? = null,
    val name: String,
    val phoneNumber: String,
    val email: String
) {
    init {
        require(name.isNotBlank()) { "Contact name can't be empty." }
        require(email.contains("@")) { "Email is invalid." }

        val internationalPhoneRegex = """^\+\d{1,3}([\s-]?\d+)+$""".toRegex()

        require(internationalPhoneRegex.matches(phoneNumber)) {
            "Invalid telephone number. Use the international standard (ex: +36 11 345 6789)."
        }
    }
}