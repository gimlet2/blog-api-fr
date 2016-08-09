package de.flightright.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class BlogPost extends ResourceSupport {
    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer _id;
    private String title;
    private String content;
    @JoinColumn(name = "owner")
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    private User owner;

    protected BlogPost() {
    }

    @JsonCreator(mode = JsonCreator.Mode.DEFAULT)
    public BlogPost(@JsonProperty("id") Integer _id, @JsonProperty("title") String title, @JsonProperty("content") String content) {
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

    @JsonIgnore
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @JsonGetter("author_id")
    public Integer getAuthorId() {
        if (owner != null) {
            return owner.getAid();
        }
        return null;
    }

    @JsonGetter("author_name")
    public String getAuthorName() {
        if (owner != null) {
            return owner.getUsername();
        }
        return null;
    }
}
