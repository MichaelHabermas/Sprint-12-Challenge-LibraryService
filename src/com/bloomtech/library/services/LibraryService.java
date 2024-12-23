package com.bloomtech.library.services;

import com.bloomtech.library.exceptions.CheckableNotFoundException;
import com.bloomtech.library.exceptions.LibraryNotFoundException;
import com.bloomtech.library.exceptions.ResourceExistsException;
import com.bloomtech.library.models.*;
import com.bloomtech.library.models.checkableTypes.Checkable;
import com.bloomtech.library.models.checkableTypes.Media;
import com.bloomtech.library.repositories.LibraryRepository;
import com.bloomtech.library.models.CheckableAmount;
import com.bloomtech.library.views.LibraryAvailableCheckouts;
import com.bloomtech.library.views.OverdueCheckout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private CheckableService checkableService;

    public List<Library> getLibraries() {
        return libraryRepository.findAll();
    }

    public Library getLibraryByName(String name) {
        if (libraryRepository.findByName(name).isPresent()) {
            return libraryRepository.findByName(name).get();
        } else {
            throw new LibraryNotFoundException("There is no library with that name");
        }
    }

    public void save(Library library) {
        List<Library> libraries = libraryRepository.findAll();
        if (libraries.stream().anyMatch(p->p.getName().equals(library.getName()))) {
            throw new ResourceExistsException("Library with name: " + library.getName() + " already exists!");
        }
        libraryRepository.save(library);
    }

    public CheckableAmount getCheckableAmount(String libraryName, String checkableIsbn) {
        Library library = getLibraryByName(libraryName);

        Checkable checkable = checkableService.getByIsbn(checkableIsbn);

        for (CheckableAmount checkableAmount : library.getCheckables()) {
            if (Objects.equals(checkableAmount.getCheckable().getIsbn(), checkableIsbn)) {
                return checkableAmount;
            }
        }

        return new CheckableAmount(checkable, 0);
    }

    public List<LibraryAvailableCheckouts> getLibrariesWithAvailableCheckout(String isbn) {
        Checkable checkable = checkableService.getByIsbn(isbn);
        List<Library> libraries = libraryRepository.findAll();
        List<LibraryAvailableCheckouts> libraryAvailableCheckouts = new ArrayList<>();

        for (Library library : libraries) {
            int available = 0;
            List<CheckableAmount> checkableAmounts = library.getCheckables();

            for (CheckableAmount checkableAmount : checkableAmounts) {
                if (checkableAmount.getCheckable().equals(checkable)) {
                    available = checkableAmount.getAmount();
                }
            }
            libraryAvailableCheckouts.add(new LibraryAvailableCheckouts(available, library.getName()));
        }

        return libraryAvailableCheckouts;
    }

    public List<OverdueCheckout> getOverdueCheckouts(String libraryName) {
        List<OverdueCheckout> overdueCheckouts = new ArrayList<>();
        Library library = libraryRepository.findByName(libraryName).get();
        Set<LibraryCard> libraryCards = library.getLibraryCards();

        for (LibraryCard libraryCard : libraryCards) {
            List<Checkout> checkouts = libraryCard.getCheckouts();
            Checkout checkoutDue;

            for (Checkout checkout : checkouts) {
                if (checkout.getDueDate().isBefore(LocalDateTime.now())) {
                    checkoutDue = checkout;
                    overdueCheckouts.add(new OverdueCheckout(libraryCard.getPatron(), checkoutDue));
                }
            }
        }

        return overdueCheckouts;
    }
}
