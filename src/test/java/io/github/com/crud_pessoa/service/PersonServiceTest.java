package io.github.com.crud_pessoa.service;

import io.github.com.crud_pessoa.dto.AddressRequestDTO;
import io.github.com.crud_pessoa.dto.AddressResponseDTO;
import io.github.com.crud_pessoa.dto.PersonRequestDTO;
import io.github.com.crud_pessoa.dto.PersonResponseDTO;
import io.github.com.crud_pessoa.exception.CpfAlreadyExistsException;
import io.github.com.crud_pessoa.exception.CpfMismatchException;
import io.github.com.crud_pessoa.exception.ResourceNotFoundException;
import io.github.com.crud_pessoa.mapper.PersonMapper;
import io.github.com.crud_pessoa.model.Address;
import io.github.com.crud_pessoa.model.Person;
import io.github.com.crud_pessoa.repository.PersonRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {
    @Mock
    private PersonRepository personRepository;

    @Mock
    private PersonMapper mapper;

    @InjectMocks
    private PersonService personService;

    @Test
    @DisplayName("Deve criar uma pessoa com sucesso com CPF único e válido")
    public void shouldCreatePersonSuccessfullyWithUniqueAndValidCpf() {
        var personRequestDTO = new PersonRequestDTO("Josias Barreto", LocalDate.of(1994, 9, 24), "128.363.000-19", List.of(new AddressRequestDTO(null, "Avenida José Carlos Pereira Pinto", "178-A", "Parque Guarus", "Campos dos Goytacazes", "RJ", "28143-000")));

        Person entityToSave = new Person(null, // ID nulo antes de salvar
                "Josias Barreto", LocalDate.of(1994, 9, 24), "128.363.000-19", List.of(new Address(1L, "Avenida José Carlos Pereira Pinto", "178-A", "Parque Guarus", "Campos dos Goytacazes", "RJ", "28143-000")));

        Person savedPersonEntity = new Person(1L, "Josias Barreto", LocalDate.of(1994, 9, 24), "128.363.000-19", List.of(new Address(1L, "Avenida José Carlos Pereira Pinto", "178-A", "Parque Guarus", "Campos dos Goytacazes", "RJ", "28143-000")));

        PersonResponseDTO expectedResponseDTO = new PersonResponseDTO(1L, "Josias Barreto", LocalDate.of(1994, 9, 24), "128.363.000-19", List.of(new AddressResponseDTO(1L, "Avenida José Carlos Pereira Pinto", "178-A", "Parque Guarus", "Campos dos Goytacazes", "RJ", "28143-000")));
        when(mapper.toEntity(any(PersonRequestDTO.class))).thenReturn(entityToSave);
        when(personRepository.existsByCpf(entityToSave.getCpf())).thenReturn(false);
        when(personRepository.save(any(Person.class))).thenReturn(savedPersonEntity);
        when(mapper.toDTO(savedPersonEntity)).thenReturn(expectedResponseDTO);

        PersonResponseDTO actualResponse = personService.savePerson(personRequestDTO);

        assertNotNull(actualResponse);
        assertEquals(expectedResponseDTO.id(), actualResponse.id());
        assertEquals(expectedResponseDTO.name(), actualResponse.name());
        assertEquals(expectedResponseDTO.dateOfBirth(), actualResponse.dateOfBirth());
        assertEquals(expectedResponseDTO.cpf(), actualResponse.cpf());
        assertNotNull(actualResponse.addresses());
        assertEquals(expectedResponseDTO.addresses().size(), actualResponse.addresses().size());
        assertEquals(expectedResponseDTO.addresses().get(0).street(), actualResponse.addresses().get(0).street());
        assertEquals(expectedResponseDTO.addresses().get(0).number(), actualResponse.addresses().get(0).number());
        assertEquals(expectedResponseDTO.addresses().get(0).neighborhood(), actualResponse.addresses().get(0).neighborhood());
        assertEquals(expectedResponseDTO.addresses().get(0).city(), actualResponse.addresses().get(0).city());
        assertEquals(expectedResponseDTO.addresses().get(0).state(), actualResponse.addresses().get(0).state());
        assertEquals(expectedResponseDTO.addresses().get(0).zipCode(), actualResponse.addresses().get(0).zipCode());

        verify(mapper, times(1)).toEntity(personRequestDTO);
        verify(personRepository, times(1)).existsByCpf(entityToSave.getCpf());
        verify(personRepository, times(1)).save(entityToSave);
        verify(mapper, times(1)).toDTO(savedPersonEntity);
        verifyNoMoreInteractions(personRepository, mapper);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar uma pessoa com CPF já existente")
    public void shouldThrowExceptionWhenCreatingPersonWithExistingCpf() {
        var personRequestDTO = new PersonRequestDTO("Maria Silva", LocalDate.of(1990, 5, 20), "123.456.789-00", List.of(new AddressRequestDTO(null, "Rua Exemplo", "123", "Bairro Exemplo", "Cidade Exemplo", "SP", "12345-678")));

        Person entityToSave = new Person(null, "Maria Silva", LocalDate.of(1990, 5, 20), "123.456.789-00", List.of(new Address(1L, "Rua Exemplo", "123", "Bairro Exemplo", "Cidade Exemplo", "SP", "12345-678")));

        when(mapper.toEntity(any(PersonRequestDTO.class))).thenReturn(entityToSave);
        when(personRepository.existsByCpf(entityToSave.getCpf())).thenReturn(true);

        try {
            personService.savePerson(personRequestDTO);
        } catch (CpfAlreadyExistsException e) {
            assertEquals("There is already a registered user with the CPF provided: 123.456.789-00", e.getMessage());
        }

        verify(mapper, times(1)).toEntity(personRequestDTO);
        verify(personRepository, times(1)).existsByCpf(entityToSave.getCpf());
        verifyNoMoreInteractions(personRepository, mapper);
    }

    @Test
    @DisplayName("Quando buscar uma pessoa pelo ID, deve retornar a pessoa correspondente")
    void shouldReturnPersonById() {
        Long personID = 1L;
        Person expectedPerson = new Person(personID, "John Doe", LocalDate.of(2000, 1, 1), "926.591.480-74", new ArrayList<>());
        PersonResponseDTO expectedResponseDTO = new PersonResponseDTO(personID, "John Doe", LocalDate.of(2000, 1, 1), "926.591.480-74", new ArrayList<>());

        when(personRepository.findById(personID)).thenReturn(Optional.of(expectedPerson));
        when(mapper.toDTO(expectedPerson)).thenReturn(expectedResponseDTO);

        PersonResponseDTO actualResponse = personService.getPersonById(personID);

        assertNotNull(actualResponse);
        assertEquals(expectedResponseDTO.id(), actualResponse.id());
        assertEquals(expectedResponseDTO.name(), actualResponse.name());
        assertEquals(expectedResponseDTO.dateOfBirth(), actualResponse.dateOfBirth());
        assertEquals(expectedResponseDTO.cpf(), actualResponse.cpf());

        verify(personRepository, times(1)).findById(personID);
        verify(mapper, times(1)).toDTO(expectedPerson);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao buscar uma pessoa com ID inexistente")
    void shouldThrowExceptionWhenPersonNotFoundById() {
        Long nonExistentId = 10L;
        when(personRepository.findById(nonExistentId)).thenThrow(new ResourceNotFoundException("Person with ID " + nonExistentId + " not found."));
        try {
            personService.getPersonById(nonExistentId);
        } catch (ResourceNotFoundException e) {
            assertEquals("Person with ID " + nonExistentId + " not found.", e.getMessage());
        }
        verify(personRepository, times(1)).findById(nonExistentId);
        verifyNoMoreInteractions(mapper);
    }

    @Test
    @DisplayName("Deve deletar uma pessoa com sucesso quando o ID for válido")
    void shouldDeletePersonSuccessfullyWhenIdIsValid() {
        Long personID = 1L;
        Person personToDelete = new Person(personID, "Jane Doe", LocalDate.of(1995, 5, 15), "123.456.789-00", new ArrayList<>());

        when(personRepository.findById(personID)).thenReturn(Optional.of(personToDelete));

        personService.deletePerson(personID);

        verify(personRepository, times(1)).findById(personID);
        verify(personRepository, times(1)).delete(personToDelete);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar deletar uma pessoa com ID inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentPerson() {
        Long nonExistentId = 10L;
        when(personRepository.findById(nonExistentId)).thenThrow(new ResourceNotFoundException("Person with ID " + nonExistentId + " not found."));

        try {
            personService.deletePerson(nonExistentId);
        } catch (ResourceNotFoundException e) {
            assertEquals("Person with ID " + nonExistentId + " not found.", e.getMessage());
        }

        verify(personRepository, times(1)).findById(nonExistentId);
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    @DisplayName("Deve retornar uma lista de pessoas paginada com sucesso")
    void shouldReturnPagedListOfPersonsSuccessfully() {
        int page = 0;
        int size = 10;
        List<Person> persons = List.of(
                new Person(1L, "Alice", LocalDate.of(1990, 1, 1), "123.456.789-00", new ArrayList<>()),
                new Person(2L, "Bob", LocalDate.of(1992, 2, 2), "987.654.321-00", new ArrayList<>())
        );
        Page<Person> pagedPersons = new PageImpl<>(persons, PageRequest.of(page, size), persons.size());

        when(personRepository.findAll(PageRequest.of(page, size))).thenReturn(pagedPersons);
        when(mapper.toDTO(any(Person.class))).thenAnswer(invocation -> {
            Person person = invocation.getArgument(0);
            return new PersonResponseDTO(person.getId(), person.getName(), person.getDateOfBirth(), person.getCpf(), new ArrayList<>());
        });

        Page<PersonResponseDTO> actualPage = personService.getAllPersons(page, size);

        assertNotNull(actualPage);
        assertEquals(pagedPersons.getTotalElements(), actualPage.getTotalElements());
        assertEquals(pagedPersons.getContent().size(), actualPage.getContent().size());
        assertEquals("Alice", actualPage.getContent().get(0).name());
        assertEquals("Bob", actualPage.getContent().get(1).name());

        verify(personRepository, times(1)).findAll(PageRequest.of(page, size));
        verify(mapper, times(2)).toDTO(any(Person.class));
    }

    @Test
    @DisplayName("Deve atualizar uma pessoa com sucesso quando o ID for válido e o CPF coincidir")
    void shouldUpdatePersonSuccessfullyWhenIdIsValidAndCpfMatches() {
        Long personID = 1L;
        PersonRequestDTO personRequestDTO = new PersonRequestDTO("John Doe", LocalDate.of(2000, 1, 1), "926.591.480-74", new ArrayList<>());
        Person existingPerson = new Person(personID, "John Doe", LocalDate.of(2000, 1, 1), "926.591.480-74", new ArrayList<>());
        Person updatedPerson = new Person(personID, "John Doe Updated", LocalDate.of(2000, 1, 1), "926.591.480-74", new ArrayList<>());
        PersonResponseDTO expectedResponseDTO = new PersonResponseDTO(personID, "John Doe Updated", LocalDate.of(2000, 1, 1), "926.591.480-74", new ArrayList<>());

        when(personRepository.findById(personID)).thenReturn(Optional.of(existingPerson));
        when(mapper.toEntity(personRequestDTO)).thenReturn(updatedPerson);
        when(personRepository.save(updatedPerson)).thenReturn(updatedPerson);
        when(mapper.toDTO(updatedPerson)).thenReturn(expectedResponseDTO);

        PersonResponseDTO actualResponse = personService.updatePerson(personRequestDTO, personID);

        assertNotNull(actualResponse);
        assertEquals(expectedResponseDTO.id(), actualResponse.id());
        assertEquals(expectedResponseDTO.name(), actualResponse.name());
        assertEquals(expectedResponseDTO.dateOfBirth(), actualResponse.dateOfBirth());
        assertEquals(expectedResponseDTO.cpf(), actualResponse.cpf());

        verify(personRepository, times(1)).findById(personID);
        verify(mapper, times(1)).toEntity(personRequestDTO);
        verify(personRepository, times(1)).save(updatedPerson);
        verify(mapper, times(1)).toDTO(updatedPerson);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar atualizar uma pessoa com ID inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentPerson() {
        Long nonExistentId = 10L;
        PersonRequestDTO personRequestDTO = new PersonRequestDTO("Jane Doe", LocalDate.of(1995, 5, 15), "123.456.789-00", new ArrayList<>());

        when(personRepository.findById(nonExistentId)).thenThrow(new ResourceNotFoundException("Person with ID " + nonExistentId + " not found."));
        try {
            personService.updatePerson(personRequestDTO, nonExistentId);
        } catch (ResourceNotFoundException e) {
            assertEquals("Person with ID " + nonExistentId + " not found.", e.getMessage());
        }
        verify(personRepository, times(1)).findById(nonExistentId);
        verifyNoMoreInteractions(personRepository, mapper);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar atualizar uma pessoa com CPF diferente do existente")
    void shouldThrowExceptionWhenUpdatingPersonWithDifferentCpf() {
        Long personID = 1L;
        PersonRequestDTO personRequestDTO = new PersonRequestDTO("John Doe", LocalDate.of(2000, 1, 1), "123.456.789-00", new ArrayList<>());
        Person existingPerson = new Person(personID, "John Doe", LocalDate.of(2000, 1, 1), "926.591.480-74", new ArrayList<>());

        when(personRepository.findById(personID)).thenReturn(Optional.of(existingPerson));
        try {
            personService.updatePerson(personRequestDTO, personID);
        } catch (CpfMismatchException e) {
            assertEquals("CPF mismatch.", e.getMessage());
        }
        verify(personRepository, times(1)).findById(personID);
        verifyNoMoreInteractions(personRepository, mapper);
    }

    @Test
    @DisplayName("Deve retornar a idade de uma pessoa com sucesso")
    void shouldReturnPersonAgeSuccessfully() {
        Long personID = 1L;
        Person person = new Person(personID, "Alice", LocalDate.of(1990, 1, 1), "123.456.789-00", new ArrayList<>());

        when(personRepository.findById(personID)).thenReturn(Optional.of(person));
        String age = personService.calculateAgeById(personID);
        String expectedAge = "The age of Alice is: " + person.getPersonAge() + " years.";
        assertEquals(expectedAge, age);
    }
}
