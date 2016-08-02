package de.flightright.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BlogPost extends ResourceSupport {
    @Id()
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer _id;
    private String title;
    private String content;

    protected BlogPost() {
    }

    @JsonCreator
    public BlogPost(@JsonProperty("id") Integer _id, @JsonProperty String title, @JsonProperty String content) {
        this._id = _id;
        this.title = title;
        this.content = content;
    }

    @JsonGetter(value = "id")
    public Integer getAId() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
