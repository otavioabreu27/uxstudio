package com.uxstudio.contacts.infrastructure.web.dto

import com.uxstudio.contacts.domain.models.Contact

/**
 * Data Transfer Object representing the API response for a Contact.
 *
 * This ensures the API contract is stable even if the internal
 * Domain or Entity models change.
 */
data class ContactResponse(
    val id: String?,
    val name: String,
    val phoneNumber: String,
    val email: String,
    val imageId: String?
) {
    companion object {
        /**
         * Factory method to create a response DTO from a Domain model.
         */
        fun fromDomain(contact: Contact) = ContactResponse(
            id = contact.id,
            name = contact.name,
            phoneNumber = contact.phoneNumber,
            email = contact.email,
            imageId = contact.imageId
        )
    }
}