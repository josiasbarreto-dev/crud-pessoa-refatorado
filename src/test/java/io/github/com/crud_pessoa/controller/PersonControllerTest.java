package io.github.com.crud_pessoa.controller;

import io.github.com.crud_pessoa.dto.AddressRequestDTO;
import io.github.com.crud_pessoa.dto.AddressResponseDTO;
import io.github.com.crud_pessoa.dto.PersonRequestDTO;
import io.github.com.crud_pessoa.dto.PersonResponseDTO;
import io.github.com.crud_pessoa.exception.CpfAlreadyExistsException;
import io.github.com.crud_pessoa.exception.ResourceNotFoundException;
import io.github.com.crud_pessoa.service.PersonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Controller Unit Tests")
class PersonControllerTest {

    @InjectMocks
    private PersonController personController;

    @Mock
    private PersonService personService;

    @Test
    @DisplayName("Deve retornar um produto específico com status OK quando encontrado")
    void shouldReturnSpecificPersonWithStatusOkWhenFound() {
        var person = new PersonResponseDTO(1L, "John Doe", LocalDate.of(2000, 1, 1), "926.591.480-74", List.of(new AddressResponseDTO(1L, "Rua Alvorada", "194", "Pelinca", "Campos dos Goytacazes", "RJ", "28015-000"), new AddressResponseDTO(2L, "Rua das Flores", "1293-A", "Centro", "Rio de Janeiro", "RJ", "20000000"), new AddressResponseDTO(3L, "Avenida Brasil", "1000", "Copacabana", "Rio de Janeiro", "RJ", "22000-000"), new AddressResponseDTO(4L, "Rua das Palmeiras", "45", "Jardim Botânico", "Rio de Janeiro", "RJ", "22260-000"), new AddressResponseDTO(5L, "Avenida Atlântica", "200", "Copacabana", "Rio de Janeiro", "RJ", "22021-000")));
        when(personService.getPersonById(1L)).thenReturn(person);

        ResponseEntity<PersonResponseDTO> response = personController.getPersonById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(person, response.getBody());

        verify(personService, times(1)).getPersonById(1L);
    }

