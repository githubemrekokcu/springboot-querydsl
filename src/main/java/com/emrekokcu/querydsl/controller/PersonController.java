package com.emrekokcu.querydsl.controller;

import java.time.Instant;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.emrekokcu.querydsl.model.Person;
import com.emrekokcu.querydsl.model.QPerson;
import com.emrekokcu.querydsl.repository.PersonRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;

@CrossOrigin
@RestController
@RequestMapping(value = "/v1/person")
public class PersonController {

	@Autowired
	private PersonRepository personRepository;
	
    @PersistenceContext
    private EntityManager entityManager;

	@GetMapping(value = "/page")
	public Page<Person> findPerson(@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "500") int size,
			@RequestParam(name = "firstname", required = false) String firstname,
			@RequestParam(name = "lastname", required = false) String lastname,
			@RequestParam(name = "budget", required = false) Integer budget,
			@RequestParam(name = "dobLimit", required = false) Long dobLimit) {
		
		BooleanBuilder booleanBuilder = new BooleanBuilder();
		
		if (firstname != null && !firstname.isEmpty()) {
			booleanBuilder.and(QPerson.person.firstname.eq(firstname));
		}

		if (lastname != null && !lastname.isEmpty()) {
			booleanBuilder.and(QPerson.person.lastname.eq(lastname));
		}

		if (budget != null && budget != 0) {
			booleanBuilder.and(QPerson.person.budget.goe(budget));
		}

		if (dobLimit != null && dobLimit != 0) {
			booleanBuilder.and(QPerson.person.dob.before(Instant.ofEpochSecond(dobLimit)));
		}
		System.out.println(booleanBuilder.getValue());
		return personRepository.findAll(booleanBuilder,
				PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
	}
	
	@GetMapping(value = "/list")
	public List<QPerson> findPersonWithJpaQuery() {
		QPerson qPerson = new QPerson("person");
		JPQLQuery<QPerson> jpaQuery = new JPAQuery<QPerson>(entityManager);
		jpaQuery.from(qPerson).where(qPerson.id.loe(100L).and(qPerson.firstname.eq("Timi")));
		List<QPerson> qPersonList = jpaQuery.fetch();
		return qPersonList;
	}
}
