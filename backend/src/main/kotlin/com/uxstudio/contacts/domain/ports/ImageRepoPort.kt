package com.uxstudio.contacts.domain.ports

/**
 * Output Port for Image storage operations.
 * * This contract defines the necessary CRUD operations for handling image assets
 * in a remote storage provider, such as Amazon S3.
 */
interface ImageRepoPort {
    /**
     * Persists the image data to the storage provider.
     * @param bytes The raw byte array of the image.
     * @param contentType The MIME type (e.g., image/png).
     * @return A unique identifier/key for the stored image.
     */
    fun store(bytes: ByteArray, contentType: String): String

    /**
     * Retrieves the raw image data from storage.
     * @param imageId The unique identifier of the image.
     * @return The byte array of the image, or null if not found.
     */
    fun load(imageId: String): ByteArray?

    /**
     * Replaces an existing image with new data.
     * @param imageId The ID of the image to update.
     * @param bytes The new raw byte array.
     */
    fun update(imageId: String, bytes: ByteArray)

    /**
     * Removes the image from the storage provider.
     * @param imageId The ID of the image to delete.
     */
    fun delete(imageId: String)
}