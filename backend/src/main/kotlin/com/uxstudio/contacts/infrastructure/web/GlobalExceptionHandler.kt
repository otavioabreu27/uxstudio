package com.uxstudio.contacts.infrastructure.web

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    /**
     * Handles Domain Invariant errors (require blocks).
     * These are specific business rules like "invalid phone format".
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleDomainExceptions(ex: IllegalArgumentException): ProblemDetail {
        val status = HttpStatus.BAD_REQUEST

        return ProblemDetail.forStatusAndDetail(status, ex.message ?: "Invalid business data").apply {
            title = "Business Rule Violation"
            type = URI.create("https://api.uxstudio.com/errors/business-rule")

            // Custom properties for the frontend
            setProperty("message", "Domain invariant violation: ${ex.javaClass.simpleName}")
            setProperty("userMessage", ex.message ?: "The data provided is invalid.")
            setProperty("statusCode", status.value())
        }
    }

    /**
     * Handles structural validation errors (Jakarta @Valid).
     * Generates a detailed list of which fields failed.
     */
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: org.springframework.http.HttpHeaders,
        status: org.springframework.http.HttpStatusCode,
        request: org.springframework.web.context.request.WebRequest
    ): ResponseEntity<Any>? {
        val errors = ex.bindingResult.fieldErrors.map {
            mapOf(
                "field" to it.field,
                "userMessage" to it.defaultMessage,
                "rejectedValue" to it.rejectedValue
            )
        }

        val problemDetail = ProblemDetail.forStatusAndDetail(status, "Structural validation failed").apply {
            title = "Constraint Violation"
            setProperty("message", "Validation failed for ${ex.objectName}")
            setProperty("userMessage", "Please check your input. Some fields are incorrectly formatted.")
            setProperty("statusCode", status.value())
            setProperty("errors", errors)
        }

        return ResponseEntity.of(problemDetail).build()
    }
}