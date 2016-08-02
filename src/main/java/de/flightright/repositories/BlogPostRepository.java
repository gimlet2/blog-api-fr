package de.flightright.repositories;

import de.flightright.entities.BlogPost;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BlogPostRepository extends CrudRepository<BlogPost, Integer> {
    List<BlogPost> findAll();
}
