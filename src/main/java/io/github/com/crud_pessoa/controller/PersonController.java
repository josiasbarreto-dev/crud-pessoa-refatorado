package io.github.com.crud_pessoa.controller;

import io.github.com.crud_pessoa.dto.PersonRequestDTO;
import io.github.com.crud_pessoa.dto.PersonResponseDTO;
import io.github.com.crud_pessoa.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {
    PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PersonResponseDTO> createPerson(@RequestBody @Valid PersonRequestDTO requestDTO){
        PersonResponseDTO salvedPerson =  service.savePerson(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvedPerson);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonResponseDTO> getPersonById(@PathVariable Long id) {
        PersonResponseDTO person = service.getPersonById(id);
        return ResponseEntity.status(HttpStatus.OK).body(person);
    }

    @GetMapping
    public ResponseEntity<List<PersonResponseDTO>> listPersons() {
        List<PersonResponseDTO> persons = service.getAllPersons();
        return ResponseEntity.status(HttpStatus.OK).body(persons);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        service.deletePerson(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonResponseDTO> updatePerson(@RequestBody @Valid PersonRequestDTO requestDTO, @PathVariable Long id) {
        PersonResponseDTO updatedPerson = service.updatePerson(requestDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedPerson);
    }

    @GetMapping("/age/{id}")
    public ResponseEntity<String> calculateAgeById(@PathVariable Long id){
        String calculatedAge = service.calculateAgeById(id);
        return ResponseEntity.status(HttpStatus.OK).body(calculatedAge);
    }
}
