package com.uxstudio.contacts.infrastructure.web

import com.uxstudio.contacts.domain.ports.ContactService
import com.uxstudio.contacts.domain.ports.ImageService
import com.uxstudio.contacts.infrastructure.web.dto.ContactCreationRequest
import com.uxstudio.contacts.infrastructure.web.dto.ContactEditionRequest
import com.uxstudio.contacts.infrastructure.web.dto.ContactResponse
import org.springframework.web.bind.annotation.*

/**
 * REST Controller implementation for Contact management.
 * * Acts as a Driving Adapter that delegates to the [ContactService].
 * Documentation is defined in the [ContactsApi] interface.
 */
@RestController
@RequestMapping("/contacts")
class ContactsController(
    private val contactService: ContactService,
    private val imageService: ImageService
) : ContactsApi {

    override suspend fun createContact(request: ContactCreationRequest): ContactResponse {
        val uploadedImageId = request.imageBase64?.let { base64 ->
            imageService.createImage(base64)
        }

        val domain = request.toDomain().copy(imageId = uploadedImageId)

        val savedContact = contactService.createContact(domain)

        return ContactResponse.fromDomain(savedContact)
    }

    override suspend fun editContact(
        id: String,
        request: ContactEditionRequest
    ): ContactResponse {
        val existingContact = contactService.findById(id);

        val oldImageId = existingContact.imageId

        val updatedImageId = if (request.editedImageBase64 != null) {
            val newImageId = imageService.createImage(request.editedImageBase64)

            if (oldImageId != null) {
                imageService.deleteImage(oldImageId)
            }

            newImageId
        } else {
            oldImageId
        }

        val contactToUpdate = existingContact.copy(
            name = request.editedName ?: existingContact.name,
            phoneNumber = request.editedPhoneNumber ?: existingContact.phoneNumber,
            email = request.editedEmail ?: existingContact.email,
            imageId = updatedImageId
        )

        val updatedContact = contactService.editContact(contactToUpdate)

        return ContactResponse.fromDomain(updatedContact)
    }

    override suspend fun deleteContact(id: String): ContactResponse {
        val currentContact = contactService.findById(id)

        val imageIdToDelete = currentContact.imageId

        val deletedContact = contactService.deleteContact(id)

        if (imageIdToDelete != null) {
            try {
                imageService.deleteImage(imageIdToDelete)
            } catch (e: Exception) {
                println("Warning: Failed to delete image $imageIdToDelete from S3")
            }
        }

        return ContactResponse.fromDomain(deletedContact)
    }
}