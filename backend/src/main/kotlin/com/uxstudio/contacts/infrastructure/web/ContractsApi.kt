package com.uxstudio.contacts.infrastructure.web

import com.uxstudio.contacts.infrastructure.web.dto.ContactCreationRequest
import com.uxstudio.contacts.infrastructure.web.dto.ContactEditionRequest
import com.uxstudio.contacts.infrastructure.web.dto.ContactResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Contacts", description = "Endpoints for managing the contact list")
interface ContactsApi {

    @Operation(
        summary = "Get all contacts",
        description = "Retrieves a list of all contacts."
    )
    @ApiResponse(responseCode = "200", description = "List of contacts")
    @GetMapping
    suspend fun getContacts(): List<ContactResponse>

    @Operation(
        summary = "Get a contact by id",
        description = "Retrieves a contact by its id."
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Contact found"),
        ApiResponse(responseCode = "404", description = "Contact not found")
    ])
    @GetMapping("/{id}")
    suspend fun getContactById(@PathVariable id: String): ContactResponse

    @Operation(
        summary = "Create a new contact",
        description = "Validates the input and persists a new contact record."
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Contact successfully created"),
        ApiResponse(responseCode = "400", description = "Invalid input or business rule violation")
    ])
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createContact(@Valid @RequestBody request: ContactCreationRequest): ContactResponse

    @Operation(
        summary = "Edit a created contact",
        description = "Validates the input and persists the changes if needed."
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Contact successfully edited"),
        ApiResponse(responseCode = "400", description = "Invalid input or business rule violation"),
        ApiResponse(responseCode = "404", description = "Contact not found")
    ])
    @PutMapping("/{id}")
    suspend fun editContact(
        @PathVariable id: String,
        @Valid @RequestBody request: ContactEditionRequest
    ): ContactResponse

    @Operation(
        summary = "Delete a contact",
        description = "Delete a given contact if found."
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Contact successfully deleted"),
        ApiResponse(responseCode = "404", description = "Contact not found")
    ])
    @DeleteMapping("/{id}")
    suspend fun deleteContact(
        @PathVariable id: String
    ): ContactResponse
}