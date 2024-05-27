package ru.batorov.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.batorov.model.Employee;
import ru.batorov.model.Organization;

public interface OrganizationRepository extends CrudRepository<Organization, Integer>, JpaSpecificationExecutor<Organization> {
}
