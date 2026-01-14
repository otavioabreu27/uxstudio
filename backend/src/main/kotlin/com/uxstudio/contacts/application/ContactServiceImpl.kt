package com.uxstudio.contacts.application

import com.uxstudio.contacts.domain.models.Contact
import com.uxstudio.contacts.domain.ports.ContactRepoPort
import com.uxstudio.contacts.domain.ports.ContactService
import org.springframework.stereotype.Service

/**
 * Application Service implementing the Contact use cases.
 *
 * It orchestrates the flow between the Input Port and the Output Port,
 * ensuring business rules (like email uniqueness) are enforced.
 */
@Service
class ContactServiceImpl(
    private val contactRepoPort: ContactRepoPort
) : ContactService {

    override fun createContact(contact: Contact): Contact {
        require(!contactRepoPort.existsByEmail(contact.email)) {
            "A contact with email ${contact.email} already exists."
        }

        return contactRepoPort.save(contact)
    }
}