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
    suspend fun save(contact: Contact): Contact

    /**
     * Deletes a contact on the storage system.
     */
    fun delete(id: String): Contact


    /**
     * Edits a given contract.
     */
    fun edit(contact: Contact): Contact

    /**
     * Retrieves all contacts from storage.
     */
    fun findAll(): List<Contact>

    /**
     * Checks if a contact exists with the specified email.
     */
    fun findById(id: String): Contact?

    /**
     * Checks if a email already exists in the repository
     */
    suspend fun existsByEmail(email: String): Boolean

    /**
     * Checks if a phoneNumber already exists in the repository
     */
    suspend fun existsByPhoneNumber(phoneNumber: String): Boolean
}