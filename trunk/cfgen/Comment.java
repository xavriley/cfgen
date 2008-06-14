package cfgen;

import java.awt.*;

public class Comment {

    public static enum Severity { ERROR, WARNING, FYI, LGTM };

    public Severity severity;
    public String message;
    public Pitch[] notes;

    public Comment(String newMessage, Pitch newNote, Severity s) {
				notes = new Pitch[] { newNote };
				message = newMessage;
        severity = s;
    }	
    public Comment(String newMessage, Pitch newNote) {
				this(newMessage, newNote, Severity.ERROR);
    }
    public Comment(String newMessage, Pitch[] newNotes, Severity s) {
				notes = newNotes;
				message = newMessage;
        severity = s;
    }	
    public Comment(String newMessage, Pitch[] newNotes) {
				this(newMessage, newNotes, Severity.ERROR);
    }
    public Comment(String newMessage, Severity s) {
				this(newMessage, new Pitch[0], s);
    }
    public Comment(String newMessage) {
				this(newMessage, new Pitch[0], Severity.ERROR);
    }
    public String toString() {
				return message;
    }
}
