package com.goodinitiative.vachan_chalval;

/**
 * Created by mahesh on 21-Dec-17.
 */

public class Book {
    String bookname,author,charge,yop;
    String coveruri;
    String uid;

    public Book() {
    }

    public Book(String bookname, String author, String charge, String yop, String coveruri, String uid) {
        this.bookname = bookname;
        this.author = author;
        this.charge = charge;
        this.yop = yop;
        this.coveruri = coveruri;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getBookname() {
        return bookname;
    }

    public String getAuthor() {
        return author;
    }

    public String getCharge() {
        return charge;
    }

    public String getYop() {
        return yop;
    }

    public String getCoveruri() {
        return coveruri;
    }
}
