# Introduzione

Gli studenti possono iscriversi nella piattaforma e richiedere o proporre un progetto, e aggiornarne lo stato di avanzamento.
Il docente assegna il progetto oppure accetta la proposta dello studente e ne definisce i requisiti.

Interpretando i requisiti abbiamo le seguenti specifiche:
- Email e ID sono univoci. Uno studente e un professore possono avere lo stesso ID, essendo considerati due entità diverse.
- Ogni studente può effettuare solo una proposta
- Con stato di avanzamento del progetto si intende specificare per ogni requisito se è stato soddisfatto

Introduciamo inoltre i seguenti campi:
- URL per il progetto assegnato
    
# Operazioni
## Studente
- Effettuare o annullare una richiesta di progetto
- Proporre un progetto dando una descrizione iniziale
    - Ogni studente può effettuare una e una sola proposta
- Visualizzare se presente il proprio progetto
- Aggiornare lo stato di avanzamento del proprio progetto
    - Vanno forniti il nome del requisito e il suo nuovo valore (svolto/non svolto)

## Professore
- Visualizzare tutti i progetti esistenti
- Visualizzare tutte le proposte esistenti
- Visualizzare l’elenco degli studenti richiedenti un progetto
- Creare un nuovo progetto
    - Vanno forniti descrizione e lista dei requisiti
- Accettare la proposta di uno studente
    - Ciò comporta l’assegnazione del progetto allo studente che ha effettuato la proposta
    - Vanno forniti lista dei requisiti e URL
- Assegnare un progetto a uno studente
    - Vanno forniti ID del progetto, ID dello studente e URL su cui andrà caricato il progetto
- Ottenere le informazioni di uno studente dato il suo ID

# Architettura
Il progetto si basa su un’architettura Client-Server. Client e server comunicano scambiandosi messaggi JSON.

I dati sono salvati su un database MongoDB, la scelta deriva dalla necessità di avere dei dati semistrutturati.

# Dipendenze
È stato utilizzato maven per importare librerie esterne.
All’interno del file pom.xml sono state inserite le seguenti dipendenze:
- https://mvnrepository.com/artifact/org.mongodb/mongo-java-driver 
- https://mvnrepository.com/artifact/org.json/json

# Server
All'avvio il server legge da file a quale porta mettersi in ascolto. Successivamente chiama il metodo statico readSettings() dalla classe DB, che inizializza la variabile statica db, rappresentante la connessione col database, utilizzata successivamente per tutte le interazioni con il database.

![ServerUML](uml/ServerSideUML.png)

Verranno di seguito illustrate tutte le varie classi del modello.


## User
Classe astratta per i ruoli derivati, contenente gli attributi fondamentali con i relativi getter e setter
### Attributi
- private String Name
- private String Surname
- private int ID
### Methods
- public void setName(String name)
- public void setSurname(String surname)
- public void setID(int ID)
- public String getName()
- public String getSurname()
- public int getID()


## Student
Estende la classe User
### Attributi
- private AssignedProject project
- private Boolean requesting
- private ProposalsMapperImpl proposalsmapper
### Methods
- public Student Student(String name, String surname, int ID)   
- public Student Student(String name, String surname, int ID, AssignedProject project)
- public void assignProject(AssignedProject)
- public AssignedProject getProject()
- public getProjectCompletitionPercentual()
- public void updateProjectStatus(String, Boolean)
- public Proposal proposeProject(String)
- public void toggleRequesting()
- public Boolean getRequesting()
- public String toString()


## Professor
### Attributi
- private ProjectsMapperImpl projectsmapper 
- private StudentsMapperImpl studentsmapper
- private ProposalsMapperImpl proposalsmapper
### Methods
- public Professor Professor(String name, String surname, int ID)
- public void assignProject(int studentID, int projectID, String urlString) throws NoSuchProjectException, MalformedURLException, NoSuchStudentException
- public void acceptProposal(int proposaID, ArrayList <String> requirements, String  urlString) throws NoSuchProposalException, MalformedURLException, NoSuchStudentException
- public void createProject(String description, ArrayList 
<String> requirements)
- public String viewProjects()
- public String viewProposals()
- public String viewStudents()
- public String viewRequiring()
- public String getStudentByID(int studentID) throws NoSuchStudentException


