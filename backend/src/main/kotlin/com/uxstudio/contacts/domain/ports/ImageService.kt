package com.uxstudio.contacts.domain.ports

/**
 * Input Port defining the business logic for Image management.
 * * This service orchestrates image processing, such as decoding Base64 strings,
 * before delegating storage tasks to the [ImageRepoPort].
 */
interface ImageService {
    /**
     * Decodes and uploads an image from a Base64 string.
     * @param imageBase64 The Base64 encoded image (optionally with data URI prefix).
     * @return The unique ID generated for the uploaded image.
     * @throws IllegalArgumentException if the Base64 format is invalid.
     */
    fun createImage(imageBase64: String): String

    /**
     * Retrieves an image by its ID.
     * @param imageId The unique ID of the image.
     * @return The byte array representing the image.
     */
    fun getImage(imageId: String): ByteArray?

    /**
     * Updates an existing image with a new Base64 string.
     * @param imageId The ID of the image to update.
     * @param imageBase64 The new Base64 encoded image string.
     */
    fun updateImage(imageId: String, imageBase64: String)

    /**
     * Deletes an image from the system.
     * @param imageId The ID of the image to be removed.
     */
    fun deleteImage(imageId: String)
}