package com.uxstudio.contacts.application

import com.uxstudio.contacts.domain.models.Contact
import com.uxstudio.contacts.domain.ports.ContactRepoPort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class ContactServiceImplTest {

    private val contactRepoPort = mockk<ContactRepoPort>()

    private val contactService = ContactServiceImpl(contactRepoPort)

    companion object {
        const val TEST_EMAIL = "john.doe@uxstudio.com"
        val TEST_CONTACT = Contact(
            name = "John Doe",
            phoneNumber = "+36 11 345 6789",
            email = TEST_EMAIL
        )
    }

    @Test
    fun shouldCreateContactWhenEmailDoesNotExist() {
        // Arrange
        every { contactRepoPort.existsByEmail(TEST_EMAIL) } returns false
        every { contactRepoPort.save(TEST_CONTACT) } returns TEST_CONTACT

        // Act
        val result = contactService.createContact(TEST_CONTACT)

        // Assert
        assertThat(result).isEqualTo(TEST_CONTACT)

        verify(exactly = 1) { contactRepoPort.existsByEmail(TEST_EMAIL) }
        verify(exactly = 1) { contactRepoPort.save(TEST_CONTACT) }
    }

    @Test
    fun shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        every { contactRepoPort.existsByEmail(TEST_EMAIL) } returns true

        // Act
        val exception = assertThrows<IllegalArgumentException> {
            contactService.createContact(TEST_CONTACT)
        }

        // Assert
        assertThat(exception.message).contains("already exists")

        verify(exactly = 0) { contactRepoPort.save(any())}

    }
}