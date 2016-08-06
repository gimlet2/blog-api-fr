package de.flightright.repositories;

import de.flightright.entities.BlogPost;
import de.flightright.entities.UploadFile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UploadFileRepository extends CrudRepository<UploadFile, Integer> {
    List<UploadFile> findByBlogPost(BlogPost blogPost);
}
