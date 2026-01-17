package com.uxstudio.contacts.application

import com.uxstudio.contacts.domain.ports.ImageRepoPort
import com.uxstudio.contacts.domain.ports.ImageService
import org.springframework.stereotype.Service
import java.util.Base64

/**
 * Implementation of [ImageService] that handles image processing logic.
 *
 * This service acts as the bridge between the high-level Base64 strings used by
 * the web layer and the raw binary data required by the persistence layer.
 */
@Service
class ImageServiceImpl(
    private val imageRepoPort: ImageRepoPort
) : ImageService {

    override fun createImage(imageBase64: String): String {
        val (bytes, contentType) = decodeBase64(imageBase64)
        return imageRepoPort.store(bytes, contentType)
    }

    override fun getImage(imageId: String): ByteArray? {
        return imageRepoPort.load(imageId)
    }

    override fun deleteImage(imageId: String) {
        imageRepoPort.delete(imageId)
    }

    /**
     * Decodes a Base64 string, handling optional data URI prefixes.
     * @throws IllegalArgumentException with a precise message for the GlobalExceptionHandler.
     */
    private fun decodeBase64(base64: String): Pair<ByteArray, String> {
        return try {
            val parts = base64.split(",")
            val contentType = if (parts.size > 1) {
                parts[0].substringAfter(":").substringBefore(";")
            } else {
                "image/png" // Default fallback
            }
            val data = parts.last()
            Base64.getDecoder().decode(data) to contentType
        } catch (e: Exception) {
            throw IllegalArgumentException("The provided image data is corrupted or formatted incorrectly.")
        }
    }
}