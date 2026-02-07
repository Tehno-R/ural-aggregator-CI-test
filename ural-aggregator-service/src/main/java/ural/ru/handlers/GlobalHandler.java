package ural.ru.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ural.ru.dto.ErrorDto;
import ural.ru.exceptions.InternalServerException;

@RestControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<?> handleException(InternalServerException e) {
        ErrorDto error = ErrorDto.builder()
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
