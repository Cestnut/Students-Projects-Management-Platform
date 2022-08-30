package prova.org.BaseClasses;

import java.net.URL;
import prova.org.Mappers.impl.ProposalsMapperImpl;

public class Student extends Person{
    private AssignedProject project;
    private Boolean requesting;
    private ProposalsMapperImpl proposalsmapper = new ProposalsMapperImpl();

    public Student(String name, String surname, int ID){
        super(name, surname, ID);
        this.requesting = false;
    }

    public Student(String name, String surname, int ID, AssignedProject project){
        super(name, surname, ID);
        this.requesting = false;
        this.project = project;
    }


    public void assignProject(Project project, URL url){
        this.project = new AssignedProject(project, url);
    }
    public AssignedProject getProject(){
        return this.project;
    }

    public void updateProjectStatus(String requirement, Boolean value){
        this.project.updateStatus(requirement, value);
    }

    public Proposal proposeProject(String description){
        return proposalsmapper.create(this.ID, description);
    }

    public void toggleRequesting(){
        this.requesting = !requesting;
    }

    public Boolean getRequesting(){
        return requesting;
    }

    @Override
    public String toString(){
        return getID() + " " + getName() + " " + getSurname() + " " + getProject();
    }
}
