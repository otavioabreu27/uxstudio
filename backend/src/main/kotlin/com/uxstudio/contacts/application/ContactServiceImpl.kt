package com.uxstudio.contacts.application

import com.uxstudio.contacts.domain.models.Contact
import com.uxstudio.contacts.domain.ports.ContactRepoPort
import com.uxstudio.contacts.domain.ports.ContactService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service



/**
 * Application Service implementing the Contact use cases.
 *
 * It orchestrates the flow between the Input Port and the Output Port,
 * ensuring business rules (like email uniqueness) are enforced.
 *
 * * NOTE: The descriptions of some errors are generic so we don't give
 * much information about our business logic. This is a security measure.
 */
@Service
class ContactServiceImpl(
    private val contactRepoPort: ContactRepoPort
) : ContactService {

    override suspend fun createContact(contact: Contact): Contact = coroutineScope {
        val emailExists = async { contactRepoPort.existsByEmail(contact.email) }
        val phoneExists = async { contactRepoPort.existsByPhoneNumber(contact.phoneNumber) }

        require(!emailExists.await()) { "The given email is invalid" }
        require(!phoneExists.await()) { "The given phone number is invalid" }

        contactRepoPort.save(contact)
    }

    override suspend fun editContact(contact: Contact): Contact = coroutineScope {
        val contactId = contact.id ?: throw IllegalArgumentException("Can't edit that contract") // No contract id.

        val existingContact = findById(contactId);

        val emailCheck = async {
            if (contact.email != existingContact.email) {
                contactRepoPort.existsByEmail(contact.email)
            } else false
        }

        val phoneCheck = async {
            if (contact.phoneNumber != existingContact.phoneNumber) {
                contactRepoPort.existsByPhoneNumber(contact.phoneNumber)
            } else false
        }

        require(!emailCheck.await()) { "The given email is invalid" }
        require(!phoneCheck.await()) { "The given phone number is invalid" }

        contactRepoPort.edit(contact)
    }

    override suspend fun findById(id: String): Contact = coroutineScope {
        contactRepoPort.findById(id) ?: throw NoSuchElementException("Couldn't find the contact")
    }

    override fun deleteContact(id: String): Contact {
        return contactRepoPort.delete(id);
    }
}