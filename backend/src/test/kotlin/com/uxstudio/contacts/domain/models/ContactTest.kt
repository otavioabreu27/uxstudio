package com.uxstudio.contacts.domain.models

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.assertj.core.api.Assertions.assertThat

class ContactTest {

    companion object {
        const val VALID_NAME = "John Doe"
        const val VALID_PHONE = "+36 11 345 6789"
        const val INVALID_PHONE = "1234812931890"
        const val VALID_EMAIL = "john.doe@uxstudio.com"
        const val INVALID_EMAIL = "invalid-email-format"
    }

    @Test
    fun `should throw exception when email format is invalid`() {
        // Arrange & Act
        val exception = assertThrows<IllegalArgumentException> {
            Contact(
                name = VALID_NAME,
                phoneNumber = VALID_PHONE,
                email = INVALID_EMAIL,
                imageId = null
            )
        }

        // Assert
        assertThat(exception.message).contains("Email is invalid")
    }

    @Test
    fun `should throw exception when name is empty`() {
        // Arrange & Act
        val exception = assertThrows<IllegalArgumentException> {
            Contact(
                name = "",
                phoneNumber = VALID_PHONE,
                email = VALID_EMAIL
            )
        }

        // Assert
        // É importante verificar a mensagem aqui também para garantir o motivo da falha
        assertThat(exception.message).contains("Contact name can't be empty.")
    }

    @Test
    fun `should create contact when all values are valid`() {
        // Act
        val contact = Contact(
            name = VALID_NAME,
            phoneNumber = VALID_PHONE,
            email = VALID_EMAIL
        )

        // Assert
        assertThat(contact.name).isEqualTo(VALID_NAME)
        assertThat(contact.email).isEqualTo(VALID_EMAIL)
    }

    @Test
    fun `should throw exception when phoneNumber is invalid`() {
        // Act
        val exception = assertThrows<IllegalArgumentException> {
            Contact(
                name = VALID_NAME,
                phoneNumber = INVALID_PHONE,
                email = VALID_EMAIL
            )
        }

        // Assert
        assertThat(exception.message).contains("Invalid telephone number. Use the international standard (ex: +36 11 345 6789")
    }

    @Test
    fun `should have getters when created successfully`() {
        // Arrange & Act
        val contact = Contact(
            id = "6967987b725f02b73e14fd31",
            imageId = "contacts/images/uuid-123",
            name = VALID_NAME,
            phoneNumber = VALID_PHONE,
            email = VALID_EMAIL
        )

        // Assert
        assertThat(contact.id).isEqualTo("6967987b725f02b73e14fd31")
        assertThat(contact.imageId).isEqualTo("contacts/images/uuid-123")
        assertThat(contact.name).isEqualTo(VALID_NAME)
        assertThat(contact.phoneNumber).isEqualTo(VALID_PHONE)
        assertThat(contact.email).isEqualTo(VALID_EMAIL)
    }
}