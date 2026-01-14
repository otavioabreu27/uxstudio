package com.uxstudio.contacts.infrastructure.persistence

import com.uxstudio.contacts.domain.models.Contact
import com.uxstudio.contacts.domain.ports.ContactRepoPort
import org.springframework.stereotype.Component

/**
 * Persistence Adapter that implements the [ContactRepoPort] using MongoDB.
 */
@Component
class ContactRepoAdapter(
        private val mongoRepository: MongoContactRepository
) : ContactRepoPort {

    override fun save(contact: Contact): Contact {
        val entity = ContactEntity.fromDomain(contact)
        return mongoRepository.save(entity).toDomain()
    }

    override fun findAll(): List<Contact> {
        return mongoRepository.findAll().map { it.toDomain() }
    }

    override fun existsByEmail(email: String): Boolean {
        return mongoRepository.existsByEmail(email)
    }
}