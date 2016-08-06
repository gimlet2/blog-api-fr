package de.flightright.controller;

import de.flightright.entities.User;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class RootController {

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public HttpEntity<ResourceSupport> getRoot(@AuthenticationPrincipal final User user) {
        ResourceSupport response = new ResourceSupport();
        response.add(linkTo(methodOn(RootController.class).getRoot(null)).withSelfRel());
        response.add(linkTo(methodOn(BlogPostController.class).getBlogPosts()).withRel("blog_posts"));
        if (user == null) {
            response.add(linkTo(methodOn(UserController.class).signUp(null)).withRel("signup"));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
