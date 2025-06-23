package io.github.com.crud_pessoa.dto;


import io.github.com.crud_pessoa.model.Address;

import java.time.LocalDate;
import java.util.List;

public record PersonResponseDTO(
        Long id,
        String name,
        LocalDate dateOfBirth,
        String cpf,
        List<AddressResponseDTO> addresses) {
}
