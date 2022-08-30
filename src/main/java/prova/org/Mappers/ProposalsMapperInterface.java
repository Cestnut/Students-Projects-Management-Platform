package prova.org.Mappers;

import prova.org.BaseClasses.Proposal;

public interface ProposalsMapperInterface extends MapperInterface<Proposal> {
    public Proposal create(int studentID, String description); //creates a Proposal and inserts it into the database and returns the object
    public Proposal getByStudentID(int studentID); //gets a Proposal by its studentID
}
