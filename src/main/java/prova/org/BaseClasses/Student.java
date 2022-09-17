package prova.org.BaseClasses;

import java.net.URL;
import java.util.HashMap;

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

    public float getProjectCompletitionPercentual(){
        if(this.project == null){
            return 0;
        }
        else{
            HashMap<String, Boolean> requirements = project.getStatus();
            float counter = 0;
            float requirementsNumber = requirements.size();
            for (String key : requirements.keySet()) {
                if(requirements.get(key) == true){
                    counter += 1;
                }
            }
            return counter/requirementsNumber*100;
        }
    }

    public void updateProjectStatus(String requirement, Boolean value){
        this.project.updateStatus(requirement, value);
    }

    public Proposal proposeProject(String description){
        return proposalsmapper.create(this.getID(), description);
    }

    public void toggleRequesting(){
        this.requesting = !requesting;
    }

    public Boolean getRequesting(){
        return requesting;
    }

    @Override
    public String toString(){
        if(getProject() != null){
            return getID() + " " + getName() + " " + getSurname() + " " + getProject() + " " + String.format("%.2f", getProjectCompletitionPercentual()) + "%";
        }
        else{
            return getID() + " " + getName() + " " + getSurname();
        }
    }
}
