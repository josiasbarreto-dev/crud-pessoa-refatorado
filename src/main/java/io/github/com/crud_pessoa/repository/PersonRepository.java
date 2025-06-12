package io.github.com.crud_pessoa.repository;

import io.github.com.crud_pessoa.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
    boolean existsByCpf(String cpf);
    Page<Person> findAll(Pageable pageable);
}
