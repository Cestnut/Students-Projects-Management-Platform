package prova.org.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.json.JSONObject;

public class Client {

    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    public Client(Socket socket) throws UnknownHostException, IOException{
        this.socket = socket;
        this.output = new PrintWriter(socket.getOutputStream());
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public JSONObject close() throws IOException{
        JSONObject json = new JSONObject();
        json.put("command", "close");
        JSONObject response = sendJSON(json);
        this.socket.close();
        return response;
    }

    private JSONObject sendJSON(JSONObject json){
        output.println(json);
        output.flush();

        try{
        String jsonString = this.input.readLine();
        System.out.println(jsonString);
        JSONObject response = new JSONObject(jsonString);
        return response;
        }
        catch(IOException e){
            e.printStackTrace();
            JSONObject response = new JSONObject();
            response.append("message", "Error");
            response.append("code", 100);
            return response;
        }
    }

    public JSONObject studentRegister(String name, String surname, String email, String password, int ID){
        JSONObject json = new JSONObject();
        JSONObject jsonargs = new JSONObject();
        jsonargs.put("email", email);
        jsonargs.put("password", password);
        jsonargs.put("name", name);
        jsonargs.put("surname", surname);
        jsonargs.put("ID", ID);

        json.put("command", "studentRegister");
        json.put("args", jsonargs);

        return sendJSON(json);
    }

    public JSONObject studentAuth(String email, String password){
        JSONObject json = new JSONObject();
        JSONObject jsonargs = new JSONObject();
        jsonargs.put("email", email);
        jsonargs.put("password", password);
        
        json.put("command", "studentAuth");
        json.put("args", jsonargs);

        return sendJSON(json);
    }

    public JSONObject requireProject(){
        JSONObject json = new JSONObject();
        json.put("command", "requireProject");
        return sendJSON(json);
    }

    public JSONObject proposeProject(String description){
        JSONObject json = new JSONObject();
        JSONObject jsonargs = new JSONObject();
        jsonargs.put("description", description);

        json.put("command", "proposeProject");
        json.put("args", jsonargs);
        return sendJSON(json);
    }

    public JSONObject viewProject(){
        JSONObject json = new JSONObject();
        json.put("command", "viewProject");
        return sendJSON(json);
    }

    public JSONObject updateProject(String requirementName, Boolean value){
        JSONObject json = new JSONObject();
        JSONObject jsonargs = new JSONObject();
        jsonargs.put("requirementName", requirementName);
        jsonargs.put("value", value);


        json.put("command", "updateProject");
        json.put("args", jsonargs);
        return sendJSON(json);
    }

    public JSONObject professorAuth(String email, String password){
        JSONObject json = new JSONObject();
        JSONObject jsonargs = new JSONObject();
        jsonargs.put("email", email);
        jsonargs.put("password", password);


        json.put("command", "professorAuth");
        json.put("args", jsonargs);
        return sendJSON(json);
    }

    public JSONObject createProject(ArrayList<String> requirements, String description){
        JSONObject json = new JSONObject();
        JSONObject jsonargs = new JSONObject();
        jsonargs.put("description", description);
        jsonargs.put("requirements", requirements);


        json.put("command", "createProject");
        json.put("args", jsonargs);
        return sendJSON(json);
    }

    public JSONObject assignProject(int studentID, int projectID, String URL){
        JSONObject json = new JSONObject();
        JSONObject jsonargs = new JSONObject();
        jsonargs.put("studentID", studentID);
        jsonargs.put("projectID", projectID);
        jsonargs.put("URL", URL);


        json.put("command", "assignProject");
        json.put("args", jsonargs);
        return sendJSON(json);
    }

    public JSONObject acceptProposal(int proposalID, ArrayList<String> requirements, String URL){
        JSONObject json = new JSONObject();
        JSONObject jsonargs = new JSONObject();
        jsonargs.put("proposalID", proposalID);
        jsonargs.put("requirements", requirements);
        jsonargs.put("URL", URL);


        json.put("command", "acceptProposal");
        json.put("args", jsonargs);
        return sendJSON(json);
    }

    public JSONObject viewStudents(){
        JSONObject json = new JSONObject();
        json.put("command", "viewStudents");
        return sendJSON(json);
    }

    public JSONObject viewRequiring(){
        JSONObject json = new JSONObject();
        json.put("command", "viewRequiring");
        return sendJSON(json);
    }

    public JSONObject viewProjects(){
        JSONObject json = new JSONObject();
        json.put("command", "viewProjects");
        return sendJSON(json);
    }

    public JSONObject viewProposals(){
        JSONObject json = new JSONObject();
        json.put("command", "viewProposals");
        return sendJSON(json);
    }

    public JSONObject getStudentByID(int studentID){
        JSONObject json = new JSONObject();
        JSONObject jsonargs = new JSONObject();
        jsonargs.put("studentID", studentID);
        json.put("command", "getStudentByID");
        json.put("args", jsonargs);
        return sendJSON(json);
    }
}
