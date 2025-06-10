package com.expensesplitter.expense_splitter_backend.controller;

import com.expensesplitter.expense_splitter_backend.model.dto.response.ApiResponse;
import com.expensesplitter.expense_splitter_backend.model.dto.response.PersonResponse;
import com.expensesplitter.expense_splitter_backend.model.entity.Person;
import com.expensesplitter.expense_splitter_backend.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/people")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    private PersonResponse convertToDto(Person person) {
        PersonResponse dto = new PersonResponse();
        dto.setId(person.getId());
        dto.setName(person.getName());
        dto.setCreatedAt(person.getCreatedAt());
        return dto;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PersonResponse>>> getAllPersons() {
        List<PersonResponse> personResponses = personService.getAllPersons().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(personResponses, "Successfully retrieved all persons"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PersonResponse>> getPersonById(@PathVariable Long id) {
        Person person = personService.getPersonById(id);
        return ResponseEntity.ok(ApiResponse.success(convertToDto(person), "Successfully retrieved person"));
    }

     @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<PersonResponse>> getPersonByName(@PathVariable String name) {
        Person person = personService.getPersonByName(name);
        return ResponseEntity.ok(ApiResponse.success(convertToDto(person), "Successfully retrieved person by name"));
    }

    // Note: Person creation is typically handled implicitly via Expense creation.
    // Adding an explicit endpoint might be useful for admin purposes or direct person management.
    // @PostMapping
    // public ResponseEntity<ApiResponse<PersonResponse>> createPerson(@RequestBody String name) { // Assuming a simple name string for creation
    //     Person person = personService.createPerson(name);
    //     return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(convertToDto(person), "Person created successfully"));
    // }
}
