package prova.org.DB;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;

import prova.org.BaseClasses.AssignedProject;
import prova.org.BaseClasses.Professor;
import prova.org.BaseClasses.Project;
import prova.org.BaseClasses.Proposal;
import prova.org.BaseClasses.Student;
import prova.org.Exceptions.EmailAlreadyUsedException;
import prova.org.Exceptions.IDAlreadyUsedException;
import prova.org.Exceptions.NoSuchProfessorException;
import prova.org.Exceptions.NoSuchProjectException;
import prova.org.Exceptions.NoSuchProposalException;
import prova.org.Exceptions.NoSuchStudentException;
import prova.org.Exceptions.WrongCredentialsException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.bson.Document;
import org.bson.conversions.Bson;

public class DB {
    
    public static MongoDatabase db;

    public static void readSettings(){
        Properties prop = new Properties();
        String filename = "src\\main\\java\\prova\\org\\DB\\db.conf";
        try(FileInputStream fis = new FileInputStream(filename)){
            prop.load(fis);
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        String URI;
        URI = "mongodb://"+prop.getProperty("IP")+":"+prop.getProperty("port");
        MongoClient mongoClient = MongoClients.create(URI);
        DB.db = mongoClient.getDatabase("databaseName");
    }

    //Converts a Document Student to Class Student. This piece of code is used in two different places, since it's long it was made in a method itself.
    private static Student DocumentToStudent(Document student){
        String name = student.getString("name");
        String surname = student.getString("surname");
        int ID = student.getInteger("ID");
        Document project = student.get("project", Document.class);
        if(project != null){
            try{
                String projectDescription = project.getString("description");
                URL projectURL = new URL(project.getString("URL"));
                int projectID = project.getInteger("ID");
                ArrayList<Document> projectRequirements = new ArrayList<Document>(project.getList("requirements", Document.class));
                HashMap<String, Boolean> requirements = new HashMap<String, Boolean>();

                for (Document document : projectRequirements) {
                    String requirementname = document.getString("requirementname");
                    Boolean value = document.getBoolean("value");
                    requirements.put(requirementname, value);
                }
                return new Student(name, surname, ID, new AssignedProject(projectDescription, requirements, projectID, projectURL));
            }
            catch(MalformedURLException e){
            e.printStackTrace();
            return null;
        }
        }
        else{
            return new Student(name, surname, ID);
        }
    }

    public static Student getStudentByID(int ID) throws NoSuchStudentException {
        MongoCollection<Document> students = DB.db.getCollection("students");
        Document student = students.find(new Document("ID", ID)).first();        
        if(student == null){
            throw new NoSuchStudentException("There isn't a student with ID: " + ID);
        }
        else{
            return DocumentToStudent(student);
        }
    }


    public static void insertStudent(String name, String surname, int ID, String email, byte[] hashedPassword) throws NoSuchAlgorithmException, IDAlreadyUsedException, EmailAlreadyUsedException{
        MongoCollection<Document> studentsCollection = DB.db.getCollection("students");
        try{
            Document student = new Document();
            student.append("name", name);
            student.append("surname", surname);
            student.append("ID",ID);
            student.append("email", email);
            student.append("password", hashedPassword);
            studentsCollection.insertOne(student);
        }
        catch(MongoWriteException e){
            if(studentsCollection.countDocuments(new Document("ID", ID)) > 0){
                throw new IDAlreadyUsedException("There already exists a student with ID " + ID);
            }
            else if(studentsCollection.countDocuments(new Document("email", email)) > 0){
                throw new EmailAlreadyUsedException("There already exists a student with email " + email);
            }
            else{
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<Student> fetchAllStudents(){
        MongoCollection<Document> studentsCollection = DB.db.getCollection("students");
        ArrayList<Document> studentDocuments = studentsCollection.find().into(new ArrayList<>());   
        ArrayList<Student> students = new ArrayList<Student>();

        for (Document studentDocument : studentDocuments) {

            students.add(DocumentToStudent(studentDocument));
        }

        return students;
    }

    public static void deleteStudentByID(int ID) {
        MongoCollection<Document> studentsCollection = DB.db.getCollection("students");
        studentsCollection.deleteOne(new Document("ID", ID));          
    }

    public static Student authStudent(String email, byte[] hashedPassword) throws WrongCredentialsException{
        MongoCollection<Document> students = DB.db.getCollection("students");
        
        Bson filteremail = Filters.eq("email", email);
        Bson filterpassword = Filters.eq("password", hashedPassword);
        Bson filter = Filters.and(filteremail, filterpassword);
        Document student = students.find(filter).first();
            
        if(student != null){
            return DocumentToStudent(student);
        }
        else{
            throw new WrongCredentialsException("Wrong email or password");
        }
    }
    
    public static ArrayList<Student> getRequestingStudents(){
        MongoCollection<Document> studentsCollection = DB.db.getCollection("students");
        Bson filter1 = Filters.exists("project", false);
        Bson filter2 = Filters.eq("Requesting", true);
        Bson filterand = Filters.and(filter1, filter2);

        ArrayList<Document> studentDocuments = studentsCollection.find(filterand).into(new ArrayList<>());
        ArrayList<Student> students = new ArrayList<Student>();

        for (Document studentDocument : studentDocuments) {
            students.add(DocumentToStudent(studentDocument));
        }

        return students;
    }

    public static void assignProject(Student student, Project project, URL url) {
        
        MongoCollection<Document> studentsCollection = DB.db.getCollection("students");
        Document document = new Document();
        document.append("description", project.getDescription());
        document.append("ID", project.getID());
        document.append("URL", url.toString());
        
        ArrayList<Document> requirements = new ArrayList<Document>();
        for (String requirementName : project.getRequirements()) {
            Document requirement = new Document();
            requirement.append("requirementname", requirementName);
            requirement.append("value", false);
            requirements.add(requirement);
        }
        
        document.append("requirements", requirements);

        Bson filter = Filters.eq("ID", student.getID());
        Bson update = Updates.set("project", document);
        UpdateOptions options = new UpdateOptions().upsert(true);

        studentsCollection.updateOne(filter, update, options);
        student.assignProject(project, url);  
    }

    public static void requestProject(Student student){
        MongoCollection<Document> studentsCollection = DB.db.getCollection("students");

        Bson filter = Filters.eq("ID", student.getID());
        Bson update = Updates.set("Requesting", student.getRequesting());
        UpdateOptions options = new UpdateOptions().upsert(true);

        studentsCollection.updateOne(filter, update, options);
    }

    public static void updateProjectStatus(Student student, String requirement, Boolean value){
        MongoCollection<Document> studentsCollection = DB.db.getCollection("students");
        
        Bson filter = Filters.eq("ID", student.getID());
        Bson update = Updates.set("project.requirements.$[requirement].value", value); //sets the value attribute of the array element matching arrayFilter
        
        ArrayList<Bson> arrayFilters = new ArrayList<Bson>();
        arrayFilters.add(Filters.eq("requirement.requirementname", requirement)); //sets as the arrayFilter, the aarray element where requirementname is the chosen requirement
        UpdateOptions options = new UpdateOptions().arrayFilters(arrayFilters);
        studentsCollection.updateOne(filter, update, options);
    }


    public static Project getProjectByID(int ID) throws NoSuchProjectException{
        MongoCollection<Document> projects = DB.db.getCollection("projects");
        Document project = projects.find(new Document("ID", Double.valueOf(ID))).first();        
        if(project == null){
            throw new NoSuchProjectException("There isn't a project with ID: " + ID);
        }
        else{
            String description = project.getString("description");
            ArrayList<String> requirements = new ArrayList<String>(project.getList("requirements", String.class));
            return new Project(description, ID, requirements);
        }
    }

    public static ArrayList<Project> fetchAllProjects() {
        MongoCollection<Document> projectsCollection = DB.db.getCollection("projects");
        ArrayList<Document> projectDocuments = projectsCollection.find().into(new ArrayList<>());   
        ArrayList<Project> projects = new ArrayList<Project>();

        for (Document projectDocument : projectDocuments) {
            String description = projectDocument.getString("description");
            int ID = projectDocument.getDouble("ID").intValue();
            ArrayList<String> requirements = new ArrayList<String>(projectDocument.getList("requirements", String.class));
            projects.add(new Project(description, ID, requirements));
        }

        return projects;
    }

    public static void deleteProjectByID(int ID) {
        MongoCollection<Document> projectsCollection = DB.db.getCollection("projects");
        projectsCollection.deleteOne(new Document("ID", ID));        
    }

    public static Project insertProject(String description, ArrayList<String> requirements) {
        MongoCollection<Document> projectCollection = DB.db.getCollection("projects");
        Document project = new Document();

        //fetches next available ID from collection counters
        MongoCollection<Document> countersCollection = DB.db.getCollection("counters");
        Double counter = countersCollection.find(new Document("name", "projects")).first().getDouble("count");
        countersCollection.updateOne(Filters.eq("name", "projects"), Updates.set("count", counter+1));
        
        project.append("description", description);
        project.append("requirements", requirements);
        project.append("ID", counter);
        projectCollection.insertOne(project);

        return new Project(description, counter.intValue(), requirements);
    }


    public static Proposal getProposalByID(int ID) throws NoSuchProposalException {
        MongoCollection<Document> proposals = DB.db.getCollection("proposals");
        Document proposal = proposals.find(new Document("ID", Double.valueOf(ID))).first();        
        if(proposal == null){
            throw new NoSuchProposalException("There isn't a proposal with ID: " + ID);
        }
        else{
            String description = proposal.getString("description");
            int studentID = proposal.getInteger("studentID");

            return new Proposal(description, studentID, ID);
        }
    }

    public static Proposal getProposalByStudentID(int studentID) {
        MongoCollection<Document> proposals = DB.db.getCollection("proposals");
        Document proposal = proposals.find(new Document("studentID", Double.valueOf(studentID))).first();        
        if(proposal == null){
            return null;
        }
        else{
            String description = proposal.getString("description");
            int ID = proposal.getInteger("ID");
            return new Proposal(description, studentID, ID);
        }
    }

    public static ArrayList<Proposal> fetchAllProposals() {
        MongoCollection<Document> proposalsCollection = DB.db.getCollection("proposals");
        ArrayList<Document> proposalDocuments = proposalsCollection.find().into(new ArrayList<>());   
        ArrayList<Proposal> proposals = new ArrayList<Proposal>();

        for (Document proposalDocument : proposalDocuments) {
            String description = proposalDocument.getString("description");
            int ID = proposalDocument.getInteger("ID");
            int studentID = proposalDocument.getInteger("studentID");
            proposals.add(new Proposal(description, studentID, ID));
        }

        return proposals;
    }

    public static void deleteProposalByID(int ID) {
        MongoCollection<Document> proposalsCollection = DB.db.getCollection("proposals");
        proposalsCollection.deleteOne(new Document("ID", ID));               
    }

    public static Proposal insertProposal(int studentID, String description) {
        MongoCollection<Document> proposalCollection = DB.db.getCollection("proposals");
        Document proposal = new Document();

        //fetches next available ID from collection counters
        MongoCollection<Document> countersCollection = DB.db.getCollection("counters");
        Double counter = countersCollection.find(new Document("name", "proposals")).first().getDouble("count");
        countersCollection.updateOne(Filters.eq("name", "proposals"), Updates.set("count", counter+1));
        
        proposal.append("description", description);
        proposal.append("studentID", studentID);
        proposal.append("ID", counter.intValue());
        proposalCollection.insertOne(proposal);

        return new Proposal(description, studentID, counter.intValue());        
    }

    //PROFESSOR
    public static Professor getProfessorByID(int ID) throws NoSuchProfessorException{
        MongoCollection<Document> professors = DB.db.getCollection("professors");
        Document professor = professors.find(new Document("ID", Double.valueOf(ID))).first();        
        if(professor == null){
            throw new NoSuchProfessorException("There isn't a professor with ID: "+ ID);
        }
        else{
            String name = professor.getString("name");
            String surname = professor.getString("surname");
            return new Professor(name, surname, ID);
        }
    }

    public static ArrayList<Professor> fetchAllProfessors() {
        MongoCollection<Document> professorsCollection = DB.db.getCollection("professors");
        ArrayList<Document> professorDocuments = professorsCollection.find().into(new ArrayList<>());   
        ArrayList<Professor> professors = new ArrayList<Professor>();

        for (Document professorDocument : professorDocuments) {
            String name = professorDocument.getString("name");
            String surname = professorDocument.getString("surname");
            int ID = professorDocument.getInteger("ID");
            professors.add(new Professor(name, surname, ID));
        }

        return professors;
    }

    public static void deleteProfessorByID(int ID) {
        MongoCollection<Document> professorsCollection = DB.db.getCollection("professors");
        professorsCollection.deleteOne(new Document("ID", ID));               
    }

    public static void insertProfessor(String name, String surname, int ID, String email, byte[] hashedPassword) throws IDAlreadyUsedException, EmailAlreadyUsedException{
        MongoCollection<Document> professorCollection = DB.db.getCollection("professors");
        try{
            Document professor = new Document();
            professor.append("name", name);
            professor.append("surname", surname);
            professor.append("ID", ID);
            professor.append("email", email);
            professor.append("password", hashedPassword);
            professorCollection.insertOne(professor);
        }
        catch(MongoWriteException e){
            if(professorCollection.countDocuments(new Document("ID", ID)) > 0){
                throw new IDAlreadyUsedException("There already exists a student with ID " + ID);
            }
            else if(professorCollection.countDocuments(new Document("email", email)) > 0){
                throw new EmailAlreadyUsedException("There already exists a student with email " + email);
            }
            else{
                e.printStackTrace();
            }
        }       
    }
    
    public static Professor authProfessor(String email, byte[] hashedPassword) throws WrongCredentialsException {
            MongoCollection<Document> professors = DB.db.getCollection("professors");            
            Bson filteremail = Filters.eq("email", email);
            Bson filterpassword = Filters.eq("password", hashedPassword);
            Bson filter = Filters.and(filteremail, filterpassword);
            Document professor = professors.find(filter).first();
            if(professor != null){
                String name = professor.getString("name");
                String surname = professor.getString("surname");
                int ID = professor.getInteger("ID");
                return new Professor(name, surname, ID);
            }
            else{
                throw new WrongCredentialsException("Wrong email or password");
            }
    }
}
