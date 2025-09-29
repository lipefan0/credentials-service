package br.com.upvisibility.credentialsservice.repository

import br.com.upvisibility.credentialsservice.entity.CredentialsEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CredentialsRepository: MongoRepository<CredentialsEntity, String> {
    suspend fun findByUserIdAndServiceName(userId: String, serviceName: String): CredentialsEntity?

    suspend fun findByServiceNameAndExternalId(serviceName: String, externalId: String): CredentialsEntity?
}