package prova.org.Mappers.impl;

import prova.org.BaseClasses.Project;
import prova.org.BaseClasses.Student;
import prova.org.DB.DB;
import prova.org.Exceptions.EmailAlreadyUsedException;
import prova.org.Exceptions.IDAlreadyUsedException;
import prova.org.Exceptions.NoSuchStudentException;
import prova.org.Exceptions.WrongCredentialsException;
import prova.org.Mappers.StudentsMapperInterface;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class StudentsMapperImpl implements StudentsMapperInterface {

    @Override
    public Student getByID(int ID) throws NoSuchStudentException {
        return DB.getStudentByID(ID);
    }

    @Override
    public ArrayList<Student> fetchAll() {
        return DB.fetchAllStudents();
    }

    @Override
    public void deleteByID(int ID) {
        DB.deleteStudentByID(ID);       
    }

    @Override
    public Student create(String name, String surname, int ID, String email, String password) throws IDAlreadyUsedException, EmailAlreadyUsedException{
        try{
            byte[] hashedPassword;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            hashedPassword = md.digest(password.getBytes());
            DB.insertStudent(name, surname, ID, email, hashedPassword);
            return new Student(name, surname, ID);
        }
        catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Student auth(String email, String password) throws WrongCredentialsException {
        try{
            byte[] hashedPassword;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            hashedPassword = md.digest(password.getBytes());
            return DB.authStudent(email, hashedPassword);
        }
        catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public ArrayList<Student> getRequesting() {
        return DB.getRequestingStudents();
    }

    @Override
    public void assignProject(Student student, Project project, URL url) {
        
        DB.assignProject(student, project, url);
    }

    @Override
    public void assignProject(int studentID, Project project, URL url) throws NoSuchStudentException {
        DB.assignProject(getByID(studentID), project, url);
    }

    @Override
    public void requestProject(Student student) {
        student.toggleRequesting();
        DB.requestProject(student);
    }

    @Override
    public void updateProjectStatus(Student student, String requirement, Boolean value) {
        DB.updateProjectStatus(student, requirement, value);        
    }    
}
