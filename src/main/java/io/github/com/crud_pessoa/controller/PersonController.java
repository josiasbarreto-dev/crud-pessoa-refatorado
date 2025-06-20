package io.github.com.crud_pessoa.controller;

import io.github.com.crud_pessoa.dto.PersonRequestDTO;
import io.github.com.crud_pessoa.dto.PersonResponseDTO;
import io.github.com.crud_pessoa.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/person")
@Tag(name = "API Person", description = "Endpoints for managing persons")
public class PersonController {
    PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }


    @Operation(summary = "Create a new person with one or more addresses", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Person created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Person already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonResponseDTO> createPerson(@RequestBody @Valid PersonRequestDTO requestDTO){
        PersonResponseDTO salvedPerson =  service.savePerson(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvedPerson);
    }

    @Operation(summary = "Get a person by ID", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person found"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonResponseDTO> getPersonById(@PathVariable Long id) {
        PersonResponseDTO person = service.getPersonById(id);
        return ResponseEntity.ok(person);
    }

    @Operation(summary = "List all persons with pagination", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Persons retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<PersonResponseDTO>> listPersons( @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Page<PersonResponseDTO> persons = service.getAllPersons(page, size);
        return ResponseEntity.ok(persons);
    }

    @Operation(summary = "Delete a person and all their addresses", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Person deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        service.deletePerson(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Update a person's details and/or their address(es)", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "409", description = "CPF mismatch"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonResponseDTO> updatePerson(@RequestBody @Valid PersonRequestDTO requestDTO, @PathVariable Long id) {
        PersonResponseDTO updatedPerson = service.updatePerson(requestDTO, id);
        return ResponseEntity.ok(updatedPerson);
    }

    @Operation(summary = "Calculate the age of a person by their ID", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Age calculated successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    @GetMapping(value = "/age/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> calculateAgeById(@PathVariable Long id){
        String calculatedAge = service.calculateAgeById(id);
        return ResponseEntity.ok(calculatedAge);
    }
}
