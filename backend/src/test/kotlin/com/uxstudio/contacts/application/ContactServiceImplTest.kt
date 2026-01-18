package com.uxstudio.contacts.application

import com.uxstudio.contacts.domain.models.Contact
import com.uxstudio.contacts.domain.ports.ContactRepoPort
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class ContactServiceImplTest {

    private val contactRepoPort = mockk<ContactRepoPort>()
    private val contactService = ContactServiceImpl(contactRepoPort)

    companion object {
        const val TEST_ID = "uuid-123"
        const val TEST_EMAIL = "john.doe@uxstudio.com"
        val TEST_CONTACT = Contact(
            id = TEST_ID,
            name = "John Doe",
            phoneNumber = "+36 11 345 6789",
            email = TEST_EMAIL
        )
    }

    @Test
    fun shouldCreateContactWhenEmailDoesNotExist() = runTest {
        // Arrange
        coEvery { contactRepoPort.existsByEmail(TEST_EMAIL) } returns false
        coEvery { contactRepoPort.existsByPhoneNumber(any()) } returns false
        coEvery { contactRepoPort.save(TEST_CONTACT) } returns TEST_CONTACT

        // Act
        val result = contactService.createContact(TEST_CONTACT)

        // Assert
        assertThat(result).isEqualTo(TEST_CONTACT)

        coVerify(exactly = 1) { contactRepoPort.existsByEmail(TEST_EMAIL) }
        coVerify(exactly = 1) { contactRepoPort.save(TEST_CONTACT) }
    }

    @Test
    fun shouldThrowExceptionWhenCreatingAndEmailAlreadyExists() = runTest {
        // Arrange
        coEvery { contactRepoPort.existsByEmail(TEST_EMAIL) } returns true
        coEvery { contactRepoPort.existsByPhoneNumber(any()) } returns false

        // Act
        val exception = assertThrows<IllegalArgumentException> {
            contactService.createContact(TEST_CONTACT)
        }

        // Assert
        assertThat(exception.message).contains("email is invalid")
        coVerify(exactly = 0) { contactRepoPort.save(any()) }
    }

    @Test
    fun shouldThrowExceptionWhenCreatingAndPhoneNumberAlreadyExists() = runTest {
        // Arrange
        coEvery { contactRepoPort.existsByEmail(TEST_EMAIL) } returns false
        coEvery { contactRepoPort.existsByPhoneNumber(any()) } returns true

        // Act
        val exception = assertThrows<IllegalArgumentException> {
            contactService.createContact(TEST_CONTACT)
        }

        // Assert
        assertThat(exception.message).contains("phone number is invalid")
        coVerify(exactly = 0) { contactRepoPort.save(any()) }
    }

    @Test
    fun shouldFindContactByIdSuccessfully() = runTest {
        // Arrange
        coEvery { contactRepoPort.findById(TEST_ID) } returns TEST_CONTACT

        // Act
        val result = contactService.findById(TEST_ID)

        // Assert
        assertThat(result).isEqualTo(TEST_CONTACT)
        coVerify(exactly = 1) { contactRepoPort.findById(TEST_ID) }
    }

    @Test
    fun shouldThrowExceptionWhenContactNotFoundById() = runTest {
        // Arrange
        coEvery { contactRepoPort.findById(TEST_ID) } returns null

        // Act
        val exception = assertThrows<NoSuchElementException> {
            contactService.findById(TEST_ID)
        }

        // Assert
        assertThat(exception.message).contains("Couldn't find the contact")
    }

    @Test
    fun shouldThrowExceptionWhenEditingContactWithoutId() = runTest {
        // Arrange
        val contactWithoutId = TEST_CONTACT.copy(id = null)

        // Act
        val exception = assertThrows<IllegalArgumentException> {
            contactService.editContact(contactWithoutId)
        }

        // Assert
        assertThat(exception.message).contains("Can't edit that contract")
        coVerify(exactly = 0) { contactRepoPort.edit(any()) }
    }

    @Test
    fun shouldEditContactSuccessfullyWhenDataChangedAndUnique() = runTest {
        // Arrange
        val updatedContact = TEST_CONTACT.copy(email = "new@uxstudio.com")

        // Mock finding the *original* contact to compare against
        coEvery { contactRepoPort.findById(TEST_ID) } returns TEST_CONTACT
        // Mock checks for the *new* data
        coEvery { contactRepoPort.existsByEmail("new@uxstudio.com") } returns false
        coEvery { contactRepoPort.existsByPhoneNumber(any()) } returns false
        coEvery { contactRepoPort.edit(updatedContact) } returns updatedContact

        // Act
        val result = contactService.editContact(updatedContact)

        // Assert
        assertThat(result).isEqualTo(updatedContact)
        coVerify(exactly = 1) { contactRepoPort.edit(updatedContact) }
    }

    @Test
    fun shouldEditContactSuccessfullyWhenDataIsNotChanged() = runTest {
        // Arrange - Data is exactly the same as existing
        coEvery { contactRepoPort.findById(TEST_ID) } returns TEST_CONTACT
        coEvery { contactRepoPort.edit(TEST_CONTACT) } returns TEST_CONTACT

        // Act
        val result = contactService.editContact(TEST_CONTACT)

        // Assert
        assertThat(result).isEqualTo(TEST_CONTACT)

        // Verify we optimized queries: Checks should NOT be called if data didn't change
        coVerify(exactly = 0) { contactRepoPort.existsByEmail(any()) }
        coVerify(exactly = 0) { contactRepoPort.existsByPhoneNumber(any()) }
        coVerify(exactly = 1) { contactRepoPort.edit(TEST_CONTACT) }
    }

    @Test
    fun shouldThrowExceptionWhenEditingAndNewEmailAlreadyExists() = runTest {
        // Arrange
        val updatedContact = TEST_CONTACT.copy(email = "taken@uxstudio.com")

        coEvery { contactRepoPort.findById(TEST_ID) } returns TEST_CONTACT
        coEvery { contactRepoPort.existsByEmail("taken@uxstudio.com") } returns true
        coEvery { contactRepoPort.existsByPhoneNumber(any()) } returns false // Phone didn't change anyway

        // Act
        val exception = assertThrows<IllegalArgumentException> {
            contactService.editContact(updatedContact)
        }

        // Assert
        assertThat(exception.message).contains("email is invalid")
        coVerify(exactly = 0) { contactRepoPort.edit(any()) }
    }

    @Test
    fun shouldThrowExceptionWhenEditingAndNewPhoneNumberAlreadyExists() = runTest {
        // Arrange
        val updatedContact = TEST_CONTACT.copy(phoneNumber = "+36 99 999 9999")

        coEvery { contactRepoPort.findById(TEST_ID) } returns TEST_CONTACT
        coEvery { contactRepoPort.existsByPhoneNumber("+36 99 999 9999") } returns true
        // Note: Email logic in Service is async, so existsByEmail might or might not be called depending on scheduler,
        // but since email didn't change in this test setup, it should be skipped by logic.

        // Act
        val exception = assertThrows<IllegalArgumentException> {
            contactService.editContact(updatedContact)
        }

        // Assert
        assertThat(exception.message).contains("phone number is invalid")
        coVerify(exactly = 0) { contactRepoPort.edit(any()) }
    }


    @Test
    fun shouldDeleteContactSuccessfully() = runTest {
        // Arrange
        coEvery { contactRepoPort.delete(TEST_ID) } returns TEST_CONTACT

        // Act
        val result = contactService.deleteContact(TEST_ID)

        // Assert
        assertThat(result).isEqualTo(TEST_CONTACT)
        coVerify(exactly = 1) { contactRepoPort.delete(TEST_ID) }
    }
}