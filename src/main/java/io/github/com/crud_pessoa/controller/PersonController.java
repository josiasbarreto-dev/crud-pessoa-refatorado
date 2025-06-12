package io.github.com.crud_pessoa.controller;

import io.github.com.crud_pessoa.dto.PersonRequestDTO;
import io.github.com.crud_pessoa.dto.PersonResponseDTO;
import io.github.com.crud_pessoa.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(person);
    }

    @GetMapping
    public ResponseEntity<Page<PersonResponseDTO>> listPersons( @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Page<PersonResponseDTO> persons = service.getAllPersons(page, size);
        return ResponseEntity.ok(persons);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        service.deletePerson(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonResponseDTO> updatePerson(@RequestBody @Valid PersonRequestDTO requestDTO, @PathVariable Long id) {
        PersonResponseDTO updatedPerson = service.updatePerson(requestDTO, id);
        return ResponseEntity.ok(updatedPerson);
    }

    @GetMapping("/age/{id}")
    public ResponseEntity<String> calculateAgeById(@PathVariable Long id){
        String calculatedAge = service.calculateAgeById(id);
        return ResponseEntity.ok(calculatedAge);
    }
}
