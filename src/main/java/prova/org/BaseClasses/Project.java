package prova.org.BaseClasses;

import java.util.ArrayList;

public class Project {
   
    private String description;
    private int ID;
    private ArrayList<String> requirements;
    
    public Project(){}

    public Project(String description, int ID, ArrayList<String> requirements) {
        this.description = description;
        this.ID = ID;
        this.requirements = requirements;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setRequirements(ArrayList<String> requirements) {
        this.requirements = requirements;
    }


    public String getDescription(){
        return this.description;
    }

    public int getID(){
        return this.ID;
    }

    public ArrayList<String> getRequirements(){
        return this.requirements;
    }

    @Override
    public String toString(){
        return this.ID + " " + this.description + " " + this.requirements;
    }

}  