## Project
### Attributi
- private String description
- private ArrayList <String> requirements
- private int ID
### Methods
- public Project Project(String description, int ID, ArrayList <String> Requirements)
- public void setDescription(String Description)
- public void setID(int ID)
- public void setRequirements(ArrayList <String> requirements)
- public String getDescription()
- public int getID()
- public ArrayList <String> getRequirements()
- public String toString()
## AssignedProject
### Attributi
- private URL url
- private Hashmap <String, Boolean> status
### Methods
- public AssignedProject(String description, Hashmap <String, Boolean> status, int ID, URL url)
- public AssignedProject(Project project, URL url)
- public Hashmap <String, Boolean> getStatus()
- public void setStatus(Hashmap <String, Boolean>)
- public void updateStatus(String, Boolean)
- public URL getURL()
- public void setURL(URL url)
- public String toString()


## Proposal
### Attributi
- private int ID
- private String description
- private int studentID
### Methods
- public Proposal(String description, int studentID, int ID)
- public void setDescription(String description)
- public void setStudentID(int studentID)
- public void setID(int ID)
- public String getDescription()
- public int getStudentID()
- public int getID()
- public String toString()


## Server
### Attributi
- private int port
### Methods
- public static void readPort()
- public static void main(String[] args)
        

## Worker
Classe che implementa l'interfaccia Runnable. Ne viene creata un'istanza per ogni connessione al server.
### Attributi
- private Student student
- private Professor professor
- private Socket socket
- private PrintWriter output
- private BufferedReader input
- private StudentsMapperImpl studentsmapper
- private ProfessorsMapperImpl professorsmapper
- private ProposalsMapperImpl proposalsmapper
### Methods
- public Worker(Socket socket) throws IOException
- public JSONObject studentAuth(JSONObject args)
- public JSONObject studentRegister(JSONObject args)
- public JSONObject requireProject(JSONObject args)
- public JSONObject proposeProject(JSONObject args)
- public JSONObject viewProject()
- public JSONObject updateProject(JSONObject args)
- public JSONObject professorAuth(JSONObject args)
- public JSONObject createProject(JSONObject args)
- public JSONObject assignProject(JSONObject args)
- public JSONObject acceptProposal(JSONObject args)
- public JSONObject viewProjects()
- public JSONObject viewProposals()
- public JSONObject viewStudents()
- public JSONObject viewRequiring()
- public JSONObject getStudentByID(JSONObject args)
- public void run()
        
## DB
Classe utilizzata per connettersi al database
### Attributi
- static MongoDatabase db
    - Attributo rappresentate la connessione al database
### Methods
- static void readSettings()
    - Setta l'attibuto db leggendo IP e porta del database dal file db.conf
    - private Student DocumentToStudent(Document student)
- public Student getStudentByID(int ID) throws NoSuchStudentException
- public void insertStudent(String name, String surname, int ID, String email, byte[] hashedPassword) throws NoSuchAlgorithmException, IDAlreadyUsedException, EmailAlreadyUsedException
- public  ArrayList <Student> fetchAllStudents()
- public void deleteStudentByID(int ID)
- public Student authStudent(String email, byte[] hashedPassword) throws WrongCredentialsException
- public ArrayList <Student> getRequestingStudents()
- public void assignProject(Student student, Project project, URL url)

- public void requestProject(Student student)
- public void updateProjectStatus(Student student, String requirement, Boolean value)

- public Project getProjectByID(int ID) throws NoSuchProjectException
- public void insertProject(String description, ArrayList <String> requirements)
- public ArrayList <Project> fetchAllProjects()
- public void deleteProjectByID(int ID)

