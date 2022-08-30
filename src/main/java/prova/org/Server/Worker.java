package prova.org.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import prova.org.BaseClasses.Professor;
import prova.org.BaseClasses.Project;
import prova.org.BaseClasses.Proposal;
import prova.org.BaseClasses.Student;
import prova.org.Exceptions.EmailAlreadyUsedException;
import prova.org.Exceptions.IDAlreadyUsedException;
import prova.org.Exceptions.NoSuchProjectException;
import prova.org.Exceptions.NoSuchProposalException;
import prova.org.Exceptions.NoSuchStudentException;
import prova.org.Exceptions.WrongCredentialsException;
import prova.org.Mappers.impl.ProfessorsMapperImpl;
import prova.org.Mappers.impl.ProjectsMapperImpl;
import prova.org.Mappers.impl.ProposalsMapperImpl;
import prova.org.Mappers.impl.StudentsMapperImpl;

public class Worker implements Runnable{

    private Student student = null;
    private Professor professor = null;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private StudentsMapperImpl studentsmapper = new StudentsMapperImpl();
    private ProposalsMapperImpl proposalsmapper = new ProposalsMapperImpl();
    private ProfessorsMapperImpl professorsmapper = new ProfessorsMapperImpl();
    private ProjectsMapperImpl projectsmapper = new ProjectsMapperImpl();


