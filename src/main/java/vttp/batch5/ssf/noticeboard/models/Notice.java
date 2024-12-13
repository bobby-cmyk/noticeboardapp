package vttp.batch5.ssf.noticeboard.models;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class Notice {
    @NotEmpty(message="Title cannot be empty")
    @Size(min=3, max=128, message="Length must be between 3 and 123 characters")
    private String title;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message="Must be a well-formed email address")
    private String poster;

    @Future(message="Post date must be future dates")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate postDate;

    @Size(min=1, message="Must include at least 1 category")
    private List<String> categories;

    @NotEmpty(message="Content of notice cannot be empty")
    private String text;

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getPoster() {return poster;}
    public void setPoster(String poster) {this.poster = poster;}

    public LocalDate getPostDate() {return postDate;}
    public void setPostDate(LocalDate postDate) {this.postDate = postDate;}
    
    public long getPostDateEpoch() {
        ZoneId zoneId = ZoneId.systemDefault();
        long postDateEpoch = postDate.atStartOfDay(zoneId).toInstant().toEpochMilli();
        return postDateEpoch;
    }

    public List<String> getCategories() {return categories;}
    public void setCategories(List<String> categories) {this.categories = categories;}

    public String getText() {return text;}
    public void setText(String text) {this.text = text;}
}
