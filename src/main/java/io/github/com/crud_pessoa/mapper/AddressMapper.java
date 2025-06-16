package io.github.com.crud_pessoa.mapper;

import io.github.com.crud_pessoa.dto.AddressRequestDTO;
import io.github.com.crud_pessoa.dto.AddressResponseDTO;
import io.github.com.crud_pessoa.model.Address;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toEntity(AddressRequestDTO dto);
    AddressResponseDTO toDTO(Address entity);
}