- public Proposal getProposalByID(int ID) throws NoSuchProposalException
- public void insertProposal(int studentID, String description)
- public ArrayList <Proposal> fetchAllProposals()
- public void deleteProposalByID(int ID)

- public Professor getProfessorByID(int ID) throws NoSuchProfessorException
- public void insertProfessor(String name, String surname, int ID, String email, byte[] hashedPassword) throws IDAlreadyUsedException, EmailAlreadyUsedException
- public ArrayList <Professor> fetchAllProfessors()
- public void deleteProfessorByID(int ID)
- public Professor authProfessor(String email, byte[] hashedPassword) throws WrongCredentialsException


## MapperInterface
Interfaccia implementata da tutti i mapper, con i metodi più generici
### Methods
- public  <generic> getByID(int ID) throws NoSuchResourceException
- public ArrayList <generic> fetchAll()
- public void deleteByID(int ID)
    

## StudentsMapperInterface
### Methods
- public Student create(String name, String surname, int ID, String email, String password) throws IDAlreadyUsedException, EmailAreadyUsedException
- public Student auth(String email, String password) throws WrongCredentialsException
- public ArrayList <Student> getRequesting()
- public void assignProject(Student student, Project project, URL url)
- public void assignProject(int studentID, Project project, URL url) throws NoSuchStudentException
- public void requestProject(Student student)
- public void updateProjectStatus(Student student, String requirement, Boolean value)
## StudentsMapperImpl

## ProjectsMapperInterface
### Methods
- public Project create(String Description, ArrayList <String> requirements)
## ProjectsMapperImpl

## ProposalsMapperInterface
### Methods
- public Proposal create(int studentID, String description)
- public Proposal getByStudentID(int studentID)        
## ProposalsMapperImpl

## ProfessorsMapperInterface
### Methods
- public Professor create(String name, String surname, int ID, String email, String password) throws IDAlreadyUsedException, EmailAreadyUsedException
- public Professor auth(String email, String password)throws WrongCredentialsException        
## ProfessorMapperImpl
## Eccezioni
Sono stato definite delle eccezioni per gestire alcune situazioni.
### NoSuchResourceException
Eccezione che indica l'assenza di una risorsa dal database. Viene ulteriormente estesa in:
- NoSuchProfessorException
        - NoSuchStudentException
        - NoSuchProjectException
        - NoSuchProposalException 
### EmailAlreadyUsedException
### IDAlreadyUsedException
### WrongCredentialsException

# Client

![ClientUML](uml/ClientSideUML.png)

## Client
Gestisce lo scambio dei messaggi tra client-side e server-side.
### Attributi
- private Socket socket
- private BufferedReader input
- private PrintWriter output
### Methods
- public Client(Socket socket) throws UnknownHostException, IOException
- public JSONObject close() throws IOException
- public JSONObject sendJSON(JSONObject json)
- public JSONObject studentRegister(String name, String surname, String email, String password, int ID)
- public JSONObject studentAuth(String email, String password)
- public JSONObject requireProject()

- public JSONObject proposeProject(String description)
- public JSONObject viewProject()
- public JSONObject updateProject(String requirementName, Boolean value)

- public JSONObject professorAuth(String email, String password)
- public JSONObject createProject(ArrayList <String> requirements, String description)
- public JSONObject assignProject(int studentID, int projectID, String URL)
- public JSONObject acceptProposal(int proposalID, ArrayList <String> requirements, String URL)
- public JSONObject viewStudents()
- public JSONObject viewRequiring()
- public JSONObject viewProjects()
- public JSONObject viewProposals()
- public JSONObject getStudentByID(int studentID)
        

