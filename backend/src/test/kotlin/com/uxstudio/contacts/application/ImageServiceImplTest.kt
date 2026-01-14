package com.uxstudio.contacts.application

import com.uxstudio.contacts.domain.ports.ImageRepoPort
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ImageServiceImplTest {

    private val imageRepoPort = mockk<ImageRepoPort>()
    private val imageService = ImageServiceImpl(imageRepoPort)

    companion object {
        const val VALID_BASE64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg=="
        const val RAW_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg=="
        const val IMAGE_ID = "contacts/images/uuid-123"
        val MOCK_BYTES = "mock-image-content".toByteArray()
    }

    @Test
    fun shouldDecodeBase64AndStoreImageSuccessfully() {
        every { imageRepoPort.store(any(), "image/png") } returns IMAGE_ID

        val result = imageService.createImage(VALID_BASE64)

        assertThat(result).isEqualTo(IMAGE_ID)
        verify(exactly = 1) { imageRepoPort.store(any(), "image/png") }
    }

    @Test
    fun shouldDecodeBase64WithoutPrefixUsingDefaultContentType() {
        every { imageRepoPort.store(any(), "image/png") } returns IMAGE_ID

        val result = imageService.createImage(RAW_BASE64)

        assertThat(result).isEqualTo(IMAGE_ID)
        verify(exactly = 1) { imageRepoPort.store(any(), "image/png") }
    }

    @Test
    fun shouldThrowExceptionWhenBase64IsCorrupted() {
        val corruptedBase64 = "data:image/png;base64,invalid-chars-@#$%"

        val exception = assertThrows<IllegalArgumentException> {
            imageService.createImage(corruptedBase64)
        }

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
        every { imageRepoPort.load(IMAGE_ID) } returns null

        val result = imageService.getImage(IMAGE_ID)

        assertThat(result).isNull()
    }

    @Test
    fun shouldUpdateImageSuccessfully() {
        // Arrange
        every { imageRepoPort.update(IMAGE_ID, any()) } just runs

        // Act
        imageService.updateImage(IMAGE_ID, VALID_BASE64)

        // Assert
        verify(exactly = 1) { imageRepoPort.update(IMAGE_ID, match { it.isNotEmpty() }) }
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