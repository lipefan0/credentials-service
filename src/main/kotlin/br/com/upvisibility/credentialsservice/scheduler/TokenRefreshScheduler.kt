package br.com.upvisibility.credentialsservice.scheduler

import br.com.upvisibility.credentialsservice.entity.CredentialDetails
import br.com.upvisibility.credentialsservice.repository.CredentialsRepository
import br.com.upvisibility.credentialsservice.service.CredentialService
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class TokenRefreshScheduler(
    private val credentialsRepository: CredentialsRepository,
    private val credentialService: CredentialService
) {
    private val logger = LoggerFactory.getLogger(TokenRefreshScheduler::class.java)

    // Executa a cada 1 minuto para testes (depois pode voltar para 5 minutos = 300000)
    @Scheduled(fixedRate = 60000)
    fun checkAndRefreshTokens() {
        logger.info("Iniciando verificação de tokens que precisam ser renovados")

        val tenMinutesFromNow = Instant.now().plusSeconds(600) // 10 minutos

        try {
            // Obter todas as credenciais como lista
            val allCredentials = credentialsRepository.findAll().collectList().block()

            logger.info("Total de credenciais encontradas: ${allCredentials?.size ?: 0}")

            // Filtrar apenas as que precisam ser renovadas
            val credentialsToRenew = allCredentials?.filter { credentials ->
                val details = credentials.details
                if (details is CredentialDetails.OAuth2Details) {
                    val shouldRefresh = details.expiresIn?.isBefore(tenMinutesFromNow) ?: false
                    logger.debug("Credencial ${credentials.id} para usuário ${credentials.userId} e serviço ${credentials.serviceName}: expiresIn=${details.expiresIn}, shouldRefresh=$shouldRefresh")
                    shouldRefresh
                } else {
                    false
                }
            }

            logger.info("Credenciais que precisam ser renovadas: ${credentialsToRenew?.size ?: 0}")

            // Renovar cada credencial que precisa
            credentialsToRenew?.forEach { credentials ->
                logger.info("Token para usuário ${credentials.userId} e serviço ${credentials.serviceName} próximo de expirar. Renovando...")

                runBlocking {
                    try {
                        val updatedCredentials = credentialService.refreshToken(credentials.userId, credentials.serviceName)
                        val details = updatedCredentials.details as CredentialDetails.OAuth2Details
                        logger.info("Token renovado com sucesso para usuário ${credentials.userId} e serviço ${credentials.serviceName}. Expira em: ${details.expiresIn}")
                    } catch (e: Exception) {
                        logger.error("Erro ao renovar token para usuário ${credentials.userId} e serviço ${credentials.serviceName}: ${e.message}", e)
                    }
                }
            }

            logger.info("Verificação de tokens concluída")
        } catch (e: Exception) {
            logger.error("Erro durante a verificação e renovação de tokens: ${e.message}", e)
        }
    }
}