    @Test
    @DisplayName("Deve retornar uma exceção quando o ID da pessoa não for encontrado")
    void shouldReturnExceptionWhenPersonIdNotFound() {
        long nonExistentId = 999L;

        when(personService.getPersonById(nonExistentId)).thenThrow(new ResourceNotFoundException("Person with ID " + 999L + " not found."));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            personController.getPersonById(nonExistentId);
        });

        assertEquals("Person with ID " + nonExistentId + " not found.", exception.getMessage());

        verify(personService, times(1)).getPersonById(999L);
    }

    @Test
    @DisplayName("Deve retornar uma página de pessoas com status OK")
    void shouldReturnPageOfPersonsWithStatusOk() {
        var listOfPersons = List.of(new PersonResponseDTO(1L, "John Doe", LocalDate.of(2000, 1, 1), "926.591.480-74", List.of(new AddressResponseDTO(1L, "Rua Alvorada", "194", "Pelinca", "Campos dos Goytacazes", "RJ", "28015-000"))), new PersonResponseDTO(2L, "Jane Smith", LocalDate.of(1995, 5, 15), "123.456.789-00", List.of(new AddressResponseDTO(2L, "Rua das Flores", "1293-A", "Centro", "Rio de Janeiro", "RJ", "20000000"))));

        Pageable pageable = PageRequest.of(0, 10); // página 0, tamanho 10
        Page<PersonResponseDTO> pagedPersons = new PageImpl<>(listOfPersons, pageable, listOfPersons.size());

        when(personService.getAllPersons(pageable.getPageNumber(), pageable.getPageSize())).thenReturn(pagedPersons);

        Page<PersonResponseDTO> result = personController.listPersons(pageable.getPageNumber(), pageable.getPageSize()).getBody();

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("John Doe", result.getContent().get(0).name());
        assertEquals("Jane Smith", result.getContent().get(1).name());

        verify(personService, times(1)).getAllPersons(0, 10);
    }

    @Test
    @DisplayName("Deve deletar uma pessoa com sucesso e retornar status NO_CONTENT")
    void shouldDeletePersonSuccessfullyAndReturnNoContent() {
        long personId = 1L;

        ResponseEntity<Void> response = personController.deletePerson(personId);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(personService, times(1)).deletePerson(personId);
    }

    @Test
    @DisplayName("Deve atualizar uma pessoa com sucesso e retornar a pessoa atualizada")
    void shouldUpdatePersonSuccessfullyAndReturnUpdatedPerson() {
        long personID = 1L;
        var personRequestDTO = new PersonRequestDTO("João da Silva", LocalDate.of(2000, 1, 1), "428.209.060-01", List.of(new AddressRequestDTO(1l, "Avenida José Carlos Pereira Pinto", "178-A", "Parque Guarus", "Campos dos Goytacazes", "RJ", "28143-000"), new AddressRequestDTO(2L, "Avenida 28 de Março", "1000", "Centro", "Campos dos Goytacazes", "RJ", "28015-000"), new AddressRequestDTO(3L, "Avenida Dário Canela", "500", "Centro", "Campos dos Goytacazes", "RJ", "28015-000"), new AddressRequestDTO(4L, "Rua do Comércio", "300", "Centro", "Campos dos Goytacazes", "RJ", "28015-000"), new AddressRequestDTO(5L, "Rua da Liberdade", "200", "Centro", "Campos dos Goytacazes", "RJ", "28015-000")));

        var personUpdated = new PersonResponseDTO(1L, "Josias Barreto Borges", LocalDate.of(2000, 1, 1), "428.209.060-01", List.of(new AddressResponseDTO(1L, "Rua Alvorada", "194", "Pelinca", "Campos dos Goytacazes", "RJ", "28015-000"), new AddressResponseDTO(2L, "Rua das Flores", "1293-A", "Centro", "Rio de Janeiro", "RJ", "20000000"), new AddressResponseDTO(3L, "Avenida Brasil", "1000", "Copacabana", "Rio de Janeiro", "RJ", "22000-000"), new AddressResponseDTO(4L, "Rua das Palmeiras", "45", "Jardim Botânico", "Rio de Janeiro", "RJ", "22260-000"), new AddressResponseDTO(5L, "Avenida Atlântica", "200", "Copacabana", "Rio de Janeiro", "RJ", "22021-000")));

        when(personService.updatePerson(personRequestDTO, personID)).thenReturn(personUpdated);

        ResponseEntity<PersonResponseDTO> response = personController.updatePerson(personRequestDTO, personID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(personUpdated, response.getBody());
        verify(personService, times(1)).updatePerson(personRequestDTO, personID);
    }

    @Test
    @DisplayName("Deve retornar uma exceção quando o ID da pessoa não for encontrado ao atualizar")
    void shouldReturnExceptionWhenPersonIdNotFoundOnUpdate() {
        long nonExistentId = 999L;
        var personRequestDTO = new PersonRequestDTO("João da Silva", LocalDate.of(2000, 1, 1), "428.209.060-01", List.of(new AddressRequestDTO(1l, "Avenida José Carlos Pereira Pinto", "178-A", "Parque Guarus", "Campos dos Goytacazes", "RJ", "28143-000")));

        when(personService.updatePerson(personRequestDTO, nonExistentId)).thenThrow(new ResourceNotFoundException("Person with ID " + nonExistentId + " not found."));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            personController.updatePerson(personRequestDTO, nonExistentId);
        });

        assertEquals("Person with ID " + nonExistentId + " not found.", exception.getMessage());

        verify(personService, times(1)).updatePerson(personRequestDTO, nonExistentId);
    }

    @Test
    @DisplayName("Deve criar uma pessoa e retornar o Status Code 201")
    void shouldCreatePersonAndReturnSuccess() {
        var personRequestDTO = new PersonRequestDTO("Josias Barreto", LocalDate.of(1994, 9, 24), "128.363.000-19", List.of(new AddressRequestDTO(1l, "Avenida José Carlos Pereira Pinto", "178-A", "Parque Guarus", "Campos dos Goytacazes", "RJ", "28143-000")));
        var personCreated = new PersonResponseDTO(1L, "Josias Barreto", LocalDate.of(1994, 9, 24), "128.363.000-19", List.of(new AddressResponseDTO(1l, "Avenida José Carlos Pereira Pinto", "178-A", "Parque Guarus", "Campos dos Goytacazes", "RJ", "28143-000")));

        when(personService.savePerson(personRequestDTO)).thenReturn(personCreated);

        ResponseEntity<PersonResponseDTO> response = personController.createPerson(personRequestDTO);

        assertNotNull(response);
        assertEquals(personCreated, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(personService, times(1)).savePerson(personRequestDTO);
    }

    @Test
    @DisplayName("Deve lançar um exceção ao criar ao criar uma pessoa com CPF inválido")
    void shouldThrowExceptionWhenCreatingPersonWithInvalidCPF() {
        var personRequestDTO = new PersonRequestDTO("Josias Barreto", LocalDate.of(1994, 9, 24), "123.456.789-00", // CPF inválido
                List.of(new AddressRequestDTO(1l, "Avenida José Carlos Pereira Pinto", "178-A", "Parque Guarus", "Campos dos Goytacazes", "RJ", "28143-000")));
        when(personService.savePerson(personRequestDTO)).thenThrow(new IllegalArgumentException("Invalid CPF"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            personController.createPerson(personRequestDTO);
        });

        assertEquals("Invalid CPF", exception.getMessage());
        verify(personService, times(1)).savePerson(personRequestDTO);
    }

    @Test
    @DisplayName("Deve retornar Status Code 409 ao tentar criar uma pessoa com CPF já existente")
    void shouldReturnStatus409WhenCreatingPersonWithExistingCPF() {
        var personRequestDTO = new PersonRequestDTO("Josias Barreto", LocalDate.of(1994, 9, 24), "128.363.000-19", // CPF já existente
                List.of(new AddressRequestDTO(1l, "Avenida José Carlos Pereira Pinto", "178-A", "Parque Guarus", "Campos dos Goytacazes", "RJ", "28143-000")));

        when(personService.savePerson(personRequestDTO)).thenThrow(new CpfAlreadyExistsException("There is already a registered user with the CPF provided: " + personRequestDTO.cpf()));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            personController.createPerson(personRequestDTO);
        });

        assertEquals("There is already a registered user with the CPF provided: " + personRequestDTO.cpf(), exception.getMessage());
        verify(personService, times(1)).savePerson(personRequestDTO);
    }

    @Test
    @DisplayName("Deve calcular a idade de uma pessoa com sucesso")
    void ShouldCalculateAgeSuccessfully() {
        long personID = 1L;
        String expectedAge = "23 years old";

        when(personService.calculateAgeById(personID)).thenReturn(expectedAge);

        ResponseEntity<String> response = personController.calculateAgeById(personID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedAge, response.getBody());
        verify(personService, times(1)).calculateAgeById(personID);
    }
}