package prova.org.Server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import prova.org.DB.DB;
import prova.org.Mappers.impl.ProfessorsMapperImpl;

public class Server {
    private static int port;
    
    public static void readPort(){
        Properties prop = new Properties();
        String filename = "src\\main\\java\\prova\\org\\Server\\server.conf";
        try(FileInputStream fis = new FileInputStream(filename)){
            prop.load(fis);
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        Server.port = Integer.parseInt(prop.getProperty("port"));
    }
    
    public static void main(String[] args) {

        Server.readPort();
        try{
            DB.readSettings();
            ServerSocket serversocket = new ServerSocket(Server.port);
            ProfessorsMapperImpl professorsmapper = new ProfessorsMapperImpl();
            professorsmapper.create("Jhonny", "English", 123, "email", "password");

        /*  MongoCollection<Document> students = DB.db.getCollection("students");
            Document student1 = students.find(new Document("name", "Giovanni")).first();
            System.out.println(student1.getString("name")+ " " + student1.getString("surname")); */
            while(true){
                System.out.println("Listening for connection\n");
                Socket socket = serversocket.accept();
                System.out.println("Connection accepted\n");
                Thread worker = new Thread(new Worker(socket));
                worker.start();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
