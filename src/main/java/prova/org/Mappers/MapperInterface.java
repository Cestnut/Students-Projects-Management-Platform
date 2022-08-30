package prova.org.Mappers;

import java.util.ArrayList;

import prova.org.Exceptions.NoSuchResourceException;

public interface MapperInterface <T>{
    public T getByID(int ID) throws NoSuchResourceException;
    public ArrayList<T> fetchAll();
    public void deleteByID(int ID);
}