    public Worker(Socket socket) throws IOException{
        this.socket = socket;
        this.output = new PrintWriter(socket.getOutputStream());
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    //public studentAuth(args) //email, password
    public JSONObject studentAuth(JSONObject args){
        String email = args.getString("email");
        String password = args.getString("password");

        JSONObject response = new JSONObject();

        try{
            this.student = studentsmapper.auth(email, password);
            response.put("code", 0);
            response.put("message", "Login succesful");
        }
        catch(WrongCredentialsException e){
            e.printStackTrace();
            response.put("code", 1);
            response.put("message", "Wrong e-mail or password");
        }
    
        return response;
    }

    //public studentRegister(args) //name, surname, id, email, password
    public JSONObject studentRegister(JSONObject args){
        String name = args.getString("name");
        String surname = args.getString("surname");
        String email = args.getString("email");
        String password = args.getString("password");
        int ID = args.getInt("ID");

        JSONObject response = new JSONObject();

        try{
            this.student = studentsmapper.create(name, surname, ID, email, password);
            System.out.println(student);
            response.put("code", 1);
            response.put("message", "Succesfully registered");
        }
        catch(IDAlreadyUsedException e){
            e.printStackTrace();
            response.put("code", 0);
            response.put("message", "There already exists a Student with this ID");
        }
        catch(EmailAlreadyUsedException e){
            e.printStackTrace();
            response.put("code", 0);
            response.put("message", "There already exists a Student with this email");
        }

        return response;
    }

    //public requireProject()
    public JSONObject requireProject(){

        JSONObject response = new JSONObject();
        response.put("code", 0);

        if(student == null){
            response.put("message", "You must be logged in as student");
        }
        else if(student.getProject() != null){
            response.put("message", "You already have an assigned project");
        }
        else{
            studentsmapper.requestProject(student);
            if(student.getRequesting() == true){
                response.put("message", "Requesting project");
            }
            else{
                response.put("message", "Request removed");
            }
        }

        return response;
    }

    //public proposeProject(args) //description
    public JSONObject proposeProject(JSONObject args){
        
        String description = args.getString("description");
        JSONObject response = new JSONObject();
        response.put("code", 0);

        if(student == null){
            response.put("message", "You must be logged in as student");
        }
        else if(student.getProject() != null){
            response.put("message", "You already have an assigned project");
        }
        else if(proposalsmapper.getByStudentID(student.getID()) != null){
            response.put("message", "You've already proposed a project");
        }
        else{
            student.proposeProject(description);
            response.put("message", "Project proposed succesfully");
        }
    
        return response;
    }


    public JSONObject viewProject(){
        JSONObject response = new JSONObject();
        if(student != null){
            if(student.getProject() != null){
                response.put("message", student.getProject());
                response.put("code", 0);
            }
            else{
                response.put("message", "You don't have an assigned project");
                response.put("code", 1);
            }
        }
        else{
            response.put("message", "You must be a student");
            response.put("code", 1);
        }
        return response;
    }
    
    //public updateProject(args) //requirementName, value
    public JSONObject updateProject(JSONObject args){
        
        String requirement = args.getString("requirementName");
        Boolean value = args.getBoolean("value");
        JSONObject response = new JSONObject();
        response.put("code", 0);
        if(student == null){
            response.put("message", "You must be logged in as student");
        }
        else if(student.getProject() == null){
            response.put("message", "You don't have an assigned project");
        }
        else if(!(student.getProject().getStatus().containsKey(requirement))){
            response.put("message", "Your project doesnt have this requirementName");
        }
        else{
            studentsmapper.updateProjectStatus(student, requirement, value);
            response.put("message", "Project updated succesfully");
        }
        
        return response;
    }

    //public professorAuth(args) //email, password
    public JSONObject professorAuth(JSONObject args){
        String email = args.getString("email");
        String password = args.getString("password");
        JSONObject response = new JSONObject();

        try{
            this.professor = professorsmapper.auth(email, password);
            response.put("code", 0);
            response.put("message", "Succesfully logged in");
        }
        catch(WrongCredentialsException e){
            e.printStackTrace();
            response.put("code", 1);
            response.put("message", "Wrong email or password");
        }
        return response;
    }

    //public createProject(args) //requirements, description
    public JSONObject createProject(JSONObject args){
        
        JSONObject response = new JSONObject();
        response.put("code", 0);

        if(professor != null){
            String description = args.getString("description");
            
            JSONArray jsonrequirements = args.getJSONArray("requirements");
            
            ArrayList<String> requirements = new ArrayList<String>();
            for (Object object : jsonrequirements) {
                requirements.add(object.toString());
            }

            professor.createProject(description, requirements);
            response.put("message", "Project created succesfully");
        }   
        else{
            response.put("message", "You must be a professor to do this action");
        }
        return response;
    }

    //public assignProject(args) //studentID, projectID, URL
    public JSONObject assignProject(JSONObject args){
        int studentID = args.getInt("studentID");
        int projectID = args.getInt("projectID");
        String urlString = args.getString("URL");
        
        JSONObject response = new JSONObject();
        response.put("code", 0);

        try{
            Project project = projectsmapper.getByID(projectID);
            URL url = new URL(urlString);
            
            if(professor != null){
                studentsmapper.assignProject(studentID, project, url);
                response.put("message", "Project assigned");
            }
            else{
                response.put("message", "You must be a professor to do this action");
            }
        }
        catch(NoSuchStudentException e){
            e.printStackTrace();
            response.put("message", "There isn't a student with ID: " + studentID);
        }
        catch(NoSuchProjectException e){
            e.printStackTrace();
            response.put("message", "There isn't a project with ID: " + projectID);
        }
        catch(MalformedURLException e){
            e.printStackTrace();
            response.put("message", "Invalid URL: "+urlString);
        }
        return response;
    }
 

    //public acceptProposal(args) //proposalID, requirements[]
    public JSONObject acceptProposal(JSONObject args){
        int proposalID = args.getInt("proposalID");

        JSONObject response = new JSONObject();
        response.put("code", 0);

        try{
            Proposal proposal = proposalsmapper.getByID(proposalID);
            URL url = new URL(args.getString("URL"));
            
            JSONArray jsonrequirements = args.getJSONArray("requirements");
            ArrayList<String> requirements = new ArrayList<String>();
            for (Object object : jsonrequirements) {
                requirements.add(object.toString());
            }
        
            if(professor != null){
                professor.acceptProposal(proposal, requirements, url);
                response.put("message", "Proposal accepted");
            }
            else{
                response.put("message", "You must be a professor to do this action");
            }
        }
        catch(NoSuchProposalException e){
            e.printStackTrace();
            response.put("message", "There isn't a proposal with ID: " + proposalID);
        }
        catch(NoSuchStudentException e){
            e.printStackTrace();
            response.put("message", "There isn't a valid student associated with this proposal");
        }
        catch(MalformedURLException e){
            e.printStackTrace();
            response.put("message", "Invalid URL");
        }
        return response;
    }
    
    //public viewProjects()
    public JSONObject viewProjects(){

        JSONObject response = new JSONObject();
        response.put("code", 0);

        if(this.professor != null){
            response.put("message", projectsmapper.fetchAll().toString());
        }
        else{
            response.put("message", "You must be a professor");
        }
        return response;
    }

    //public viewProposals()
    public JSONObject viewProposals(){
        JSONObject response = new JSONObject();
        response.put("code", 0);

        if(this.professor != null){
            response.put("message", proposalsmapper.fetchAll().toString());
        }
        else{
            response.put("message", "You must be a professor");
        }
        return response;   
    }

    public JSONObject viewStudents(){

        JSONObject response = new JSONObject();
        response.put("code", 0);

        if(this.professor != null){
            response.put("message", studentsmapper.fetchAll().toString());
        }
        else{
            response.put("message", "You must be a professor");
        }
        return response;
    }


    //public viewRequiring()
    public JSONObject viewRequiring(){

        JSONObject response = new JSONObject();
        response.put("code", 0);

        if(this.professor != null){
            response.put("message", studentsmapper.getRequesting().toString());
        }
        else{
            response.put("message", "You must be a professor");
        }
        return response;
    }

    public JSONObject getStudentByID(JSONObject args){
        int studentID = args.getInt("studentID");
        JSONObject response = new JSONObject();

        try{
            response.put("message", studentsmapper.getByID(studentID).toString());
        }
        catch(NoSuchStudentException e){
            response.put("message", "There isn't a student with ID " + studentID);
        }
        return response;
    }

    public void run(){
        Boolean running = true;
        while(running){
            try{
            String jsonString = this.input.readLine();
            JSONObject json = new JSONObject(jsonString);
            JSONObject args = new JSONObject();
            System.out.println(json);
            String command = json.getString("command");
            
            if(json.has("args")){
                args = json.getJSONObject("args");
            }
            
            JSONObject result = null;
            
            switch (command) {
                case "studentRegister":
                    result = this.studentRegister(args);
                    break;
                case "studentAuth":
                    result = this.studentAuth(args);
                    break;
                case "requireProject":
                    result = this.requireProject();
                    break;
                case "proposeProject":
                    result = this.proposeProject(args);
                    break;
                case "viewProject":
                    result = this.viewProject();
                    break;
                case "updateProject":
                    result = this.updateProject(args);
                    break;
                case "professorAuth":
                    result = this.professorAuth(args);
                    break;
                case "createProject":
                    result = this.createProject(args);
                    break;
                case "assignProject":
                    result = this.assignProject(args);
                    break;
                case "acceptProposal":
                    result = this.acceptProposal(args);
                    break;
                case "viewProjects":
                    result = this.viewProjects();
                    break;
                case "viewProposals":
                    result = this.viewProposals();
                    break;
                case "viewStudents":
                    result = this.viewStudents();
                    break;
                case "viewRequiring":
                    result = this.viewRequiring();
                    break;
                case "getStudentByID":
                    result = this.getStudentByID(args);
                    break;
                case "close":
                    running = false;
                    result = new JSONObject().append("message", "Connection closed");
                default:
                    break;
                }
                this.output.println(result);
                this.output.flush();
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        return;
    }   
}
