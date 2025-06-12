package io.github.com.crud_pessoa.dto;

public record AddressResponseDTO(
        Long id,
        String street,
        String number,
        String neighborhood,
        String city,
        String state,
        String zipCode
) {}
