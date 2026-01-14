package com.uxstudio.contacts.domain.ports

import com.uxstudio.contacts.domain.models.Contact

/**
 * Output Port for Contact persistence.
 *
 * This interface defines the contract for data storage operations, allowing
 * the domain to remain agnostic of the specific database implementation (e.g., MongoDB).
 */
interface ContactRepoPort {
    /**
     * Persists a contact to the storage system.
     */
    fun save(contact: Contact): Contact

    /**
     * Retrieves all contacts from storage.
     */
    fun findAll(): List<Contact>

    /**
     * Checks if a contact exists with the specified email.
     */
    fun existsByEmail(email: String): Boolean
}