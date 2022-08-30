package prova.org.BaseClasses;

import java.net.URL;
import java.util.ArrayList;

import prova.org.Exceptions.NoSuchStudentException;
import prova.org.Mappers.impl.ProjectsMapperImpl;
import prova.org.Mappers.impl.ProposalsMapperImpl;
import prova.org.Mappers.impl.StudentsMapperImpl;

public class Professor extends Person {
    
    private ProjectsMapperImpl projectsmapper = new ProjectsMapperImpl();
    private StudentsMapperImpl studentsmapper = new StudentsMapperImpl();
    private ProposalsMapperImpl proposalsmapper = new ProposalsMapperImpl();

    public Professor(String name, String surname, int ID) {
        super(name, surname, ID);
    }

    //public Student[] viewRequests(){}

    //public Project[] viewProjects(){}

    //public Proposal[] viewProposals(){}

    
    public void assignProject(Student student, Project project){
        
    }

    public void acceptProposal(Proposal proposal, ArrayList<String> requirements, URL url) throws NoSuchStudentException{
        Project newProject = createProject(proposal.getDescription(), requirements);
        studentsmapper.assignProject(proposal.getStudentID(), newProject, url);
        proposalsmapper.deleteByID(proposal.getID());

    }

    public Project createProject(String description, ArrayList<String> requirements){        
        return projectsmapper.create(description, requirements);
    }

    @Override
    public String toString(){
        return (getID() + " " + getName() + " " + getSurname());
    }

}
