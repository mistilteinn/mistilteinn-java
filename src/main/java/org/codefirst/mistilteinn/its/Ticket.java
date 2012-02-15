package org.codefirst.mistilteinn.its;

/**
 * Ticket model.
 */
public class Ticket {
    /** ticket identifier. */
    private Integer id;

    /** ticket subject. */
    private String subject;

    /**
     * Default Constructor.
     */
    public Ticket() {
        super();
    }

    /**
     * Constructor.
     * @param id the id
     * @param subject the subject
     */
    public Ticket(Integer id, String subject) {
        super();
        this.id = id;
        this.subject = subject;
    }

    /**
     * Get the id.
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the id.
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Get the subject.
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Set the subject.
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

}
