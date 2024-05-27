package ru.batorov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import ru.batorov.repository.DepartmentRepository;
import ru.batorov.repository.OrganizationRepository;

@Controller
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentRepository departmentRepository;
    private final OrganizationRepository organizationRepository;

    @MutationMapping
    public Department newDepartment(@Argument DepartmentInput department) {
        Organization organization = organizationRepository
                .findById(department.getOrganizationId()).get();
        return departmentRepository.save(new Department(null, department.getName(), null, organization));
    }

    @QueryMapping
    public Iterable<Department> departments(DataFetchingEnvironment environment) {
        DataFetchingFieldSelectionSet s = environment.getSelectionSet();
        List<Specification<Department>> specifications = new ArrayList<>();
        if (s.contains("employees") && !s.contains("organization"))
            return departmentRepository.findAll(fetchEmployees());
        else if (!s.contains("employees") && s.contains("organization"))
            return departmentRepository.findAll(fetchOrganization());
        else if (s.contains("employees") && s.contains("organization"))
            return departmentRepository.findAll(fetchEmployees().and(fetchOrganization()));
        else
            return departmentRepository.findAll();
    }

    @QueryMapping
    public Department department(@Argument Integer id, DataFetchingEnvironment environment) {
        Specification<Department> spec = byId(id);
        DataFetchingFieldSelectionSet selectionSet = environment
                .getSelectionSet();
        if (selectionSet.contains("employees"))
            spec = spec.and(fetchEmployees());
        if (selectionSet.contains("organization"))
            spec = spec.and(fetchOrganization());
        return departmentRepository.findOne(spec).orElseThrow(NoSuchElementException::new);
    }

    private Specification<Department> fetchOrganization() {
        return (root, query, builder) -> {
            Fetch<Department, Organization> f = root
                    .fetch("organization", JoinType.LEFT);
            Join<Department, Organization> join = (Join<Department, Organization>) f;
            return join.getOn();
        };
    }

    private Specification<Department> fetchEmployees() {
        return (root, query, builder) -> {
            Fetch<Department, Employee> f = root
                    .fetch("employees", JoinType.LEFT);
            Join<Department, Employee> join = (Join<Department, Employee>) f;
            return join.getOn();
        };
    }

    private Specification<Department> byId(Integer id) {
        return (root, query, builder) -> builder.equal(root.get("id"), id);
    }
}
