package hexlet.code.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TaskStatusAlreadyExistsException extends RuntimeException {
    public TaskStatusAlreadyExistsException(String message) {
        super(message);
    }
}
