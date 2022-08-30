package prova.org.Mappers.impl;

import java.util.ArrayList;

import prova.org.BaseClasses.Proposal;
import prova.org.DB.DB;
import prova.org.Exceptions.NoSuchProposalException;
import prova.org.Mappers.ProposalsMapperInterface;

public class ProposalsMapperImpl implements ProposalsMapperInterface{

    @Override
    public Proposal getByID(int ID) throws NoSuchProposalException {
        return DB.getProposalByID(ID);
    }

    @Override
    public Proposal getByStudentID(int studentID) {
        return DB.getProposalByStudentID(studentID);
    }

    @Override
    public ArrayList<Proposal> fetchAll() {
        return DB.fetchAllProposals();
    }

    @Override
    public void deleteByID(int ID) {
        DB.deleteProposalByID(ID);      
    }

    @Override
    public Proposal create(int studentID, String description) {
        return DB.insertProposal(studentID, description);
    }
    
}
