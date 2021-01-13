package pl.isbrandt.model;

import java.util.Map;

public class ExceptionResponse {
    private int status;
    private String path;
    private String message;
    private String timestamp;

    public ExceptionResponse(int status, Map<String, Object> errorAttributes) {
        this.status = status;
        this.setPath(errorAttributes.get("path").toString());
        this.setMessage(errorAttributes.get("message").toString());
        this.setTimestamp(errorAttributes.get("timestamp").toString());
    }

    //Getters & Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
