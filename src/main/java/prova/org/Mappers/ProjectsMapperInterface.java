package prova.org.Mappers;

import java.util.ArrayList;

import prova.org.BaseClasses.Project;

public interface ProjectsMapperInterface extends MapperInterface<Project>{
    public Project create(String description, ArrayList<String> requirements); //creates a Proposal and inserts it into the database and returns the object
}
