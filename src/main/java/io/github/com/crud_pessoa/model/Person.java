package io.github.com.crud_pessoa.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Pessoa")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "NOME", nullable = false)
    private String name;

    @Column(name = "DATA DE ANIVERSÁRIO")
    private LocalDate dateOfBirth;

    @Column(name = "CPF", unique = true)
    private String cpf;

    public Person(long id, String name, LocalDate dateOfBirth, String cpf) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.cpf = cpf;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
