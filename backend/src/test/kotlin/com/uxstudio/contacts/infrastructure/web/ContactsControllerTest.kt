package com.uxstudio.contacts.infrastructure.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.uxstudio.contacts.domain.models.Contact
import com.uxstudio.contacts.domain.ports.ContactService
import com.uxstudio.contacts.domain.ports.ImageService
import com.uxstudio.contacts.infrastructure.config.SecurityConfig
import com.uxstudio.contacts.infrastructure.web.dto.ContactRequest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.isNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import io.mockk.clearMocks
import org.junit.jupiter.api.BeforeEach


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
        val TEST_REQUEST = ContactRequest(
            name = "John Doe",
            phoneNumber = "+36 11 345 6789",
            email = "john.doe@uxstudio.com",
            imageBase64 = TEST_BASE64
        )
    }

    @Test
    fun shouldCreateContactWithImageSuccessfully() {
        // Arrange
        val expectedContact = Contact(
            id = "6967987b725f02b73e14fd31",
            name = TEST_REQUEST.name,
            phoneNumber = TEST_REQUEST.phoneNumber,
            email = TEST_REQUEST.email,
            imageId = MOCK_IMAGE_ID
        )

        every { imageService.createImage(TEST_BASE64) } returns MOCK_IMAGE_ID
        every { contactService.createContact(any()) } returns expectedContact

        // Act & Assert
        mockMvc.post("/contacts") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(TEST_REQUEST)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.name") { value(TEST_REQUEST.name) }
            jsonPath("$.imageId") { value(MOCK_IMAGE_ID) }
        }

        verify(exactly = 1) { imageService.createImage(TEST_BASE64) }
        verify(exactly = 1) { contactService.createContact(match { it.imageId == MOCK_IMAGE_ID }) }
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
    fun shouldCreateContactSuccessfullyWhenImageIsNotProvided() {
        // Arrange
        val requestWithoutImage = TEST_REQUEST.copy(imageBase64 = null)

        val expectedContact = Contact(
            id = "6967987b725f02b73e14fd31",
            name = requestWithoutImage.name,
            phoneNumber = requestWithoutImage.phoneNumber,
            email = requestWithoutImage.email,
            imageId = null
        )

        every { contactService.createContact(any()) } returns expectedContact

        // Act
        mockMvc.post("/contacts") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(requestWithoutImage)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.imageId") { isNull() }
        }

        // Assert
        verify(exactly = 0) { imageService.createImage(any()) }
    }
}