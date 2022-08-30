package prova.org.Exceptions;

public class NoSuchStudentException extends NoSuchResourceException{
    public NoSuchStudentException(String message){
        super(message);
    }
}
