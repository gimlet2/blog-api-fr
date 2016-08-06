package de.flightright.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class UploadFile extends ResourceSupport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer _id;
    private String fileName;
    private byte[] data;

    @JoinColumn(name = "blog_post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = BlogPost.class)
    private BlogPost blogPost;

    @JsonGetter("id")
    public Integer getAid() {
        return _id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @JsonIgnore
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @JsonIgnore
    public BlogPost getBlogPost() {
        return blogPost;
    }

    public void setBlogPost(BlogPost blogPost) {
        this.blogPost = blogPost;
    }

    @JsonGetter("blog_post_id")
    public Integer getBlogPostId() {
        if (blogPost != null) {
            return blogPost.getAId();
        }
        return null;
    }
}