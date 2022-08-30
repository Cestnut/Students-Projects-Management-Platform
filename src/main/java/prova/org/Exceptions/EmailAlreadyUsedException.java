package prova.org.Exceptions;

public class EmailAlreadyUsedException extends Exception{
    public EmailAlreadyUsedException(String message){
        super(message);
    }
}
