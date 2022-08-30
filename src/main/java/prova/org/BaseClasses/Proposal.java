package prova.org.BaseClasses;

public class Proposal {
    private String description;
    private int studentID, ID;

    public Proposal(String description, int studentID, int ID) {
        this.description = description;
        this.studentID = studentID;
        this.ID = ID;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDescription() {
        return description;
    }
   
    public int getStudentID() {
        return studentID;
    }

    public int getID() {
        return ID;
    }

    @Override
    public String toString(){
        return  "Student ID: " + getStudentID() + " Proposal ID: " + getID() + " Description: " + getDescription();
                 
    }
}
