package algoQuePedir.controller

import algoQuePedir.errors.BusinessException
import algoQuePedir.errors.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

// Al dispararse una runtimeException este handler la procesa y retorna un ResponseEntity adecuado
@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<String> {
        // We use the status defined in the annotation, but explicitly set the body
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ex.message)
    }


    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ex.message)
    }
}