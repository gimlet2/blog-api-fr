package de.flightright.controller;

import de.flightright.entities.BlogPost;
import de.flightright.entities.Comment;
import de.flightright.entities.User;
import de.flightright.exception.AccessDeniedException;
import de.flightright.exception.BlogPostNotFoundException;
import de.flightright.exception.CommentNotFoundException;
import de.flightright.repositories.BlogPostRepository;
import de.flightright.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class BlogPostController {

    private final BlogPostRepository blogPostRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public BlogPostController(BlogPostRepository blogPostRepository, CommentRepository commentRepository) {
        this.blogPostRepository = blogPostRepository;
        this.commentRepository = commentRepository;
    }

    @RequestMapping(value = "/blog_post", method = RequestMethod.GET)
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

    @RequestMapping(value = "/blog_post/{id}/comment", method = RequestMethod.GET)
    public HttpEntity<List<Comment>> getBlogPostComments(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(
                commentRepository.findByBlogPost(blogPostRepository.findOne(id)),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/blog_post/{id}/comment", method = RequestMethod.POST, consumes = "application/json")
    public HttpEntity<Comment> createComment(@PathVariable("id") Integer id, @RequestBody Comment comment, @AuthenticationPrincipal final User user) {
        BlogPost blogPost = blogPostRepository.findOne(id);
        if (blogPost == null) {
            throw new BlogPostNotFoundException();
        }
        comment.setBlogPost(blogPost);
        comment.setAuthor(user);
        return new ResponseEntity<>(commentRepository.save(comment), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/blog_post/{id}/comment/{comment_id}", method = RequestMethod.DELETE)
    public HttpEntity<Void> createComment(@PathVariable("id") Integer id, @PathVariable("comment_id") Integer commentId, @AuthenticationPrincipal final User user) {
        BlogPost blogPost = blogPostRepository.findOne(id);
        if (blogPost == null) {
            throw new BlogPostNotFoundException();
        }
        if (blogPost.getOwner().getAid().equals(user.getAid())) {
            if (!commentRepository.exists(id)) {
                throw new CommentNotFoundException();
            }
            commentRepository.delete(commentId);
        } else {
            throw new AccessDeniedException();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/blog_post", method = RequestMethod.POST, consumes = "application/json")
    public HttpEntity<BlogPost> createBlogPosts(@RequestBody BlogPost blogPost, @AuthenticationPrincipal final User user) {
        blogPost.setOwner(user);
        return new ResponseEntity<>(blogPostRepository.save(blogPost), HttpStatus.CREATED);
    }

}
