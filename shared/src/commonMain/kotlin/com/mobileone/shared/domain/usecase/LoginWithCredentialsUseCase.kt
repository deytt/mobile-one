package com.mobileone.shared.domain.usecase

import com.mobileone.shared.domain.entity.AuthToken
import com.mobileone.shared.domain.error.AuthDomainError
import com.mobileone.shared.domain.error.asFailure
import com.mobileone.shared.domain.error.toAuthDomainError
import com.mobileone.shared.domain.repository.AuthRepository
import com.mobileone.shared.domain.repository.LoginAttemptStatus
import com.mobileone.shared.domain.repository.SessionRepository
import com.mobileone.shared.domain.validation.AuthValidator

/**
 * Login por CPF + senha (SPEC-001): valida CPF/senha, verifica integridade do dispositivo,
 * autentica e, em caso de falha por credenciais inválidas, incrementa o contador de tentativas
 * do [SessionRepository] até o bloqueio de 5 minutos.
 */
class LoginWithCredentialsUseCase(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository,
    private val deviceIntegrity: ValidateDeviceIntegrityUseCase,
    private val validator: AuthValidator = AuthValidator()
) {
    suspend operator fun invoke(cpf: String, password: String): Result<AuthToken> {
        when (val lock = sessionRepository.lockStatus()) {
            is LoginAttemptStatus.Locked ->
                return AuthDomainError.AccountLocked(lock.remainingSeconds).asFailure()
            is LoginAttemptStatus.Allowed -> Unit
        }

        validator.validateCpf(cpf).onFailure { return Result.failure(it) }
        validator.validatePassword(password).onFailure { return Result.failure(it) }
        deviceIntegrity().onFailure { return Result.failure(it) }

        return authRepository.login(cpf, password).fold(
            onSuccess = { token ->
                sessionRepository.resetFailedAttempts()
                sessionRepository.startSession(token)
                Result.success(token)
            },
            onFailure = { failure ->
                if (failure.toAuthDomainError() is AuthDomainError.NetworkError) {
                    Result.failure(failure)
                } else {
                    when (val status = sessionRepository.registerFailedAttempt()) {
                        is LoginAttemptStatus.Locked ->
                            AuthDomainError.AccountLocked(status.remainingSeconds).asFailure()
                          is LoginAttemptStatus.Allowed ->
                              AuthDomainError.InvalidCredentials(
                                  attemptsRemaining = status.attemptsRemaining,
                                  failedAttempts = status.failedAttempts
                              ).asFailure()
                    }
                }
            }
        )
    }
}
