package com.uxstudio.contacts.infrastructure.persistence

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data MongoDB repository for [ContactEntity].
 *
 * This is a low-level infrastructure interface. It should only be accessed
 * by the [ContactRepoAdapter] to ensure the domain remains decoupled
 * from Spring Data specifics.
 */
@Repository
interface MongoContactRepository : MongoRepository<ContactEntity, String> {

    /**
     * Checks for existence of a record by email.
     * Spring Data derives the query automatically from the method name.
     */
    fun existsByEmail(email: String): Boolean

    /**
     * Retrieves a contact by email for unique constraint checks.
     */
    fun findByEmail(email: String): ContactEntity?
}