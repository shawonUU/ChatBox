package com.example.chatbox.fragmentprant;

public class COntacts {
    String fname,lname,status,ppictur;
    public COntacts(){

    }
    public COntacts(String fname, String lname, String status, String ppictur) {
        this.fname = fname;
        this.lname = lname;
        this.status = status;
        this.ppictur = ppictur;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPpictur() {
        return ppictur;
    }

    public void setPpictur(String ppictur) {
        this.ppictur = ppictur;
    }
}
