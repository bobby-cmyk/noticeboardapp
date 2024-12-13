package vttp.batch5.ssf.noticeboard.models;

import org.springframework.http.HttpStatusCode;

public class Response {
    
    private HttpStatusCode statusCode;

    private String content;

    public HttpStatusCode getStatusCode() {return statusCode;}
    public void setStatusCode(HttpStatusCode statusCode) {this.statusCode = statusCode;}

    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}
}