## Menu
Presenta l'interfaccia da riga di comando all'utente. Utilizza la classe Client per mandare messaggi al server.
### Attributi
- private Scanner keyboard
- private Client client
### Methods
- public void root()
- public void studentRegister()
- public void studentAuth()
- public void professorAuth()
- public void studentHome()
- public void requireProject()
- public void proposeProject()
- public void viewProject()
- public void updateProject()

- public void professorHome()
- public void createProject()
- public void assignProject()
- public void acceptProposal()
- public void getStudentByID()
        
# Requisiti
## Information Hiding
Con Information Hiding si intende l'atto di nascondere il modo in cui un metodo o una classe sono stati implementati. 

Ciò permette a una classe di usarne un'altra senza sapere come funziona, dando ai programmatori la possibilità di cambiare l'implementazione di quest'ultima senza dover cambiare il resto del codice.

Un esempio di ciò si ha nel metodo create della classe **StudentsMapperImpl**

```
byte[] hashedPassword;
MessageDigest md = MessageDigest.getInstance("SHA-256");
hashedPassword = md.digest(password.getBytes());
DB.insertStudent(name, surname, ID, email, hashedPassword);
return new Student(name, surname, ID);
```

Vediamo che viene chiamato il metodo **insertStudent** della classe **DB**. Se dovesse cambiare l'implementazione del metodo nella classe DB, magari perché si usa un altro DBMS con API diversa, non sarebbe necessario modificare la classe **StudentsMapperImpl**.

## Incapsulamento
L'incapsulamento è un concetto della programmazione a oggetti per cui all'interno di una classe sono presenti sia gli attributi che i metodi che operano su di essi. In questo modo si può rendere impossibile dall'esterno l'accesso agli attributi di una classe, se non tramite dei metodi interni alla classe che magari eseguono dei controlli sulla validità degli input.

Anche se senza controlli specifici, un'esempio è la classe **Student**:

```
public class Student extends Person{
    private AssignedProject project;
    ...
    public void assignProject(Project project, URL url){
        this.project = new AssignedProject(project, url);
    }
    public AssignedProject getProject(){
        return this.project;
    }
}
```

Qui l'attributo project è accessibile solo tramite i due metodi, che potrebbero effettuare dei controlli aggiuntivi.

## Ereditarietà
L'ereditarietà è un concetto che si applica tra due classi, una classe padre e una classe figlio, facendo in modo che il figlio erediti i metodi e gli attributi del padre, potendo così riutilizzarli e in caso modificarli (overriding).
In java viene utilizzata la keyword **Extends**. Un esempio si ha tra la classe astratta **Person** e la classe **Student**:

```
public abstract class Person { ... }
public class Student extends Person{ ... }
```

In questo modo la classe **Student** eredita tutti i metodi e gli attributi della classe **Person**, e può liberamente riutilizzarli, incluso ad esempio il costruttore:

```
  public Student(String name, String surname, int ID){
        super(name, surname, ID);
        this.requesting = false;
    }
```

La classe Studente richiama il costruttore che prende in ingresso i parametri **(String, String, ID)** e in più lo estendo inizializzando un proprio attributo.
Una classe può essere figlia solamente di un'altra classe.

## Overloading
**L'overloading** è un concetto per cui si possono avere all'interno di una classe due o più metodi con lo stesso nome, dandogli quindi implementazioni diverse, a patto che il tipo di parametri aspettati sia diverso.

Un esempio di ciò si ha nella classe **StudentsMapperInterface**:

```
public void assignProject(Student student, Project project, URL url);
public void assignProject(int studentID, Project project, URL url) throws NoSuchStudentException;
```
In questo caso si ha che il secondo metodo prende prima lo studente dal database, e poi richiama il primo metodo. Nella classe **StudentsMapperImpl**:

```
@Override
    public void assignProject(Student student, Project project, URL url) {
        DB.assignProject(student, project, url);
    }

@Override
    public void assignProject(int studentID, Project project, URL url) throws NoSuchStudentException {
        DB.assignProject(getByID(studentID), project, url);
    }
```

