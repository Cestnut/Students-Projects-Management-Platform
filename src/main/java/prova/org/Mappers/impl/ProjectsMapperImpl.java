package prova.org.Mappers.impl;

import java.util.ArrayList;

import prova.org.BaseClasses.Project;
import prova.org.DB.DB;
import prova.org.Exceptions.NoSuchProjectException;
import prova.org.Mappers.ProjectsMapperInterface;

public class ProjectsMapperImpl implements ProjectsMapperInterface {

    @Override
    public Project getByID(int ID) throws NoSuchProjectException{
        return DB.getProjectByID(ID);
    }

    @Override
    public ArrayList<Project> fetchAll() {
        return DB.fetchAllProjects();
    }

    @Override
    public void deleteByID(int ID) {
        DB.deleteProjectByID(ID);   
    }

    @Override
    public Project create(String description, ArrayList<String> requirements) {
        return DB.insertProject(description, requirements);
    }
    
}
