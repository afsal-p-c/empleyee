package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.EmployeeDTO;
import com.example.employeemanagementsystem.entity.*;
import com.example.employeemanagementsystem.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeDefaultEntities() {
        // Add default roles
        addRoleIfNotExists("STAFF");
        addRoleIfNotExists("ADMIN");

        // Add default countries, states, cities, departments, designations, and grades
        addCountryIfNotExists("USA");
        addCountryIfNotExists("IND");
        addStateIfNotExists("California");
        addStateIfNotExists("Kerala");
        addCityIfNotExists("San Francisco");
        addCityIfNotExists("Malappuram");
        addDepartmentIfNotExists("IT");
        addDepartmentIfNotExists("PT");
        addDesignationIfNotExists("Software Engineer");
        addDesignationIfNotExists("Sports Engineer");
        addGradeIfNotExists("Junior");
        addGradeIfNotExists("Senior");
        addLanguageIfNotExists("English");
        addLanguageIfNotExists("Spanish");


//        createDefaultUser("STAFF", "STAFF", "newstaff@example.com", "NewStaff", "User", "Office", "IND", "Kerala", "Malappuram", "IT", "Software Engineer", "Junior", List.of("English"));
        createDefaultUser("ADMIN", "ADMIN", "admin@example.com", "Admin", "User", "Headquarters", "USA", "California", "San Francisco", "PT", "Sports Engineer", "Senior", List.of("English", "Spanish"));
    }

    private void addRoleIfNotExists(String roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }

    private void addCountryIfNotExists(String countryName) {
        if (!countryRepository.existsByName(countryName)) {
            Country country = new Country();
            country.setName(countryName);
            countryRepository.save(country);
        }
    }

    private void addStateIfNotExists(String stateName) {
        if (!stateRepository.existsByName(stateName)) {
            State state = new State();
            state.setName(stateName);
            stateRepository.save(state);
        }
    }

    private void addCityIfNotExists(String cityName) {
        if (!cityRepository.existsByName(cityName)) {
            City city = new City();
            city.setName(cityName);
            cityRepository.save(city);
        }
    }

    private void addDepartmentIfNotExists(String departmentName) {
        if (!departmentRepository.existsByName(departmentName)) {
            Department department = new Department();
            department.setName(departmentName);
            departmentRepository.save(department);
        }
    }

    private void addDesignationIfNotExists(String designationName) {
        if (!designationRepository.existsByName(designationName)) {
            Designation designation = new Designation();
            designation.setName(designationName);
            designationRepository.save(designation);
        }
    }

    private void addGradeIfNotExists(String gradeName) {
        if (!gradeRepository.existsByName(gradeName)) {
            Grade grade = new Grade();
            grade.setName(gradeName);
            gradeRepository.save(grade);
        }
    }

    private void addLanguageIfNotExists(String languageName) {
        if (!languageRepository.existsByName(languageName)) {
            Language language = new Language();
            language.setName(languageName);
            languageRepository.save(language);
        }
    }

    private void createDefaultUser(String roleName, String password, String email, String firstName, String lastName, String place, String countryName, String stateName, String cityName, String departmentName, String designationName, String gradeName, List<String> languages) {
        if (employeeRepository.existsByEmail(email)) {
            return;
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        Country country = countryRepository.findByName(countryName)
                .orElseThrow(() -> new RuntimeException("Country not found: " + countryName));
        State state = stateRepository.findByName(stateName)
                .orElseThrow(() -> new RuntimeException("State not found: " + stateName));
        City city = cityRepository.findByName(cityName)
                .orElseThrow(() -> new RuntimeException("City not found: " + cityName));
        Department department = departmentRepository.findByName(departmentName)
                .orElseThrow(() -> new RuntimeException("Department not found: " + departmentName));
        Designation designation = designationRepository.findByName(designationName)
                .orElseThrow(() -> new RuntimeException("Designation not found: " + designationName));
        Grade grade = gradeRepository.findByName(gradeName)
                .orElseThrow(() -> new RuntimeException("Grade not found: " + gradeName));
        List<Language> languageEntities = languages.stream()
                .map(lang -> languageRepository.findByName(lang)
                        .orElseThrow(() -> new RuntimeException("Language not found: " + lang)))
                .collect(Collectors.toList());

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setFirstName(firstName);
        employeeDTO.setLastName(lastName);
        employeeDTO.setPlace(place);
        employeeDTO.setCountry(country.getName());
        employeeDTO.setState(state.getName());
        employeeDTO.setCity(city.getName());
        employeeDTO.setRole(role.getName());
        employeeDTO.setDepartment(department.getName());
        employeeDTO.setDesignation(designation.getName());
        employeeDTO.setGrade(grade.getName());
        employeeDTO.setLanguages(languages);
        employeeDTO.setEmail(email);
        employeeDTO.setPhone("123-456-7890");
        employeeDTO.setPassword(password);

        createEmployee(employeeDTO);
    }
    @Cacheable(value = "employees")
    public Page<EmployeeDTO> getAllEmployees(Specification<Employee> spec, Pageable pageable) {
        Page<Employee> employees = employeeRepository.findAll(spec, pageable);
        return employees.map(this::convertToDTO);
    }

    @Cacheable(value = "employees", key = "#id")
    public EmployeeDTO getEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return convertToDTO(employee);
    }

    @Transactional
    @CacheEvict(value = "employees", allEntries = true)
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        if (employeeRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        Employee employee = convertToEntity(employeeDTO);
        employee.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDTO(savedEmployee);
    }

    @Transactional
    @CacheEvict(value = "employees", key = "#id")
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        updateEmployeeFromDTO(employee, employeeDTO);
        Employee updatedEmployee = employeeRepository.save(employee);
        return convertToDTO(updatedEmployee);
    }

    @Transactional
    @CacheEvict(value = "employees", key = "#id")
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public boolean isOwnProfile(Long id, String email) {
        return employeeRepository.findById(id)
                .map(employee -> employee.getEmail().equals(email))
                .orElse(false);
    }

    private EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setPlace(employee.getPlace());
        dto.setCountry(employee.getCountry().getName());
        dto.setState(employee.getState().getName());
        dto.setCity(employee.getCity().getName());
        dto.setRole(employee.getRole().getName());
        dto.setDepartment(employee.getDepartment().getName());
        dto.setDesignation(employee.getDesignation().getName());
        dto.setGrade(employee.getGrade().getName());
        dto.setLanguages(employee.getLanguages().stream().map(Language::getName).collect(Collectors.toList()));
        dto.setEmail(employee.getEmail());
        dto.setPhone(employee.getPhone());
        return dto;
    }

    private Employee convertToEntity(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        updateEmployeeFromDTO(employee, employeeDTO);
        return employee;
    }

    private void updateEmployeeFromDTO(Employee employee, EmployeeDTO employeeDTO) {
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setPlace(employeeDTO.getPlace());
        employee.setCountry(countryRepository.findByName(employeeDTO.getCountry())
                .orElseThrow(() -> new RuntimeException("Country not found")));
        employee.setState(stateRepository.findByName(employeeDTO.getState())
                .orElseThrow(() -> new RuntimeException("State not found")));
        employee.setCity(cityRepository.findByName(employeeDTO.getCity())
                .orElseThrow(() -> new RuntimeException("City not found")));
        employee.setRole(roleRepository.findByName(employeeDTO.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found")));
        employee.setDepartment(departmentRepository.findByName(employeeDTO.getDepartment())
                .orElseThrow(() -> new RuntimeException("Department not found")));
        employee.setDesignation(designationRepository.findByName(employeeDTO.getDesignation())
                .orElseThrow(() -> new RuntimeException("Designation not found")));
        employee.setGrade(gradeRepository.findByName(employeeDTO.getGrade())
                .orElseThrow(() -> new RuntimeException("Grade not found")));
        employee.setLanguages(employeeDTO.getLanguages().stream()
                .map(lang -> languageRepository.findByName(lang)
                        .orElseThrow(() -> new RuntimeException("Language not found")))
                .collect(Collectors.toList()));
        employee.setEmail(employeeDTO.getEmail());
        employee.setPhone(employeeDTO.getPhone());
        if (employeeDTO.getPassword() != null && !employeeDTO.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        }
    }
}
