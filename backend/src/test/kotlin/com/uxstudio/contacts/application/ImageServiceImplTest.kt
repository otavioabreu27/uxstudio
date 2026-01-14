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
    fun `should decode base64 and store image successfully`() {
        every { imageRepoPort.store(any(), "image/png") } returns IMAGE_ID

        val result = imageService.createImage(VALID_BASE64)

        assertThat(result).isEqualTo(IMAGE_ID)
        verify(exactly = 1) { imageRepoPort.store(any(), "image/png") }
    }

    @Test
    fun `should decode base64 without prefix using default content type`() {
        // Testa o fallback do método privado decodeBase64 para "image/png"
        every { imageRepoPort.store(any(), "image/png") } returns IMAGE_ID

        val result = imageService.createImage(RAW_BASE64)

        assertThat(result).isEqualTo(IMAGE_ID)
        verify(exactly = 1) { imageRepoPort.store(any(), "image/png") }
    }

    @Test
    fun `should throw exception when base64 is corrupted`() {
        val corruptedBase64 = "data:image/png;base64,invalid-chars-@#$%"

        val exception = assertThrows<IllegalArgumentException> {
            imageService.createImage(corruptedBase64)
        }

        assertThat(exception.message).contains("image data is corrupted")
        verify(exactly = 0) { imageRepoPort.store(any(), any()) }
    }

    @Test
    fun `should load image bytes successfully`() {
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
    fun `should return null when image is not found`() {
        every { imageRepoPort.load(IMAGE_ID) } returns null

        val result = imageService.getImage(IMAGE_ID)

        assertThat(result).isNull()
    }

    @Test
    fun `should update image successfully`() {
        // Arrange
        every { imageRepoPort.update(IMAGE_ID, any()) } just runs

        // Act
        imageService.updateImage(IMAGE_ID, VALID_BASE64)

        // Assert
        verify(exactly = 1) { imageRepoPort.update(IMAGE_ID, match { it.isNotEmpty() }) }
    }

    @Test
    fun `should delete image successfully`() {
        // Arrange
        every { imageRepoPort.delete(IMAGE_ID) } just runs

        // Act
        imageService.deleteImage(IMAGE_ID)

        // Assert
        verify(exactly = 1) { imageRepoPort.delete(IMAGE_ID) }
    }
}