/*
 * FILE:    ChannelLoader.java
 *
 * DESCRIPTION: This class creates an object
 * 				of a program with parameters.
 *
 * AUTHOR:  Elias Niko, c18eno
 *
 * CHANGES: 20200129: Initial version.
 */
package model;

/**
 * This class creates an object of a program with parameters.
 * @author Elias Niko
 * @version 1.0
 */
public class Program {
    private final String title;
    private final String description;
    private final String startTime;
    private final String endTime;
    private final String stat;
    private final int channelId;
    private final String imgURL;


    /**
     * Constructor for the program class.
     * @param id as int
     * @param title as String
     * @param desc as String
     * @param start as String
     * @param end as String
     * @param status as String
     * @param imageURL as String
     */
    public Program(final int id, final String title, final String desc, final String start,
                   final String end, final String status, final String imageURL) {
        this.channelId = id;
        this.title = title;
        this.description = desc;
        this.startTime = start;
        this.endTime = end;
        this.stat = status;
        this.imgURL = imageURL;
    }

    /**
     * Getter for image URL.
     * @return the imgURL
     */
    public String getImgURL() {
        return imgURL;
    }


    /**
     * Getter for channels ID.
     * @return the channelId
     */
    public int getChannelId() {
        return channelId;
    }

    /**
     * Getter for programs title.
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter for programs description.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for programs title.
     * @return the startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Getter for programs end time.
     * @return the endTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Getter for programs status.
     * @return the status
     */
    public String getStatus() {
        return stat;
    }
}
