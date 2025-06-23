package io.github.com.crud_pessoa.repository;

import io.github.com.crud_pessoa.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
