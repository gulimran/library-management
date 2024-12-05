package imran.exercise.library.management.exception.handler;

import imran.exercise.library.management.exception.BookAlreadyExistsException;
import imran.exercise.library.management.exception.BookNotFoundException;
import imran.exercise.library.management.exception.BookUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BookNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected String bookNotFoundException(BookNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(value = {BookAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected String bookAlreadyExistsException(BookAlreadyExistsException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(value = {BookUnavailableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected String bookNotAvailableException(BookUnavailableException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(value = {RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected String runtimeException(RuntimeException ex) {
        return ex.getMessage();
    }
}
