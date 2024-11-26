package com.bloomtech.library.services;

import com.bloomtech.library.exceptions.ResourceExistsException;
import com.bloomtech.library.models.Patron;
import com.bloomtech.library.repositories.PatronRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatronService {

    @Autowired
    private PatronRepository patronRepository;

    public void save(Patron patron) {
        List<Patron> patrons = patronRepository.findAll();

        if (patrons.stream().anyMatch(p->p.getName().equals(patron.getName()))) {
            throw new ResourceExistsException("Patron with name: " + patron.getName() + " already exists!");
        }

        patronRepository.save(patron);
    }
}
