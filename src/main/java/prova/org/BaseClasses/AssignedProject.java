package prova.org.BaseClasses;

import java.net.URL;
import java.util.HashMap;

public class AssignedProject extends Project{
    
    private HashMap<String, Boolean> status;
    private URL url;

    public AssignedProject(String description, HashMap<String, Boolean> status, int ID, URL url){
        super();
        super.setDescription(description);
        super.setID(ID);
        this.status = status;
        this.url = url;
    }

    public AssignedProject(Project project, URL url){
        super(project.getDescription(), project.getID(), project.getRequirements());
        this.url = url;
        
        HashMap<String, Boolean> status = new HashMap<String, Boolean>();
        for (String requirement : project.getRequirements()) {
            status.put(requirement, false);
        }
        this.status = status;
    }

 
    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public HashMap<String, Boolean> getStatus() {
        return status;
    }

    public void setStatus(HashMap<String, Boolean> status) {
        this.status = status;
    }

    public void updateStatus(String requirement, Boolean value) {
        this.status.put(requirement, value);       
    }


    public String toString(){
        return this.getID() + " " + this.getDescription() + " " + this.getUrl() + " " + this.getStatus();
    }


}
