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
    suspend fun createContact(contact: Contact): Contact

    /**
     * Find a contact based on its id.
     * @throws NoSuchElementException if the contact is not found.
     */
    suspend fun findById(id: String): Contact

    /**
     * Edit an existing contact after validating business rules.
     * @throws IllegalArgumentException if business invariants are violated.
     */
    suspend fun editContact(contact: Contact): Contact

    /**
     * Delete an existing contact after validating busines rules.
     * @throws IllegalArgumentException if business invariants are violated.
     */
    fun deleteContact(id: String): Contact
}