package de.flightright;

import de.flightright.entities.BlogPost;
import de.flightright.repositories.BlogPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class BlogPostController {

    private final BlogPostRepository blogPostRepository;

    @Autowired
    public BlogPostController(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    @RequestMapping(value = "/blog_post")
    public HttpEntity<List<BlogPost>> getBlogPosts() {
        List<BlogPost> blogPosts = blogPostRepository.findAll();
        blogPosts.forEach(blogPost -> blogPost.add(linkTo(methodOn(BlogPostController.class).getBlogPostById(blogPost.getAId())).withSelfRel()));
        return new ResponseEntity<>(blogPosts, HttpStatus.OK);
    }

    @RequestMapping(value = "/blog_post/{id}")
    public HttpEntity<BlogPost> getBlogPostById(@PathVariable("id") Integer id) {
        BlogPost blogPost = blogPostRepository.findOne(id);
        blogPost.add(linkTo(methodOn(BlogPostController.class).getBlogPostById(id)).withSelfRel());
        return new ResponseEntity<>(blogPost, HttpStatus.OK);
    }
}
