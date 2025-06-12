package io.github.com.crud_pessoa.service;

import io.github.com.crud_pessoa.dto.PersonRequestDTO;
import io.github.com.crud_pessoa.dto.PersonResponseDTO;
import io.github.com.crud_pessoa.exception.CpfAlreadyExistsException;
import io.github.com.crud_pessoa.exception.CpfMismatchException;
import io.github.com.crud_pessoa.exception.ResourceNotFoundException;
import io.github.com.crud_pessoa.mapper.PersonMapper;
import io.github.com.crud_pessoa.model.Person;
import io.github.com.crud_pessoa.repository.PersonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {
    private final PersonRepository repository;
    private final PersonMapper mapper;

    public PersonService(PersonRepository repository, PersonMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public PersonResponseDTO savePerson(PersonRequestDTO dto) {
        Person person = mapper.toEntity(dto);
        if (repository.existsByCpf(person.getCpf())) {
            throw new CpfAlreadyExistsException("There is already a registered user with the CPF provided: " + person.getCpf());
        }

        Person savedPerson = repository.save(person);
        return mapper.toDTO(savedPerson);
    }

    public PersonResponseDTO getPersonById(Long id) {
        Person person = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person with ID " + id + " not found."));
        return mapper.toDTO(person);
    }

    public Page<PersonResponseDTO> getAllPersons(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var personsPage = repository.findAll(pageable);

        return personsPage.map(mapper::toDTO);
    }

    public void deletePerson(Long id) {
        Person person = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person with ID " + id + " not found."));
        repository.delete(person);
    }

    public PersonResponseDTO updatePerson(PersonRequestDTO dto, Long id) {
        Person currentPerson = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));

        if (!currentPerson.getCpf().equals(dto.cpf())) {
            throw new CpfMismatchException("CPF mismatch.");
        }

        Person personToUpdate = mapper.toEntity(dto);
        personToUpdate.setId(id);
        personToUpdate.getAddresses().forEach(a -> a.setPerson(personToUpdate));

        Person savedPerson = repository.save(personToUpdate);
        return mapper.toDTO(savedPerson);
    }

    public String calculateAgeById(Long id) {
        Person person = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa com ID " + id + " não encontrada."));
        return "A idade de " + person.getName() + " é: " + person.getPersonAge() + " anos.";
    }
}
