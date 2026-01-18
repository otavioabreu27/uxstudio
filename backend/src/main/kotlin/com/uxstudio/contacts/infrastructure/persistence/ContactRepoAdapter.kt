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

    override suspend fun save(contact: Contact): Contact {
        val entity = ContactEntity.fromDomain(contact)
        return mongoRepository.save(entity).toDomain()
    }

    override fun findAll(): List<Contact> {
        return mongoRepository.findAll().map { it.toDomain() }
    }

    override fun findById(id: String): Contact? {
        return mongoRepository.findById(id)
            .map { it.toDomain() }
            .orElse(null)
    }

    override suspend fun existsByEmail(email: String): Boolean {
       return this.mongoRepository.existsByEmail(email);
    }

    override suspend fun existsByPhoneNumber(phoneNumber: String): Boolean {
        return this.mongoRepository.existsByPhoneNumber(phoneNumber);
    }

    override fun edit(contact: Contact): Contact {
        val entity = ContactEntity.fromDomain(contact)
        return mongoRepository.save(entity).toDomain()
    }

    override fun delete(id: String): Contact {
        val entity = mongoRepository.findById(id)
            .orElseThrow { NoSuchElementException("Contact not found with id: $id") }

        mongoRepository.deleteById(id)
        return entity.toDomain()
    }
}