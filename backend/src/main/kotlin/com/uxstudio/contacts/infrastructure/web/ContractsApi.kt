package com.uxstudio.contacts.infrastructure.web

import com.uxstudio.contacts.infrastructure.web.dto.ContactRequest
import com.uxstudio.contacts.infrastructure.web.dto.ContactResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.RequestBody

/**
 * REST API Contract for Contact Management.
 * * This interface isolates the documentation and mapping annotations from
 * the controller implementation, following the "Interface-first" approach.
 */
@Tag(name = "Contacts", description = "Endpoints for managing the contact list")
interface ContactsApi {

    @Operation(
        summary = "Create a new contact",
        description = "Validates the input and persists a new contact record."
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Contact successfully created"),
        ApiResponse(responseCode = "400", description = "Invalid input or business rule violation")
    ])
    fun createContact(@Valid @RequestBody request: ContactRequest): ContactResponse
}