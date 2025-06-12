package io.github.com.crud_pessoa.mapper;

import io.github.com.crud_pessoa.dto.PersonRequestDTO;
import io.github.com.crud_pessoa.dto.PersonResponseDTO;
import io.github.com.crud_pessoa.model.Address;
import io.github.com.crud_pessoa.model.Person;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = { AddressMapper.class })
public interface PersonMapper {
    @Mapping(target = "id", ignore = true)
    Person toEntity(PersonRequestDTO dto);

    PersonResponseDTO toDTO(Person entity);

    List<PersonResponseDTO> toListDTO(List<Person> entities);

    @AfterMapping
    default void linkAddressToPerson(@MappingTarget Person person) {
        if (person.getAddresses() != null) {
            for (Address address : person.getAddresses()) {
                address.setPerson(person);
            }
        }
    }
}