## Overriding
L'overriding si ha quando i metodi di una classe sovrascrivono quelli della classe che estende o dell'interfaccia che implementa (in quest'ultimo caso è obbligatorio).

Un esempio lo abbiamo nella classe **Student**, viene fatto l'override del metodo toString, definito per la prima volta nella classe **Object**, padre di tutte le classi.

```
@Override
public String toString{
    return (getID() + " " + getName() + " " + getSurname());
    }}
```

## Classe Astratta
Una classe astratta è una classe che può avere metodi astratti o metodi implementati, ma non può esistere un'istanza di tale classe. I metodi astratti vanno implementati dalla classe figlia, a meno che quest'ultima non sia astratta a sua volta.

Un esempio ne è la classe **Person**, che in questo caso non ha metodi astratti:

```
public abstract class Person{
    ...
}
```

## Interfaccia
Le interfacce sono delle raccolte di attributi e metodi astratti che non possono essere implementati, a differenza delle classi astratte.

Una classe può implementare più di un'interfaccia alla volta, oltre che estendere un'altra classe. In questo modo si ottiene una sorta di ereditarietà multipla.

Un'esempio di interfaccia è l'interfaccia **ProposalsMapperInterface**:

```
public interface ProposalsMapperInterface extends MapperInterface <Proposal> {
    public Proposal create(int studentID, String description);
    public Proposal getByStudentID(int studentID);
}
```

Come si può vedere quest'interfaccia eredita i metodi della classe **MapperInterface**, ed essendo anch'essa un'interfaccia non ha bisogno di implementarli.

## Networking
Java fornisce quattro classi per la connessione tra applicazioni, dando la possibilità di scegliere tra connessione UDP e TCP.

Per la modalità UDP abbiamo DatagramSocket e DatagramPacket.

Per la modalità TCP, che è quella utilizzata nel progetto, abbiamo invece Socket e ServerSocket.

La classe ServerSocket è la classe utilizzata dal server che si mette in ascolto su una porta e genera un oggetto Socket quando riceve una connessione.

La classe Socket è utilizzata per lo scambio di dati in entrambi i sensi di comunicazione.

Nel progetto esiste la classe **Server** che utilizza entrambe. All'interno del main abbiamo:

```
...
ServerSocket serversocket = new ServerSocket(Server.port);
...
while(true){
    System.out.println("Listening for connection");
    Socket socket = serversocket.accept();
    System.out.println("Connection accepted");
    Thread worker = new Thread(new Worker(socket));
    worker.start();
}
```

Viene prima generata la serversocket, legandola alla Server.port.

All'interno di un while infinto viene chiamato il metodo **accept**, che resta in attesa di connessione e genera un oggetto Socket quando ne riceve una.

L'oggetto socket viene poi passato al metodo Worker per svolgere le operazioni di I/O col client.


## Multithreading
Il multithreading è una tecnica che permette di eseguire diversi flussi di istruzioni concorrentemente condividendo alcune risorse, senza dover quindi necessariamente creare un altro processo.

In java il multithreading può essere realizzato in due modi:
- estendendo la classe **Thread** per poi chiamare il metodo **start**
- implementando l'interfaccia **Runnable** e facendo l'override del metodo **run** che sarà poi il metodo da cui partirà l'esecuzione di ogni thread.

Viene poi instanziato un'oggetto della classe Thread, passando in input al costruttore l'oggetto che implementa la classe Runnable.
Infine viene eseguito il metodo start.

Il multithreading viene utilizzato nel progetto per poter gestire più connessioni contemporaneamente. La classe **Worker** implementa i metodi per interagire con il client.

```
    public class Worker implements Runnable{
    ...
    
    public void run(){
    ...
    }
    }

Thread worker = new Thread(new Worker(socket));
worker.start();

```
Creazione dell'oggetto di classe Thread e successiva invocazione del metodo start nel main della classe Server.
