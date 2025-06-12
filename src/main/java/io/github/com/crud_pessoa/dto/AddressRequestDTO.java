package io.github.com.crud_pessoa.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequestDTO(
        Long id,

        @NotBlank(message = "The street address cannot be empty")
        @Size(max = 100, message = "The street address must have a maximum of {max} characters")
        String street,

        int number,

        @NotBlank(message = "The neighborhood cannot be empty")
        @Size(max = 50, message = "The neighborhood must have a maximum of {max} characters")
        String neighborhood,

        @NotBlank(message = "The city cannot be empty")
        @Size(max = 50, message = "The city must have a maximum of {max} characters")
        String city,

        @NotBlank(message = "The state cannot be empty")
        @Size(min = 2, max = 2, message = "The state must have 2 characters (e.g., US state abbreviation)")
        @Pattern(regexp = "^[A-Z]{2}$", message = "The state must be a valid two-letter abbreviation (e.g., SP, RJ, CA, NY)")
        String state,

        @NotBlank(message = "The ZIP code cannot be empty")
        @Pattern(regexp = "^[0-9]{5}-[0-9]{3}$|^[0-9]{8}$", message = "The ZIP code must be in the 00000-000 or 00000000 format")
        String zipCode
) {}