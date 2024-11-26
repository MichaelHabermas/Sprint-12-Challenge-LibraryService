package com.bloomtech.library.services;

import com.bloomtech.library.exceptions.ResourceExistsException;
import com.bloomtech.library.models.LibraryCard;
import com.bloomtech.library.models.Patron;
import com.bloomtech.library.repositories.LibraryCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibraryCardService {

    @Autowired
    private LibraryCardRepository libraryCardRepository;

    public List<LibraryCard> getLibraryCardsByLibraryName(String libraryName) {
        return libraryCardRepository.findByLibraryName(libraryName);
    }

    public List<LibraryCard> getLibraryCardsByPatronName(String patronName) {
        return libraryCardRepository.findByPatronName(patronName);
    }

    public void save(LibraryCard libraryCard) {
        List<LibraryCard> libraryCards = libraryCardRepository.findAll();
        if (libraryCards.stream()
                .anyMatch(lc -> lc.getPatron().getName().equals(libraryCard.getPatron().getName()) &&
                lc.getLibrary().getName().equals(libraryCard.getLibrary().getName()))) {
            throw new ResourceExistsException("LibraryCard for Patron: " + libraryCard.getPatron().getName() +
                    " already exists for Library with name: " + libraryCard.getLibrary().getName() + " !");
        }
        libraryCardRepository.save(libraryCard);
    }
}
