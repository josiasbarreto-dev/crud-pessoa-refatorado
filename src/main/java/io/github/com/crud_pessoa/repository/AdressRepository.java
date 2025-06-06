package io.github.com.crud_pessoa.repository;

import io.github.com.crud_pessoa.model.Adress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdressRepository extends JpaRepository<Adress, Long> {
}
