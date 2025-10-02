package br.com.upvisibility.credentialsservice.service

import br.com.upvisibility.credentialsservice.client.ClientBling
import br.com.upvisibility.credentialsservice.entity.CredentialDetails
import br.com.upvisibility.credentialsservice.entity.CredentialsEntity
import br.com.upvisibility.credentialsservice.exception.CredentialAlreadyExistsException
import br.com.upvisibility.credentialsservice.repository.CredentialsRepository
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class CredentialService(
    private val clientBling: ClientBling,
    private val credentialsRepository: CredentialsRepository
) {

    suspend fun createBlingCredentials(userId: String, code: String): CredentialsEntity {

        val existingCredentials = credentialsRepository
            .findByUserIdAndServiceName(userId, "bling")
            .awaitSingleOrNull()

        if (existingCredentials != null) {
            throw CredentialAlreadyExistsException("Credencial para o usuário $userId e serviço 'bling' já existe.")
        }

        val tokenInfo = clientBling.generateToken(code)
        val empresaInfo = clientBling.getEmpresaDetails(tokenInfo.access_token)

        val credentials = CredentialsEntity(
            userId = userId,
            serviceName = "bling",
            externalId = empresaInfo.data.id,
            details = CredentialDetails.OAuth2Details(
                accessToken = tokenInfo.access_token,
                refreshToken = tokenInfo.refresh_token,
                expiresIn = Instant.now().plusSeconds(tokenInfo.expires_in.toLong())
            )
        )
        return credentialsRepository.save(credentials).awaitSingle()
    }


    suspend fun refreshToken(userId: String, serviceName: String): CredentialsEntity {
        val credentials = credentialsRepository
            .findByUserIdAndServiceName(userId, serviceName)
            .awaitSingleOrNull()
            ?: throw IllegalArgumentException("Credenciais para usuário $userId e serviço $serviceName não encontradas")

        val oauth2Details = credentials.details as? CredentialDetails.OAuth2Details
            ?: throw IllegalStateException("Credenciais não são do tipo OAuth2")

        val refreshedToken = clientBling.refreshToken(oauth2Details.refreshToken)

        val updatedCredentials = credentials.copy(
            details = CredentialDetails.OAuth2Details(
                accessToken = refreshedToken.access_token,
                refreshToken = refreshedToken.refresh_token,
                expiresIn = Instant.now().plusSeconds(refreshedToken.expires_in.toLong())
            )
        )

        return credentialsRepository.save(updatedCredentials).awaitSingle()
    }

    suspend fun userHasBlingCredentials(userId: String): Boolean {
        val credentials = credentialsRepository
            .findByUserIdAndServiceName(userId, "bling")
            .awaitSingleOrNull()
        return credentials != null
    }

    suspend fun findCredentialsByExternalId(externalId: String, serviceName: String): CredentialsEntity? {
        return credentialsRepository
            .findByExternalIdAndServiceName(externalId, serviceName)
            .awaitSingleOrNull()
    }
}