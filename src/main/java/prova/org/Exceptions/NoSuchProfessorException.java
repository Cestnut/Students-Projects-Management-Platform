package prova.org.Exceptions;

public class NoSuchProfessorException extends NoSuchResourceException{
    public NoSuchProfessorException(String message){
        super(message);
    }
}
