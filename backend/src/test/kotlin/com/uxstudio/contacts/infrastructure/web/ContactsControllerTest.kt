package com.uxstudio.contacts.infrastructure.web

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.request
import org.hamcrest.CoreMatchers.nullValue

import com.fasterxml.jackson.databind.ObjectMapper
import com.uxstudio.contacts.domain.models.Contact
import com.uxstudio.contacts.domain.ports.ContactService
import com.uxstudio.contacts.domain.ports.ImageService
import com.uxstudio.contacts.infrastructure.config.SecurityConfig
import com.uxstudio.contacts.infrastructure.web.dto.ContactCreationRequest
import com.uxstudio.contacts.infrastructure.web.dto.ContactEditionRequest
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(ContactsController::class)
@Import(SecurityConfig::class)
class ContactsControllerTest {

    @BeforeEach
    fun setUp() {
        clearMocks(imageService, contactService)
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @TestConfiguration
    class MockConfig {
        @Bean fun contactService() = mockk<ContactService>()
        @Bean fun imageService() = mockk<ImageService>()
    }

    @Autowired
    private lateinit var contactService: ContactService

    @Autowired
    private lateinit var imageService: ImageService

    companion object {
        const val TEST_BASE64 = "data:image/png;base64,iVBORw0..."
        const val MOCK_IMAGE_ID = "contacts/images/uuid-123"
        const val NEW_IMAGE_ID = "contacts/images/uuid-456"
        const val TEST_ID = "6967987b725f02b73e14fd31"

        val TEST_REQUEST = ContactCreationRequest(
            name = "John Doe",
            phoneNumber = "+36 11 345 6789",
            email = "john.doe@uxstudio.com",
            imageBase64 = TEST_BASE64
        )

        val TEST_EDITION_REQUEST = ContactEditionRequest(
            editedName = "John Updated",
            editedPhoneNumber = "+36 11 345 9999",
            editedEmail = "john.updated@uxstudio.com",
            editedImageBase64 = TEST_BASE64 // Ajustado para bater com o Controller
        )
    }

    @Test
    fun shouldCreateContactWithImageSuccessfully() = runTest {
        val expectedContact = Contact(
            id = TEST_ID,
            name = TEST_REQUEST.name,
            phoneNumber = TEST_REQUEST.phoneNumber,
            email = TEST_REQUEST.email,
            imageId = MOCK_IMAGE_ID
        )

        coEvery { imageService.createImage(TEST_BASE64) } returns MOCK_IMAGE_ID
        coEvery { contactService.createContact(any()) } returns expectedContact

        val mvcResult = mockMvc.post("/contacts") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(TEST_REQUEST)
        }.andExpect {
            request { asyncStarted() }
        }.andReturn()

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value(TEST_REQUEST.name))
            .andExpect(jsonPath("$.imageId").value(MOCK_IMAGE_ID))

