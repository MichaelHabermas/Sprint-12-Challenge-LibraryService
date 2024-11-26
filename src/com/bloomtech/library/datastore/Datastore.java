package com.bloomtech.library.datastore;

import com.bloomtech.library.models.*;
import com.bloomtech.library.models.checkableTypes.*;
import com.bloomtech.library.models.CheckableAmount;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class Datastore {
    private final List<Library> libraries = new ArrayList<>();
    private final List<Patron> patrons = new ArrayList<>();
    private final List<LibraryCard> libraryCards = new ArrayList<>();
    private final List<Checkable> checkables = new ArrayList<>();

    public List<Checkable> getCheckables() {
        return checkables;
    }
    public List<Library> getLibraries() {
        return libraries;
    }
    public List<Patron> getPatrons() {
        return patrons;
    }
    public List<LibraryCard> getLibraryCards() {
        return libraryCards;
    }
}
