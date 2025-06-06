package io.github.com.crud_pessoa.service;

import io.github.com.crud_pessoa.exception.CpfAlreadyExistsException;
import io.github.com.crud_pessoa.model.Person;
import io.github.com.crud_pessoa.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {
    private PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person savePerson(Person person){
        if(personRepository.existsByCPF(person.getCpf())){
            throw new CpfAlreadyExistsException("CPF already registered: " + person.getCpf());
        }
        return personRepository.save(person);
    }
}
