package com.expensesplitter.expense_splitter_backend.service;

import com.expensesplitter.expense_splitter_backend.exception.ResourceNotFoundException;
import com.expensesplitter.expense_splitter_backend.model.entity.Person;
import com.expensesplitter.expense_splitter_backend.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Transactional
    public Person findOrCreatePerson(String name) {
        Optional<Person> existingPerson = personRepository.findByName(name);
        return existingPerson.orElseGet(() -> personRepository.save(new Person(name)));
    }

    @Transactional(readOnly = true)
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Person getPersonById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Person getPersonByName(String name) {
        return personRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with name: " + name));
    }

    @Transactional
    public Person createPerson(String name) {
        if (personRepository.existsByName(name)) {
            throw new IllegalArgumentException("Person with name '" + name + "' already exists.");
        }
        Person person = new Person(name);
        return personRepository.save(person);
    }
}
