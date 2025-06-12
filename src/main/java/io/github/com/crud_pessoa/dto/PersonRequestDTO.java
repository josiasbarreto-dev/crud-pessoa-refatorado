package io.github.com.crud_pessoa.dto;

import io.github.com.crud_pessoa.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.List;

public record PersonRequestDTO(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 50, message = "The name must be between 3 and 50 characters long.")
        @Pattern(regexp = "^[A-Z]+(.)*", message = "The name must start with an uppercase letter and can only contain letters and spaces.")
        String name,

        @NotNull(message = "Date of birth cannot be null")
        @PastOrPresent(message = "Date of birth must be in the past or present")
        LocalDate dateOfBirth,


        @CPF(message = "Invalid CPF")
        String cpf,

        @Valid
        @NotNull(message = "Address list cannot be null")
        @Size(min = 1, message = "At least one address is required")
        List<AddressRequestDTO> addresses
) {}
