package com.example.demo.error
import com.example.demo.util.findRootCause
import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
//import org.springframework.data.mapping.MappingException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime
import javax.persistence.EntityNotFoundException

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    private fun buildResponseEntity(apiError: ApiError): ResponseEntity<Any> {
        return ResponseEntity(apiError, apiError.status )
    }

    @ExceptionHandler(EntityNotFoundException::class)
    protected fun handleEntityNotFound(ex: EntityNotFoundException): ResponseEntity<Any> {
        val apiError = ApiError( HttpStatus.NOT_FOUND)
        apiError.message = ex.message
        return  buildResponseEntity(apiError)
    }

@ExceptionHandler(Throwable::class)
protected fun handleAny(ex: Throwable): ResponseEntity<Any> {
        val apiError = ApiError( HttpStatus.BAD_REQUEST)
        apiError.message = ex.findRootCause().message
        return buildResponseEntity(apiError)
    }
}

class ApiError ( val status: HttpStatus ) {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private val timestamp: LocalDateTime = LocalDateTime.now()
    var message: String? = null
}