package de.flightright.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such BlogPost")
public class BlogPostNotFoundException extends RuntimeException {
}
