package com.bloomtech.library.services;

import com.bloomtech.library.exceptions.CheckableNotFoundException;
import com.bloomtech.library.exceptions.ResourceExistsException;
import com.bloomtech.library.models.Library;
import com.bloomtech.library.models.checkableTypes.Checkable;
import com.bloomtech.library.repositories.CheckableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CheckableService {

    @Autowired
    private CheckableRepository checkableRepository;

    public List<Checkable> getAll() {
        return checkableRepository.findAll();
    }

    public Checkable getByIsbn(String isbn) {
        Optional<Checkable> checkable = checkableRepository.findByIsbn(isbn);

        if (checkable.isPresent()) {
            return checkable.get();
        } else {
            throw new CheckableNotFoundException("No Checkable found with isbn: " + isbn);
        }
    }

    public Checkable getByType(Class type) {
        Optional<Checkable> checkable = checkableRepository.findByType(type);

        if (checkable.isPresent()) {
            return checkable.get();
        } else {
            throw new CheckableNotFoundException("No Checkable found of type: " + type.getName());
        }
    }

    public void save(Checkable checkable) {
        List<Checkable> checkables = checkableRepository.findAll();
        if (checkables.stream().anyMatch(c->c.getIsbn().equals(checkable.getIsbn()))) {
            throw new ResourceExistsException("Checkable with isbn: " + checkable.getIsbn() + " already exists!");
        }
        checkableRepository.save(checkable);
    }
}
