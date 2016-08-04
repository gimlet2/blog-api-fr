package de.flightright.repositories;

import de.flightright.entities.BlogPost;
import de.flightright.entities.Comment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Integer> {
    List<Comment> findByBlogPost(BlogPost blogPost);
}
