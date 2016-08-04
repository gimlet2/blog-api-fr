package de.flightright.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Comment extends ResourceSupport {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer _id;

    @JoinColumn(name = "author")
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    private User author;

    @JoinColumn(name = "blog_post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = BlogPost.class)
    private BlogPost blogPost;

    private String content;

    @Column(name = "creation_date")
    private Date creationDate = new Date();

    public Comment() {
    }

    @JsonCreator
    public Comment(@JsonProperty("content") String content) {
        this.content = content;
    }

    public Integer get_id() {
        return _id;
    }

    public User getAuthor() {
        return author;
    }

    @JsonIgnore
    public void setAuthor(User author) {
        this.author = author;
    }

    @JsonIgnore
    public BlogPost getBlogPost() {
        return blogPost;
    }

    public void setBlogPost(BlogPost blogPost) {
        this.blogPost = blogPost;
    }

    public String getContent() {
        return content;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    @JsonGetter("blog_post_id")
    public Integer getBlogPostId() {
        if (blogPost != null) {
            return blogPost.getAId();
        }
        return null;
    }

    @JsonGetter("author_id")
    public Integer getAuthorId() {
        if (author != null) {
            return author.getAid();
        }
        return null;
    }

    @JsonGetter("author_name")
    public String getAuthorName() {
        if (author != null) {
            return author.getUsername();
        }
        return null;
    }
}
