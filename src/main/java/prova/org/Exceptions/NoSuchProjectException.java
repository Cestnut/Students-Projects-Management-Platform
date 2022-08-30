package prova.org.Exceptions;

public class NoSuchProjectException extends NoSuchResourceException{
    public NoSuchProjectException(String message){
        super(message);
    }
}