        coVerify(exactly = 1) { imageService.createImage(TEST_BASE64) }
        coVerify(exactly = 1) { contactService.createContact(match { it.imageId == MOCK_IMAGE_ID }) }
    }

    @Test
    fun shouldCreateContactSuccessfullyWhenImageIsNotProvided() = runTest {
        val requestWithoutImage = TEST_REQUEST.copy(imageBase64 = null)
        val expectedContact = Contact(
            id = TEST_ID,
            name = requestWithoutImage.name,
            phoneNumber = requestWithoutImage.phoneNumber,
            email = requestWithoutImage.email,
            imageId = null
        )

        coEvery { contactService.createContact(any()) } returns expectedContact

        val mvcResult = mockMvc.post("/contacts") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(requestWithoutImage)
        }.andExpect {
            request { asyncStarted() }
        }.andReturn()

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.imageId").value(nullValue()))

        coVerify(exactly = 0) { imageService.createImage(any()) }
    }

    @Test
    fun shouldReturn400WhenRequestIsInvalid() {
        val invalidRequest = TEST_REQUEST.copy(name = "")

        mockMvc.post("/contacts") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(invalidRequest)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun shouldEditContactAndReplaceImageSuccessfully() = runTest {
        // Arrange
        val existingContact = Contact(
            id = TEST_ID,
            name = "Old Name",
            phoneNumber = "+36 00 000 0000",
            email = "old@uxstudio.com",
            imageId = MOCK_IMAGE_ID
        )

        val updatedContact = existingContact.copy(
            name = TEST_EDITION_REQUEST.editedName!!,
            imageId = NEW_IMAGE_ID // Terá nova imagem
        )

        coEvery { contactService.findById(TEST_ID) } returns existingContact
        coEvery { imageService.createImage(TEST_BASE64) } returns NEW_IMAGE_ID
        coEvery { imageService.deleteImage(MOCK_IMAGE_ID) } just Runs
        coEvery { contactService.editContact(any()) } returns updatedContact

        // Act
        val mvcResult = mockMvc.perform(
            put("/contacts/{id}", TEST_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TEST_EDITION_REQUEST))
        ).andExpect(request().asyncStarted()).andReturn()

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.imageId").value(NEW_IMAGE_ID))
            .andExpect(jsonPath("$.name").value(TEST_EDITION_REQUEST.editedName))

        coVerify(exactly = 1) { imageService.createImage(TEST_BASE64) }
        coVerify(exactly = 1) { imageService.deleteImage(MOCK_IMAGE_ID) }
        coVerify(exactly = 1) { contactService.editContact(any()) }
    }

    @Test
    fun shouldEditContactWithoutChangingImage() = runTest {
        // Arrange
        val requestWithoutImage = TEST_EDITION_REQUEST.copy(editedImageBase64 = null)

        val existingContact = Contact(
            id = TEST_ID,
            name = "Old Name",
            phoneNumber = "+36 00 000 0000",
            email = "old@uxstudio.com",
            imageId = MOCK_IMAGE_ID
        )

        val updatedContact = existingContact.copy(name = requestWithoutImage.editedName!!)

        coEvery { contactService.findById(TEST_ID) } returns existingContact
        coEvery { contactService.editContact(any()) } returns updatedContact

        // Act
        val mvcResult = mockMvc.perform(
            put("/contacts/{id}", TEST_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestWithoutImage))
        ).andExpect(request().asyncStarted()).andReturn()

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.imageId").value(MOCK_IMAGE_ID))
        coVerify(exactly = 0) { imageService.createImage(any()) }
        coVerify(exactly = 0) { imageService.deleteImage(any()) }
    }

    @Test
    fun shouldDeleteContactAndImageSuccessfully() = runTest {
        // Arrange
        val contactToDelete = Contact(
            id = TEST_ID,
            name = "John",
            phoneNumber = "+36 00 000 0000",
            email = "john@test.com",
            imageId = MOCK_IMAGE_ID
        )

        coEvery { contactService.findById(TEST_ID) } returns contactToDelete
        coEvery { contactService.deleteContact(TEST_ID) } returns contactToDelete
        coEvery { imageService.deleteImage(MOCK_IMAGE_ID) } just Runs

        // Act
        val mvcResult = mockMvc.perform(
            delete("/contacts/{id}", TEST_ID)
        ).andExpect(request().asyncStarted()).andReturn()

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk)

        coVerify(exactly = 1) { contactService.deleteContact(TEST_ID) }
        coVerify(exactly = 1) { imageService.deleteImage(MOCK_IMAGE_ID) }
    }

    @Test
    fun shouldDeleteContactEvenIfImageDeletionFails() = runTest {
        // Arrange
        val contactToDelete = Contact(
            id = TEST_ID,
            name = "John",
            phoneNumber = "+36 00 000 0000",
            email = "john@test.com",
            imageId = MOCK_IMAGE_ID
        )

        coEvery { contactService.findById(TEST_ID) } returns contactToDelete
        coEvery { contactService.deleteContact(TEST_ID) } returns contactToDelete
        coEvery { imageService.deleteImage(MOCK_IMAGE_ID) } throws RuntimeException("S3 Down")

        // Act
        val mvcResult = mockMvc.perform(
            delete("/contacts/{id}", TEST_ID)
        ).andExpect(request().asyncStarted()).andReturn()

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk)

        coVerify(exactly = 1) { contactService.deleteContact(TEST_ID) }
        coVerify(exactly = 1) { imageService.deleteImage(MOCK_IMAGE_ID) }
    }

    @Test
    fun shouldAddNewImageToContactThatHadNoImagePreviously() = runTest {
        // Arrange
        val existingContactNoImage = Contact(
            id = TEST_ID,
            name = "Old Name",
            phoneNumber = "+36 00 000 0000",
            email = "old@uxstudio.com",
            imageId = null
        )

        val updatedContact = existingContactNoImage.copy(imageId = NEW_IMAGE_ID)

        coEvery { contactService.findById(TEST_ID) } returns existingContactNoImage
        coEvery { imageService.createImage(TEST_BASE64) } returns NEW_IMAGE_ID
        coEvery { contactService.editContact(any()) } returns updatedContact

        // Act
        val mvcResult = mockMvc.perform(
            put("/contacts/{id}", TEST_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TEST_EDITION_REQUEST))
        ).andExpect(request().asyncStarted()).andReturn()

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.imageId").value(NEW_IMAGE_ID))

        coVerify(exactly = 0) { imageService.deleteImage(any()) }
        coVerify(exactly = 1) { imageService.createImage(TEST_BASE64) }
    }

    @Test
    fun shouldPartiallyUpdateContactPreservingExistingFieldsWhenInputIsNull() = runTest {
        // Arrange
        val partialRequest = ContactEditionRequest(
            editedName = null,
            editedPhoneNumber = null,
            editedEmail = null,
            editedImageBase64 = null
        )

        val existingContact = Contact(
            id = TEST_ID,
            name = "Original Name",
            phoneNumber = "+36 00 000 0000",
            email = "original@email.com",
            imageId = MOCK_IMAGE_ID
        )

        coEvery { contactService.findById(TEST_ID) } returns existingContact
        coEvery { contactService.editContact(any()) } answers { firstArg() }

        // Act
        val mvcResult = mockMvc.perform(
            put("/contacts/{id}", TEST_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialRequest))
        ).andExpect(request().asyncStarted()).andReturn()

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Original Name"))

        coVerify(exactly = 1) {
            contactService.editContact(match {
                it.name == "Original Name" &&
                        it.phoneNumber == "+36 00 000 0000"
            })
        }
    }

    @Test
    fun shouldDeleteContactThatHasNoImage() = runTest {
        // Arrange
        val contactNoImage = Contact(
            id = TEST_ID,
            name = "John",
            phoneNumber = "+36 00 000 0000",
            email = "john@test.com",
            imageId = null
        )

        coEvery { contactService.findById(TEST_ID) } returns contactNoImage
        coEvery { contactService.deleteContact(TEST_ID) } returns contactNoImage

        // Act
        val mvcResult = mockMvc.perform(
            delete("/contacts/{id}", TEST_ID)
        ).andExpect(request().asyncStarted()).andReturn()

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk)

        coVerify(exactly = 0) { imageService.deleteImage(any()) }
    }

    @Test
    fun shouldReturnAllContacts() = runTest {
        // Arrange
        val contactsList = listOf(
            Contact(
                id = "1",
                name = "Alice",
                phoneNumber = "+36 11 111 1111",
                email = "alice@test.com",
                imageId = "img1"
            ),
            Contact(
                id = "2",
                name = "Bob",
                phoneNumber = "+36 22 222 2222",
                email = "bob@test.com",
                imageId = null
            )
        )

        coEvery { contactService.getAllContacts() } returns contactsList

        // Act
        val mvcResult = mockMvc.perform(
            get("/contacts")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(request().asyncStarted()).andReturn()

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[0].name").value("Alice"))
            .andExpect(jsonPath("$[1].name").value("Bob"))

        coVerify(exactly = 1) { contactService.getAllContacts() }
    }

    @Test
    fun shouldReturnContactById() = runTest {
        // Arrange
        val expectedContact = Contact(
            id = TEST_ID,
            name = "John Doe",
            phoneNumber = "+36 11 345 6789",
            email = "john.doe@uxstudio.com",
            imageId = MOCK_IMAGE_ID
        )

        coEvery { contactService.findById(TEST_ID) } returns expectedContact

        // Act
        val mvcResult = mockMvc.perform(
            get("/contacts/{id}", TEST_ID)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(request().asyncStarted()).andReturn()

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(TEST_ID))
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.imageId").value(MOCK_IMAGE_ID))

        coVerify(exactly = 1) { contactService.findById(TEST_ID) }
    }
}