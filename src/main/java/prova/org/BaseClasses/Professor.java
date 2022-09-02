package prova.org.BaseClasses;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import prova.org.Exceptions.NoSuchProjectException;
import prova.org.Exceptions.NoSuchProposalException;
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

    public void assignProject(int studentID, int projectID, String urlString) throws NoSuchProjectException, MalformedURLException, NoSuchStudentException{
        Project project = projectsmapper.getByID(projectID);
        URL url = new URL(urlString);
        studentsmapper.assignProject(studentID, project, url);
    }

    public void acceptProposal(int proposalID, ArrayList<String> requirements, String urlString) throws NoSuchProposalException, MalformedURLException, NoSuchStudentException{
        Proposal proposal = proposalsmapper.getByID(proposalID);
        URL url = new URL(urlString);
        Project newProject = createProject(proposal.getDescription(), requirements);
        studentsmapper.assignProject(proposal.getStudentID(), newProject, url);
        proposalsmapper.deleteByID(proposal.getID());
    }

    public Project createProject(String description, ArrayList<String> requirements){        
        return projectsmapper.create(description, requirements);
    }


    //The following methods have String return types because they manipulate how the message is displayed in the client-side
    public String viewProjects(){
        return projectsmapper.fetchAll().toString();
    }

    public String viewProposals(){
        return proposalsmapper.fetchAll().toString();
    }

    public String viewStudents(){

        return studentsmapper.fetchAll().toString();
    }

    public String viewRequiring(){

        return studentsmapper.getRequesting().toString();
    }

    public String getStudentByID(int studentID) throws NoSuchStudentException{
        return studentsmapper.getByID(studentID).toString();
    }


    @Override
    public String toString(){
        return (getID() + " " + getName() + " " + getSurname());
    }

}
