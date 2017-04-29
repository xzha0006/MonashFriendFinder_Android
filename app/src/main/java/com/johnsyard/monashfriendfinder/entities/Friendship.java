package com.johnsyard.monashfriendfinder.entities;

/**
 * This is the Friendship entity
 * Created by xuanzhang on 28/04/2017.
 */

public class Friendship {
    private Integer friendshipId;
    private String startingDate;
    private String endingDate;
    private Profile studentTwoId;
    private Profile studentOneId;

    /**
     * Constructor
     */
    public Friendship(){

    }

    /**
     * Getters and setters
     */
    public Integer getFriendshipId() {
        return friendshipId;
    }

    public void setFriendshipId(Integer friendshipId) {
        this.friendshipId = friendshipId;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    public Profile getStudentTwoId() {
        return studentTwoId;
    }

    public void setStudentTwoId(Profile studentTwoId) {
        this.studentTwoId = studentTwoId;
    }

    public Profile getStudentOneId() {
        return studentOneId;
    }

    public void setStudentOneId(Profile studentOneId) {
        this.studentOneId = studentOneId;
    }
}
