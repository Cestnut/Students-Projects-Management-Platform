package prova.org.Mappers;

import java.net.URL;
import java.util.ArrayList;

import prova.org.BaseClasses.Project;
import prova.org.BaseClasses.Student;
import prova.org.Exceptions.EmailAlreadyUsedException;
import prova.org.Exceptions.IDAlreadyUsedException;
import prova.org.Exceptions.NoSuchStudentException;
import prova.org.Exceptions.WrongCredentialsException;

public interface StudentsMapperInterface extends MapperInterface<Student>{
    public Student create(String name, String surname, int ID, String email, String password) throws IDAlreadyUsedException, EmailAlreadyUsedException; //This method is used when a Student gets registered.
                                                        //it gets inserted into the database and an object is returned as well.
    public Student auth(String email, String password) throws WrongCredentialsException;
                                                        
    public ArrayList<Student> getRequesting(); //returns all the students requesting a project (a flag gets checked)

    //These two methods create an object of class AssignedProject, assign it to the Student in memory and in database
    public void assignProject(Student student, Project project, URL url);
    public void assignProject(int studentID, Project project, URL url) throws NoSuchStudentException;

    public void requestProject(Student student); //marks as true the requesting flag if no project is assigned yet
    public void updateProjectStatus(Student student, String requirement, Boolean value); //updates the status of a requirement of a Student's project
}
