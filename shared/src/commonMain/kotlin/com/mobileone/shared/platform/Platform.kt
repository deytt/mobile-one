package com.mobileone.shared.platform

/**
 * Contrato expect/actual usado para expor informações básicas da plataforma.
 */
expect class Platform() {
    val name: String
}

fun greet(): String = "Hello, mobile-one! ${Platform().name}"

/**
 * Retorna o timestamp atual em segundos desde a epoch Unix (plataforma neutra).
 * Usado pelo [ExecutePixTransferUseCase] para determinar o período PIX (diurno/noturno).
 */
expect fun currentEpochSeconds(): Long
