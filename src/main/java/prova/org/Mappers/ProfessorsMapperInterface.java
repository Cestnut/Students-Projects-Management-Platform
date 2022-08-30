package prova.org.Mappers;

import prova.org.BaseClasses.Professor;
import prova.org.Exceptions.EmailAlreadyUsedException;
import prova.org.Exceptions.IDAlreadyUsedException;
import prova.org.Exceptions.WrongCredentialsException;

public interface ProfessorsMapperInterface extends MapperInterface<Professor>{
    public Professor create(String name, String surname, int ID, String email, String password) throws IDAlreadyUsedException, EmailAlreadyUsedException; //creates a Professor and inserts it into the database and returns the object
    public Professor auth(String email, String password) throws WrongCredentialsException;
}
