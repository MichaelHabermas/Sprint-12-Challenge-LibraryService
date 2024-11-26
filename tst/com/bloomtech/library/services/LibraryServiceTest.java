package com.bloomtech.library.services;


import com.bloomtech.library.exceptions.LibraryNotFoundException;
import com.bloomtech.library.exceptions.ResourceExistsException;
import com.bloomtech.library.models.*;
import com.bloomtech.library.models.checkableTypes.*;
import com.bloomtech.library.repositories.LibraryRepository;
import com.bloomtech.library.views.LibraryAvailableCheckouts;
import com.bloomtech.library.views.OverdueCheckout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LibraryServiceTest {
    @Autowired
    private LibraryService libraryService;

    @MockBean
    private LibraryRepository libraryRepository;

    @MockBean
    private CheckableService checkableService;

    private List<Checkable> checkables;
    private List<Patron> patrons;
    private List<Library> libraries;
    private List<LibraryCard> libraryCards;

    @BeforeEach
    void init() {
        //SeedData for testing without using database
        checkables = new ArrayList<>();
        patrons = new ArrayList<>();
        libraries = new ArrayList<>();
        libraryCards = new ArrayList<>();

        checkables.addAll(
                Arrays.asList(
                        new Media("1-0", "The White Whale", "Melvin H", MediaType.BOOK),
                        new Media("1-1", "The Sorcerer's Quest", "Ana T", MediaType.BOOK),
                        new Media("1-2", "When You're Gone", "Complaining at the Disco", MediaType.MUSIC),
                        new Media("1-3", "Nature Around the World", "DocuSpecialists", MediaType.VIDEO),
                        new ScienceKit("2-0", "Anatomy Model"),
                        new ScienceKit("2-1", "Robotics Kit"),
                        new Ticket("3-0", "Science Museum Tickets"),
                        new Ticket("3-1", "National Park Day Pass")
                )
        );

        Patron p1 = new Patron("Aung", 24);
        Patron p2 = new Patron("Darya", 32);

        Library l1 = new Library("Brightmore Public Library");
        Library l2 = new Library("Corte Madera Library");

        l1.getCheckables().add(new CheckableAmount(checkables.get(0), 2));
        l1.getCheckables().add(new CheckableAmount(checkables.get(1), 1));
        l1.getCheckables().add(new CheckableAmount(checkables.get(4), 1));
        l1.getCheckables().add(new CheckableAmount(checkables.get(7), 4));

        l2.getCheckables().add(new CheckableAmount(checkables.get(0), 1));
        l2.getCheckables().add(new CheckableAmount(checkables.get(1), 3));
        l2.getCheckables().add(new CheckableAmount(checkables.get(2), 2));
        l2.getCheckables().add(new CheckableAmount(checkables.get(3), 4));
        l2.getCheckables().add(new CheckableAmount(checkables.get(4), 3));
        l2.getCheckables().add(new CheckableAmount(checkables.get(5), 5));
        l2.getCheckables().add(new CheckableAmount(checkables.get(6), 8));
        l2.getCheckables().add(new CheckableAmount(checkables.get(7), 6));


        LibraryCard lc1_1 = new LibraryCard();
        lc1_1.setPatron(p1);
        lc1_1.setLibrary(l1);
        p1.getLibraryCards().add(lc1_1);
        l1.getLibraryCards().add(lc1_1);

        LibraryCard lc1_2 = new LibraryCard();
        lc1_2.setPatron(p1);
        lc1_2.setLibrary(l2);
        p1.getLibraryCards().add(lc1_2);
        l2.getLibraryCards().add(lc1_2);

        LibraryCard lc2_2 = new LibraryCard();
        lc2_2.setPatron(p2);
        lc2_2.setLibrary(l2);
        p2.getLibraryCards().add(lc2_2);
        l2.getLibraryCards().add(lc2_2);


        lc1_1.getCheckouts().addAll(
                Arrays.asList(
                        new Checkout(checkables.get(0), LocalDateTime.now().plusDays(5)),
                        new Checkout(checkables.get(1), LocalDateTime.now().minusDays(1)),
                        new Checkout(checkables.get(4), LocalDateTime.now().minusDays(3))
                )
        );

        lc1_2.getCheckouts().addAll(
                Arrays.asList(
                        new Checkout(checkables.get(2), LocalDateTime.now().plusDays(15)),
                        new Checkout(checkables.get(3), LocalDateTime.now().minusDays(3)),
                        new Checkout(checkables.get(5), LocalDateTime.now().plusDays(4))
                )
        );

        lc2_2.getCheckouts().addAll(
                Arrays.asList(
                        new Checkout(checkables.get(2), LocalDateTime.now().minusDays(15)),
                        new Checkout(checkables.get(3), LocalDateTime.now().minusDays(3)),
                        new Checkout(checkables.get(4), LocalDateTime.now().minusDays(4))
                )
        );

        patrons.add(p1);
        patrons.add(p2);

        libraries.add(l1);
        libraries.add(l2);

        libraryCards.add(lc1_1);
        libraryCards.add(lc1_2);
        libraryCards.add(lc2_2);
    }

    @Test
    void getLibraries() {
        Mockito.when(libraryRepository.findAll()).thenReturn(libraries);
        List<Library> libraries = libraryService.getLibraries();
        assertEquals(2, libraries.size());
    }

    @Test
    void getLibraryByName_findsExistingLibrary() {
        Mockito.when(libraryRepository.findByName(any(String.class))).thenReturn(Optional.of(libraries.get(0)));
        Library library = libraryService.getLibraryByName("Brightmore Public Library");
        assertEquals("Brightmore Public Library", library.getName());
    }

    @Test
    void getLibraryByName_nonExistingLibrary() {
        Mockito.when(libraryRepository.findByName(any(String.class))).thenReturn(Optional.empty());

        assertThrows(LibraryNotFoundException.class, ()->{
            libraryService.getLibraryByName("Non-Existent Library");
        });
    }

    @Test
    void save() {
        when(libraryRepository.findAll()).thenReturn(libraries);
        libraryService.save(new Library("New Library"));
        Mockito.verify(libraryRepository).save(any(Library.class));
    }

    @Test
    void save_existingName_throwsResourceExistsException() {
        when(libraryRepository.findAll()).thenReturn(libraries);
        assertThrows(ResourceExistsException.class, ()->{
            libraryService.save(new Library("Corte Madera Library"));
        });
        verify(libraryRepository, never()).save(any(Library.class));
    }

    @Test
    void getCheckableAmount_checkableExistsAndIsAvailable() {
        Library library = libraries.get(0);
        Checkable checkable = checkables.get(0);

        when(checkableService.getByIsbn(checkable.getIsbn())).thenReturn(checkable);
        when(libraryRepository.findByName(library.getName())).thenReturn(Optional.of(library));

        CheckableAmount checkableAmount = libraryService.getCheckableAmount(library.getName(), checkable.getIsbn());

        assertEquals(2, checkableAmount.getAmount());
        assertEquals(checkable.getIsbn(), checkableAmount.getCheckable().getIsbn());
    }

    @Test
    void getCheckableAmount_checkableExistsAndIsNotAvailable() {
        Library library = libraries.get(0);
        Checkable checkable = checkables.get(5);

        when(checkableService.getByIsbn(checkable.getIsbn())).thenReturn(checkable);
        when(libraryRepository.findByName(library.getName())).thenReturn(Optional.of(library));

        CheckableAmount checkableAmount = libraryService.getCheckableAmount(library.getName(), checkable.getIsbn());

        assertEquals(0, checkableAmount.getAmount());
        assertEquals(checkable.getIsbn(), checkableAmount.getCheckable().getIsbn());
    }

    @Test
    void getCheckableAmount_verifyCheckableObtainedThroughCheckableService() {
        Library library = libraries.get(0);
        Checkable checkable = checkables.get(0);

        when(checkableService.getByIsbn(checkable.getIsbn())).thenReturn(checkable);
        when(libraryRepository.findByName(library.getName())).thenReturn(Optional.of(library));

        libraryService.getCheckableAmount(library.getName(), checkable.getIsbn());

        verify(checkableService).getByIsbn(checkable.getIsbn());
    }

    @Test
    void getLibrariesWithAvailableCheckout() {
        Checkable checkable = checkables.get(0);

        when(checkableService.getByIsbn(checkable.getIsbn())).thenReturn(checkable);
        when(libraryRepository.findAll()).thenReturn(libraries);
        System.out.println(" * * * * * * *  ");
        System.out.println(checkable.getIsbn());
        List<LibraryAvailableCheckouts> libraryAvailableCheckouts = libraryService.getLibrariesWithAvailableCheckout(checkable.getIsbn());
        System.out.println(" - - - - - - - - ");
        System.out.println(libraryAvailableCheckouts.toString());
        assertEquals(2, libraryAvailableCheckouts.get(0).getAvailable());
        assertEquals("Brightmore Public Library", libraryAvailableCheckouts.get(0).getLibraryName());
        assertEquals(1, libraryAvailableCheckouts.get(1).getAvailable());
        assertEquals("Corte Madera Library", libraryAvailableCheckouts.get(1).getLibraryName());

        verify(checkableService, times(1)).getByIsbn(checkable.getIsbn());
    }

    @Test
    void getOverdueCheckouts() {
        Library library = libraries.get(1);

        when(libraryRepository.findByName(library.getName())).thenReturn(Optional.of(library));

        List<OverdueCheckout> overdueCheckouts = libraryService.getOverdueCheckouts(library.getName());

        assertEquals(4, overdueCheckouts.size());
        verify(libraryRepository).findByName(library.getName());
    }
}