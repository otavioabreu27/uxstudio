package com.uxstudio.contacts.infrastructure.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.uxstudio.contacts.infrastructure.web.dto.ContactCreationRequest
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
    fun triggerValidationError(@Valid @RequestBody request: ContactCreationRequest) {}
}

@WebMvcTest(ExceptionTestController::class)
@Import(GlobalExceptionHandler::class)
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
            private const val PATH_TITLE = "$.title"
        private const val PATH_DETAIL = "$.detail"
        private const val PATH_USER_MESSAGE = "$.userMessage"
        private const val PATH_STATUS_CODE = "$.statusCode"
    }

    @Test
    fun shouldHandleIllegalArgumentExceptionAndReturnProblemDetail() {
        mockMvc.post("/test-domain-error")
            .andExpect {
                status { isBadRequest() }
                jsonPath(PATH_TITLE) { value("Business Rule Violation") }
                jsonPath(PATH_DETAIL) { value("Invalid business rule") }
                jsonPath(PATH_USER_MESSAGE) { value("Invalid business rule") }
                jsonPath(PATH_STATUS_CODE) { value(400) }
                jsonPath("$.type") { value("https://api.uxstudio.com/errors/business-rule") }
            }
    }

    @Test
    fun shouldHandleMethodArgumentNotValidExceptionAndReturnFieldErrors() {
        val invalidRequest = ContactCreationRequest(
            name = "",
            phoneNumber = "+36",
            email = "invalid-email"
        )

        mockMvc.post("/test-validation-error") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(invalidRequest)
        }.andExpect {
            status { isBadRequest() }
            jsonPath(PATH_TITLE) { value("Constraint Violation") }
            jsonPath(PATH_DETAIL) { value("Structural validation failed") }
            jsonPath("$.errors") { isArray() }
            jsonPath("$.errors[?(@.field == 'name')].userMessage") { isNotEmpty() }
            jsonPath(PATH_STATUS_CODE) { value(400) }
        }
    }

    @Test
    fun shouldUseDefaultMessageWhenIllegalArgumentExceptionMessageIsNull() {
        mockMvc.post("/test-domain-error-null")
            .andExpect {
                status { isBadRequest() }
                jsonPath(PATH_DETAIL) { value("Invalid business data") }
                jsonPath(PATH_USER_MESSAGE) { value("The data provided is invalid.") }
                jsonPath(PATH_TITLE) { value("Business Rule Violation") }
            }
    }
}