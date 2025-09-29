package br.com.upvisibility.credentialsservice.dto.bling

import java.time.LocalDate

data class EmpresaBlingWrapperDTO(
    val id: String,
    val nome: String,
    val cnpj: String,
    val email: String,
    val dataContrato: LocalDate
)