package prova.org.Mappers.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import prova.org.BaseClasses.Professor;
import prova.org.DB.DB;
import prova.org.Exceptions.EmailAlreadyUsedException;
import prova.org.Exceptions.IDAlreadyUsedException;
import prova.org.Exceptions.NoSuchProfessorException;
import prova.org.Exceptions.WrongCredentialsException;
import prova.org.Mappers.ProfessorsMapperInterface;

public class ProfessorsMapperImpl implements ProfessorsMapperInterface{

    @Override
    public Professor getByID(int ID) throws NoSuchProfessorException{
        return DB.getProfessorByID(ID);
    }

    @Override
    public ArrayList<Professor> fetchAll() {
        return DB.fetchAllProfessors();
    }

    @Override
    public void deleteByID(int ID) {
        DB.deleteProfessorByID(ID);           
    }

    @Override
    public Professor create(String name, String surname, int ID, String email, String password) throws IDAlreadyUsedException, EmailAlreadyUsedException{
        try{
            byte[] hashedPassword;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            hashedPassword = md.digest(password.getBytes());
            DB.insertProfessor(name, surname, ID, email, hashedPassword);
            return new Professor(name, surname, ID);
        }
        catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Professor auth(String email, String password) throws WrongCredentialsException {
        try{
            byte[] hashedPassword;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            hashedPassword = md.digest(password.getBytes());
            return DB.authProfessor(email, hashedPassword);
        }
        catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }
}
