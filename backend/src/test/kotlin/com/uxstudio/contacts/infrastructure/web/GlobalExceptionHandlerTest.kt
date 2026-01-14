package com.uxstudio.contacts.infrastructure.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.uxstudio.contacts.infrastructure.web.dto.ContactRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

// Creating a mock controller
@RestController
class ExceptionTestController {
    @PostMapping("/test-domain-error")
    fun triggerDomainError() {
        throw IllegalArgumentException("Invalid business rule")
    }

    @PostMapping("/test-domain-error-null")
    fun triggerDomainErrorNull() {
        throw IllegalArgumentException(null as String?)
    }

    @PostMapping("/test-validation-error")
    fun triggerValidationError(@Valid @RequestBody request: ContactRequest) {
        // Only to trigger valid
    }
}

@WebMvcTest(ExceptionTestController::class)
@Import(GlobalExceptionHandler::class)
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should handle IllegalArgumentException and return ProblemDetail`() {
        // Act & Assert
        mockMvc.post("/test-domain-error")
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.title") { value("Business Rule Violation") }
                jsonPath("$.detail") { value("Invalid business rule") }
                jsonPath("$.userMessage") { value("Invalid business rule") }
                jsonPath("$.statusCode") { value(400) }
                jsonPath("$.type") { value("https://api.uxstudio.com/errors/business-rule") }
            }
    }

    @Test
    fun `should handle MethodArgumentNotValidException and return field errors`() {
        // Arrange
        val invalidRequest = ContactRequest(
            name = "",
            phoneNumber = "+36",
            email = "invalid-email"
        )

        // Act & Assert
        mockMvc.post("/test-validation-error") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(invalidRequest)
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.title") { value("Constraint Violation") }
            jsonPath("$.detail") { value("Structural validation failed") }
            // Verificamos se a lista de erros detalhados está presente
            jsonPath("$.errors") { isArray() }
            jsonPath("$.errors[?(@.field == 'name')].userMessage") { isNotEmpty() }
            jsonPath("$.statusCode") { value(400) }
        }
    }

    @Test
    fun `should use default message when IllegalArgumentException message is null`() {
        // Act & Assert
        mockMvc.post("/test-domain-error-null")
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.detail") { value("Invalid business data") }
                jsonPath("$.userMessage") { value("The data provided is invalid.") } // Valida o fallback do elvis operator
                jsonPath("$.title") { value("Business Rule Violation") }
            }
    }
}