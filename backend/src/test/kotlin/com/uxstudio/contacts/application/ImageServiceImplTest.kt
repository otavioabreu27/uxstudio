package com.uxstudio.contacts.application

import com.uxstudio.contacts.domain.ports.ImageRepoPort
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.BeforeEach

class ImageServiceImplTest {

    private val imageRepoPort = mockk<ImageRepoPort>()
    private val imageService = ImageServiceImpl(imageRepoPort)

    @BeforeEach
    fun setUp() {
        clearMocks(imageRepoPort)
    }

    companion object {
        const val IMAGE_ID = "contacts/images/uuid-123"
        const val VALID_PNG_BASE64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg=="
        const val VALID_JPEG_BASE64 = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAAAAAAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAABAAEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD3+iiigD//2Q=="
        const val RAW_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg=="
        val MOCK_BYTES = "mock-image-content".toByteArray()
    }

    @Test
    fun shouldDecodePNGBase64AndStoreWithCorrectContentType() {
        // Arrange
        every { imageRepoPort.store(any(), "image/png") } returns IMAGE_ID

        // Act
        val result = imageService.createImage(VALID_PNG_BASE64)

        // Assert
        assertThat(result).isEqualTo(IMAGE_ID)
        verify(exactly = 1) { imageRepoPort.store(any(), "image/png") }
    }

    @Test
    fun shouldDecodeJPEGBase64AndStoreWithCorrectContentType() {
        // Arrange
        every { imageRepoPort.store(any(), "image/jpeg") } returns IMAGE_ID

        // Act
        val result = imageService.createImage(VALID_JPEG_BASE64)

        // Assert
        assertThat(result).isEqualTo(IMAGE_ID)
        verify(exactly = 1) {
            imageRepoPort.store(any(), "image/jpeg")
        }
    }

    @Test
    fun shouldUseDefaultContentTypeWhenPrefixIsMissing() {
        // Arrange
        every { imageRepoPort.store(any(), "image/png") } returns IMAGE_ID

        // Act
        val result = imageService.createImage(RAW_BASE64)

        // Assert
        assertThat(result).isEqualTo(IMAGE_ID)
        verify(exactly = 1) { imageRepoPort.store(any(), "image/png") }
    }

    @Test
    fun shouldThrowExceptionWhenBase64IsCorrupted() {
        // Arrange
        val corruptedBase64 = "data:image/png;base64,invalid-chars-@#$%"

        // Act
        val exception = assertThrows<IllegalArgumentException> {
            imageService.createImage(corruptedBase64)
        }

        // Assert
        assertThat(exception.message).contains("image data is corrupted")
        verify(exactly = 0) { imageRepoPort.store(any(), any()) }
    }

    @Test
    fun shouldLoadImageBytesSuccessfully() {
        // Arrange
        every { imageRepoPort.load(IMAGE_ID) } returns MOCK_BYTES

        // Act
        val result = imageService.getImage(IMAGE_ID)

        // Assert
        assertThat(result).isNotNull
        assertThat(result).isEqualTo(MOCK_BYTES)
        verify(exactly = 1) { imageRepoPort.load(IMAGE_ID) }
    }

    @Test
    fun shouldReturnNullWhenImageIsNotFound() {
        // Arrange
        every { imageRepoPort.load(IMAGE_ID) } returns null

        // Act
        val result = imageService.getImage(IMAGE_ID)

        // Assert
        assertThat(result).isNull()
    }

    @Test
    fun shouldDeleteImageSuccessfully() {
        // Arrange
        every { imageRepoPort.delete(IMAGE_ID) } just runs

        // Act
        imageService.deleteImage(IMAGE_ID)

        // Assert
        verify(exactly = 1) { imageRepoPort.delete(IMAGE_ID) }
    }
}