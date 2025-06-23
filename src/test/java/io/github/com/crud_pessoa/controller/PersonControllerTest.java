package io.github.com.crud_pessoa.controller;

import io.github.com.crud_pessoa.dto.AddressRequestDTO;
import io.github.com.crud_pessoa.dto.AddressResponseDTO;
import io.github.com.crud_pessoa.dto.PersonRequestDTO;
import io.github.com.crud_pessoa.dto.PersonResponseDTO;
import io.github.com.crud_pessoa.exception.CpfAlreadyExistsException;
import io.github.com.crud_pessoa.exception.ResourceNotFoundException;
import io.github.com.crud_pessoa.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
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

    private PersonResponseDTO personResponseDTO;
    private PersonRequestDTO personRequestUpdate;
    private PersonRequestDTO personRequestCreate;
    private final Long validPersonId  = 1L;
    private final Long invalidPersonId = 999L;

    @BeforeEach
    void setup() {
        personRequestUpdate = new PersonRequestDTO(
                "João da Silva",
                LocalDate.of(2000, 1, 1),
                "428.209.060-01",
                List.of(
                        new AddressRequestDTO(
                                1L,
                                "Avenida José Carlos Pereira Pinto",
                                "178-A",
                                "Parque Guarus",
                                "Campos dos Goytacazes",
                                "RJ",
                                "28143-000"
                        ),
                        new AddressRequestDTO(
                                2L,
                                "Avenida 28 de Março",
                                "1000",
                                "Centro",
                                "Campos dos Goytacazes",
                                "RJ",
                                "28015-000"
                        ),
                        new AddressRequestDTO(
                                3L,
                                "Avenida Dário Canela",
                                "500",
                                "Centro",
                                "Campos dos Goytacazes",
                                "RJ",
                                "28015-000"
                        ),
                        new AddressRequestDTO(
                                4L,
                                "Rua do Comércio",
                                "300",
                                "Centro",
                                "Campos dos Goytacazes",
                                "RJ",
                                "28015-000"
                        ),
                        new AddressRequestDTO(
                                5L,
                                "Rua da Liberdade",
                                "200",
                                "Centro",
                                "Campos dos Goytacazes",
                                "RJ",
                                "28015-000"
                        )
                )
        );

        personRequestCreate = new PersonRequestDTO(
                "Josias Barreto",
                LocalDate.of(1994, 9, 24),
                "128.363.000-19",
                List.of(
                        new AddressRequestDTO(
                                1L,
                                "Avenida José Carlos Pereira Pinto",
                                "178-A",
                                "Parque Guarus",
                                "Campos dos Goytacazes",
                                "RJ",
                                "28143-000"
                        )
                )
        );

        personResponseDTO = new PersonResponseDTO(
                1L,
                "John Doe",
                LocalDate.of(2000, 1, 1),
                "926.591.480-74",
                List.of(
                        new AddressResponseDTO(
                                1L,
                                "Rua Alvorada",
                                "194",
                                "Pelinca",
                                "Campos dos Goytacazes",
                                "RJ",
                                "28015-000"
                        )
                )
        );
    }

    @Test
    @DisplayName("Deve retornar um produto específico com status OK quando encontrado")
    void shouldReturnSpecificPersonWithStatusOkWhenFound() {
        when(personService.getPersonById(validPersonId)).thenReturn(personResponseDTO);

        ResponseEntity<PersonResponseDTO> response = personController.getPersonById(validPersonId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(personResponseDTO, response.getBody());
        verify(personService, times(1)).getPersonById(1L);
    }

    @Test
    @DisplayName("Deve retornar uma exceção quando o ID da pessoa não for encontrado")
    void shouldReturnExceptionWhenPersonIdNotFound() {
        when(personService.getPersonById(invalidPersonId)).thenThrow(new ResourceNotFoundException("Person with ID " + invalidPersonId + " not found."));

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            personController.getPersonById(invalidPersonId);
        });

        assertEquals("Person with ID " + invalidPersonId + " not found.", exception.getMessage());
        verify(personService, times(1)).getPersonById(invalidPersonId);
    }

    @Test
    @DisplayName("Deve retornar uma página de pessoas com status OK")
    void shouldReturnPageOfPersonsWithStatusOk() {
        var listOfPersons = List.of(new PersonResponseDTO(validPersonId, "John Doe", LocalDate.of(2000, 1, 1), "926.591.480-74", List.of(new AddressResponseDTO(1L, "Rua Alvorada", "194", "Pelinca", "Campos dos Goytacazes", "RJ", "28015-000"))), new PersonResponseDTO(2L, "Jane Smith", LocalDate.of(1995, 5, 15), "123.456.789-00", List.of(new AddressResponseDTO(2L, "Rua das Flores", "1293-A", "Centro", "Rio de Janeiro", "RJ", "20000000"))));

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
        ResponseEntity<Void> response = personController.deletePerson(validPersonId);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(personService, times(1)).deletePerson(validPersonId);
    }

    @Test
    @DisplayName("Deve atualizar uma pessoa com sucesso e retornar a pessoa atualizada")
    void shouldUpdatePersonSuccessfullyAndReturnUpdatedPerson() {
        when(personService.updatePerson(personRequestUpdate, validPersonId)).thenReturn(personResponseDTO);

        ResponseEntity<PersonResponseDTO> response = personController.updatePerson(personRequestUpdate, validPersonId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(personResponseDTO, response.getBody());
        verify(personService, times(1)).updatePerson(personRequestUpdate, validPersonId);
    }

    @Test
    @DisplayName("Deve retornar uma exceção quando o ID da pessoa não for encontrado ao atualizar")
    void shouldReturnExceptionWhenPersonIdNotFoundOnUpdate() {
        when(personService.updatePerson(personRequestUpdate, invalidPersonId)).thenThrow(new ResourceNotFoundException("Person with ID " + invalidPersonId + " not found."));

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            personController.updatePerson(personRequestUpdate, invalidPersonId);
        });

        assertEquals("Person with ID " + invalidPersonId + " not found.", exception.getMessage());
        verify(personService, times(1)).updatePerson(personRequestUpdate, invalidPersonId);
    }

    @Test
    @DisplayName("Deve criar uma pessoa e retornar o Status Code 201")
    void shouldCreatePersonAndReturnSuccess() {
        when(personService.savePerson(personRequestCreate)).thenReturn(personResponseDTO);

        ResponseEntity<PersonResponseDTO> response = personController.createPerson(personRequestCreate);

        assertNotNull(response);
        assertEquals(personResponseDTO, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(personService, times(1)).savePerson(personRequestCreate);
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
        when(personService.savePerson(personRequestCreate)).thenThrow(new CpfAlreadyExistsException("There is already a registered user with the CPF provided: " + personRequestCreate.cpf()));

        Exception exception = assertThrows(CpfAlreadyExistsException.class, () -> {
            personController.createPerson(personRequestCreate);
        });

        assertEquals("There is already a registered user with the CPF provided: " + personRequestCreate.cpf(), exception.getMessage());
        verify(personService, times(1)).savePerson(personRequestCreate);
    }

    @Test
    @DisplayName("Deve calcular a idade de uma pessoa com sucesso")
    void shouldCalculateAgeSuccessfully() {
        String expectedAge = "23 years old";

        when(personService.calculateAgeById(validPersonId)).thenReturn(expectedAge);

        ResponseEntity<String> response = personController.calculateAgeById(validPersonId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedAge, response.getBody());
        verify(personService, times(1)).calculateAgeById(validPersonId);
    }
}