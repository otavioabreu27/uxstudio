package com.uxstudio.contacts.infrastructure.web

import com.uxstudio.contacts.domain.ports.ContactService
import com.uxstudio.contacts.domain.ports.ImageService
import com.uxstudio.contacts.infrastructure.web.dto.ContactRequest
import com.uxstudio.contacts.infrastructure.web.dto.ContactResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
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
    private val imageServce: ImageService
) : ContactsApi {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun createContact(@Valid @RequestBody request: ContactRequest): ContactResponse {
        val uploadedImageId = request.imageBase64?.let { base64 ->
            imageServce.createImage(base64)
        }

        val domain = request.toDomain().copy(imageId = uploadedImageId)

        val savedContact = contactService.createContact(domain)

        return ContactResponse.fromDomain(savedContact)
    }
}