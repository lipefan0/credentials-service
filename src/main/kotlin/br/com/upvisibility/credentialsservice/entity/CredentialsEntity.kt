package br.com.upvisibility.credentialsservice.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "credentials")
data class CredentialsEntity(
    @Id
    val id: String? = null,

    @Indexed(unique = true)
    val userId: String,

    @Indexed
    val serviceName: String,

    @Indexed
    val externalId: String?,

    val details: CredentialDetails,

    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
