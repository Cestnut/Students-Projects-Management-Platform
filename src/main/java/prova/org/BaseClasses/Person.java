package prova.org.BaseClasses;

public abstract class Person {
    private String name, surname;
    private int ID;
    
    public Person(String name, String surname, int ID){
        this.name = name;
        this.surname = surname;
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public void setID(int iD) {
        ID = iD;
    }

    public String getName(){
        return this.name;
    }
    public String getSurname(){
        return this.surname;
    }
    public int getID(){
        return this.ID;
    }
}
