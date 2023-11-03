package com.schumaker.api.exception

import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler(private val messageSource: MessageSource) {

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): List<ValidationFormDTO>? {
        val fieldErrors = ex.bindingResult.fieldErrors
        val errors = fieldErrors.map { e ->
            val message = messageSource.getMessage(e, LocaleContextHolder.getLocale())
            ValidationFormDTO(e.field, message)
        }
        return errors
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    protected fun handleMethodArgumentTypeMismatchException(ex: MethodArgumentTypeMismatchException, request: HttpServletRequest
    ): ResponseEntity<ErrorDTO> {
        val error = ErrorDTO(
            title = ex.message,
            status = HttpStatus.BAD_REQUEST,
            path = request.requestURI,
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(DeviceAlreadyExistsException::class)
    protected fun handleDeviceAlreadyCreatedException(ex: DeviceAlreadyExistsException, request: HttpServletRequest
    ): ResponseEntity<ErrorDTO> {
        val error = ErrorDTO(
            title = ex.message,
            status = HttpStatus.CONFLICT,
            path = request.requestURI,
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error)
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    protected fun handleUserAlreadyExistsException(ex: UserAlreadyExistsException, request: HttpServletRequest
    ): ResponseEntity<ErrorDTO> {
        val error = ErrorDTO(
            title = ex.message,
            status = HttpStatus.CONFLICT,
            path = request.requestURI,
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error)
    }

    @ExceptionHandler(DeviceAlreadyAssignedToMeException::class)
    protected fun handleDeviceAlreadyAssignedToMeException(ex: DeviceAlreadyAssignedToMeException, request: HttpServletRequest
    ): ResponseEntity<ErrorDTO> {
        val error = ErrorDTO(
            title = ex.message,
            status = HttpStatus.CONFLICT,
            path = request.requestURI,
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error)
    }

    @ExceptionHandler(DeviceAlreadyAssignedToAnotherUserException::class)
    protected fun handleDeviceAlreadyAssignedToAnotherUserException(ex: DeviceAlreadyAssignedToAnotherUserException, request: HttpServletRequest
    ): ResponseEntity<ErrorDTO> {
        val error = ErrorDTO(
            title = ex.message,
            status = HttpStatus.CONFLICT,
            path = request.requestURI,
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    protected fun handleEntityNotFoundException(ex: EntityNotFoundException, request: HttpServletRequest
    ): ResponseEntity<ErrorDTO> {
        val error = ErrorDTO(
            title = ex.message,
            status = HttpStatus.BAD_REQUEST,
            path = request.requestURI,
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }
}
