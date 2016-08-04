package de.flightright.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "You have no right to do this action")
public class AccessDeniedException extends RuntimeException {
}
