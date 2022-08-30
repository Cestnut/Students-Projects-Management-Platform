package prova.org.Client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.json.JSONObject;

public class Menu {
    
    private static Scanner keyboard = new Scanner(System.in);
    private static Client client;

    public static void root(){
        try{
            client = new Client(new Socket("localhost", 5555));
            while(true){
                String string = "0 - Close\n" +
                                "1 - Register as student\n" +
                                "2 - Login as student\n" +
                                "3 - Login as professor\n";

                System.out.println(string);
                int choice = keyboard.nextInt();
                
                switch(choice){
                    case 0:
                        client.close();
                        break;
                    case 1:
                        studentRegister();
                        break;
                    case 2:
                        studentAuth();
                        break;
                    case 3:
                        professorAuth();
                        break;
                    default:
                        System.out.println("Invalid choice");
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void studentRegister(){

        System.out.println("Insert name");
        keyboard.nextLine();
        String name = keyboard.nextLine();

        System.out.println("Insert surname");
        String surname = keyboard.nextLine();

        System.out.println("Insert email");
        String email = keyboard.nextLine();

        System.out.println("Insert password");
        String password = keyboard.nextLine();

        System.out.println("Insert ID");
        int ID = keyboard.nextInt();

        JSONObject response = client.studentRegister(name, surname, email, password, ID);
        System.out.println(response.getString("message"));
        int code = response.getInt("code");
        switch(code){
            case 0:
                return;
            case 1:
                studentHome();
                break;
        }
    }

    public static void studentAuth(){

        System.out.println("Insert email");
        keyboard.nextLine();
        String email = keyboard.nextLine();

        System.out.println("Insert password");
        String password = keyboard.nextLine();
    
        JSONObject response = client.studentAuth(email, password);
        System.out.println(response.getString("message"));
        int code = response.getInt("code");
        switch(code){
            case 0:
                studentHome();
                break;
            case 1:
                return;
        }        
    }

    public static void professorAuth(){
        System.out.println("Insert email");
        keyboard.nextLine();
        String email = keyboard.nextLine();

        System.out.println("Insert password");
        String password = keyboard.nextLine();

        JSONObject response = client.professorAuth(email, password);
        System.out.println(response.getString("message"));
        int code = response.getInt("code");
        switch(code){
            case 0:
                professorHome();
                break;
            case 1:
                return;
        }        

    }

    public static void studentHome(){
        while(true){
            String string = "0 - Logout\n" +
                            "1 - Request a project\n" +
                            "2 - Propose a project\n" +
                            "3 - View project\n";

            System.out.println(string);
            int choice = keyboard.nextInt();
            
            switch(choice){
                case 0:
                    return;
                case 1:
                    requireProject();
                    break;
                case 2:
                    proposeProject();
                    break;
                case 3:
                    viewProject();
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }


    public static void requireProject(){
        JSONObject response = client.requireProject();
        System.out.println(response.getString("message"));
    }

    public static void proposeProject(){

        System.out.println("Insert description");
        keyboard.nextLine();
        String description = keyboard.nextLine();

        JSONObject response = client.proposeProject(description);
        System.out.println(response.getString("message"));
    }

    public static void viewProject(){
        while(true){
            JSONObject response = client.viewProject();
            System.out.println(response.getString("message"));
            int code = response.getInt("code");
            switch(code){
                case 0:
                    System.out.println("0 - Go back to Home\n" + 
                                        "1 - Update Project Status\n");
                    int choice = keyboard.nextInt();
                    switch(choice){
                        case 0:
                            return;
                        case 1:
                            updateProject();
                            break;
                    }
                case 1:
                    return;
            }
        }
    }

    public static void updateProject(){
        try{
            System.out.println("Insert requirementName");
            keyboard.nextLine();
            String requirementName = keyboard.nextLine();
            System.out.println("Insert True or False");
            Boolean value = keyboard.nextBoolean();            
            JSONObject response = client.updateProject(requirementName, value);
            System.out.println(response.getString("message"));

        }
        catch(InputMismatchException e){
            System.out.println("Invalid value");
            return;
        }
    }

    public static void professorHome(){
        while(true){
            String string = "0 - Logout\n" +
                            "1 - View all projects\n" +
                            "2 - View all proposals\n" +
                            "3 - View all students\n" +
                            "4 - View all students requiring a project\n" +
                            "5 - Create a project\n" + 
                            "6 - Assign a project\n" +
                            "7 - Accept a proposal\n" +
                            "8 - See Student Info\n";

            System.out.println(string);
            int choice = keyboard.nextInt();
            
            switch(choice){
                case 0:
                    return;
                case 1:
                    System.out.println(client.viewProjects().getString("message"));
                    break;
                case 2:
                    System.out.println(client.viewProposals().getString("message"));
                    break;
                case 3:
                    System.out.println(client.viewStudents().getString("message"));
                    break;    
                case 4:
                    System.out.println(client.viewRequiring().getString("message"));
                    break;
                case 5:
                    createProject();
                    break;
                case 6:
                    assignProject();
                    break;
                case 7:
                    acceptProposal();
                    break;
                case 8:
                    getByStudentID();
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    public static void createProject(){

        System.out.println("Insert description");
        keyboard.nextLine();
        String description = keyboard.nextLine();

        System.out.println("Insert requirements separated by a semicolon");
        String requirementsString = keyboard.nextLine();
        String[] requirementsArray = requirementsString.split(";");
        ArrayList<String> requirements = new ArrayList<String>();
        for (String string : requirementsArray) {
            requirements.add(string);
        }

        JSONObject response = client.createProject(requirements, description);
        System.out.println(response.getString("message"));
    }

    public static void assignProject(){
        System.out.println("Insert studentID");
        keyboard.nextLine();
        int studentID = keyboard.nextInt();

        System.out.println("Insert projectID");
        int projectID = keyboard.nextInt();

        System.out.println("Insert URL");
        keyboard.nextLine();
        String URL = keyboard.nextLine();

        JSONObject response = client.assignProject(studentID, projectID, URL);
        System.out.println(response.getString("message"));
    }

    public static void acceptProposal(){


        System.out.println("Insert proposalID");
        keyboard.nextLine();
        int proposalID = keyboard.nextInt();

        System.out.println("Insert requirements separated by a semicolon");
        keyboard.nextLine();
        String requirementsString = keyboard.nextLine();
        String[] requirementsArray = requirementsString.split(";");
        ArrayList<String> requirements = new ArrayList<String>();
        for (String string : requirementsArray) {
            requirements.add(string);
        }

        System.out.println("Insert URL");
        String URL = keyboard.nextLine();

        JSONObject response = client.acceptProposal(proposalID, requirements, URL);
        System.out.println(response.getString("message"));
    }

    public static void getByStudentID(){
        System.out.println("Insert studentID");
        keyboard.nextLine();
        int studentID = keyboard.nextInt();

        
        JSONObject response = client.getStudentByID(studentID);
        System.out.println(response.getString("message"));
    }

    public static void main(String[] args) {
        root();
    }
}
