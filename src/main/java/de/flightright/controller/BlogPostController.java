package de.flightright.controller;

import de.flightright.entities.BlogPost;
import de.flightright.entities.Comment;
import de.flightright.entities.UploadFile;
import de.flightright.entities.User;
import de.flightright.exception.AccessDeniedException;
import de.flightright.exception.BlogPostNotFoundException;
import de.flightright.exception.CommentNotFoundException;
import de.flightright.exception.FileNotFoundException;
import de.flightright.repositories.BlogPostRepository;
import de.flightright.repositories.CommentRepository;
import de.flightright.repositories.UploadFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class BlogPostController {

    private final BlogPostRepository blogPostRepository;
    private final CommentRepository commentRepository;
    private final UploadFileRepository uploadFileRepository;

    @Autowired
    public BlogPostController(BlogPostRepository blogPostRepository, CommentRepository commentRepository, UploadFileRepository uploadFileRepository) {
        this.blogPostRepository = blogPostRepository;
        this.commentRepository = commentRepository;
        this.uploadFileRepository = uploadFileRepository;
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
        blogPost.add(linkTo(methodOn(BlogPostController.class).getBlogPostComments(id, null)).withRel("comments"));
        return new ResponseEntity<>(blogPost, HttpStatus.OK);
    }

    @RequestMapping(value = "/blog_post/{id}/comment", method = RequestMethod.GET)
    public HttpEntity<List<Comment>> getBlogPostComments(@PathVariable("id") Integer id, @AuthenticationPrincipal final User user) {
        BlogPost blogPost = getBlogPost(id);
        List<Comment> comments = commentRepository.findByBlogPost(blogPost);
        if (user != null && blogPost.getOwner().getAid().equals(user.getAid())) {
            comments.forEach(c -> c.add(linkTo(methodOn(BlogPostController.class).deleteComment(c.getBlogPostId(), c.getAid(), null)).withRel("delete")));
        }
        return new ResponseEntity<>(
                comments,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/blog_post/{id}/comment", method = RequestMethod.POST, consumes = "application/json")
    public HttpEntity<Comment> createComment(@PathVariable("id") Integer id, @RequestBody Comment comment, @AuthenticationPrincipal final User user) {
        BlogPost blogPost = getBlogPost(id);
        comment.setBlogPost(blogPost);
        comment.setAuthor(user);
        return new ResponseEntity<>(commentRepository.save(comment), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/blog_post/{id}/comment/{comment_id}", method = RequestMethod.DELETE)
    public HttpEntity<Void> deleteComment(@PathVariable("id") Integer id, @PathVariable("comment_id") Integer commentId, @AuthenticationPrincipal final User user) {
        BlogPost blogPost = getBlogPost(id);
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

    @RequestMapping(value = "/blog_post/{id}/file", method = RequestMethod.GET)
    public HttpEntity<List<UploadFile>> getFiles(@PathVariable("id") Integer id) {
        BlogPost blogPost = getBlogPost(id);
        List<UploadFile> files = uploadFileRepository.findByBlogPost(blogPost);
        files.forEach(f -> f.add(linkTo(methodOn(BlogPostController.class).getFile(f.getBlogPostId(), f.getAid())).withRel("content")));
        return new ResponseEntity<>(
                files,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/blog_post/{id}/file", method = RequestMethod.POST)
    public HttpEntity<Void> handleFileUpload(@PathVariable("id") Integer id, @RequestParam MultipartFile file, @AuthenticationPrincipal final User user) throws IOException {
        BlogPost blogPost = getBlogPost(id);
        if (!blogPost.getOwner().getAid().equals(user.getAid())) {
            throw new AccessDeniedException();
        }
        if (file != null) {
            UploadFile uploadFile = new UploadFile();
            uploadFile.setFileName(file.getOriginalFilename());
            uploadFile.setData(file.getBytes());
            uploadFile.setBlogPost(blogPost);
            uploadFileRepository.save(uploadFile);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/blog_post/{id}/file/{file_id}", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> getFile(@PathVariable("id") Integer id, @PathVariable("file_id") Integer fileId) {
        HttpHeaders header = new HttpHeaders();
        UploadFile file = uploadFileRepository.findOne(fileId);
        if (file == null) {
            throw new FileNotFoundException();
        }
        header.set("Content-Disposition",
                "attachment; filename=" + file.getFileName().replace(" ", "_"));
        return new HttpEntity<>(file.getData(), header);
    }

    private BlogPost getBlogPost(Integer id) {
        BlogPost blogPost = blogPostRepository.findOne(id);
        if (blogPost == null) {
            throw new BlogPostNotFoundException();
        }
        return blogPost;
    }

}
