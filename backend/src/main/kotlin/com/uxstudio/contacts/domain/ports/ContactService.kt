package com.uxstudio.contacts.domain.ports

import com.uxstudio.contacts.domain.models.Contact

/**
 * Input Port defining the primary use cases for Contact management.
 */
interface ContactService {
    /**
     * Creates a new contact after validating business rules.
     * @throws IllegalArgumentException if business invariants are violated.
     */
    fun createContact(contact: Contact): Contact
}