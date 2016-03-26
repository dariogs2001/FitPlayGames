package dariogonzalez.fitplaygames;

import java.util.Date;

/**
 * Created by ChristensenKC on 10/28/2015.
 */
public class Chat {

    private String message;
    private String author;
    private Date dateTimeMessageSent;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Chat() {
    }

    Chat(String message, String author, Date dt) {
        this.message = message;
        this.author = author;
        this.dateTimeMessageSent = dt;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDateTimeMessageSent() {
        return dateTimeMessageSent;
    }
}

