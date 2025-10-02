package br.com.upvisibility.credentialsservice.repository

import br.com.upvisibility.credentialsservice.entity.CredentialsEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface CredentialsRepository : ReactiveMongoRepository<CredentialsEntity, String> {
    fun findByUserIdAndServiceName(userId: String, serviceName: String): Mono<CredentialsEntity>

    fun findByExternalIdAndServiceName(externalId: String, serviceName: String): Mono<CredentialsEntity>
}
