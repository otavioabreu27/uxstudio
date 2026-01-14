package com.uxstudio.contacts.infrastructure.persistence

import com.uxstudio.contacts.domain.ports.ImageRepoPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import java.util.UUID

/**
 * Persistence Adapter for AWS S3.
 *
 * It translates domain storage requests into AWS S3 API calls.
 * Error handling is designed to be caught by the GlobalExceptionHandler via
 * standard runtime exceptions.
 */
@Component
class S3ImageAdapter(
    private val s3Client: S3Client,
    @Value("\${aws.s3.bucket-name}") private val bucketName: String
) : ImageRepoPort {

    override fun store(bytes: ByteArray, contentType: String): String {
        val extension = contentType.substringAfter("/")
        val key = "contacts/images/${UUID.randomUUID()}.$extension"

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(contentType)
            .build()

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes))
        return key
    }

    override fun load(imageId: String): ByteArray? {
        return try {
            val getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(imageId)
                .build()

            s3Client.getObject(getObjectRequest).readAllBytes()
        } catch (e: NoSuchKeyException) {
            null
        }
    }

    override fun update(imageId: String, bytes: ByteArray) {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(imageId)
            .build()

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes))
    }

    override fun delete(imageId: String) {
        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(imageId)
            .build()

        s3Client.deleteObject(deleteObjectRequest)
    }
}