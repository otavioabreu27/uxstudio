package com.uxstudio.contacts.infrastructure.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@Configuration
class S3Config {

    @Value("\${aws.s3.endpoint}")
    lateinit var endpoint: String

    @Value("\${aws.s3.region}")
    lateinit var region: String

    @Value("\${aws.s3.access-key}")
    lateinit var accessKey: String

    @Value("\${aws.s3.secret-key}")
    lateinit var secretKey: String

    @Value("\${aws.s3.path-style-access}")
    var pathStyleAccess: Boolean = false

    @Bean
    fun s3Client(): S3Client {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)

        return S3Client.builder()
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .forcePathStyle(pathStyleAccess)
            .build()
    }
}