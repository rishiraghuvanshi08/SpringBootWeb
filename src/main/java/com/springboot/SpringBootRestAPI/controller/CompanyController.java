package com.springboot.SpringBootRestAPI.controller;

import com.springboot.SpringBootRestAPI.entity.Company;
import com.springboot.SpringBootRestAPI.entity.Employee;
import com.springboot.SpringBootRestAPI.repository.CompanyRepository;
import com.springboot.SpringBootRestAPI.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    EmployeeRepository employeeRepository;

//   1. To Get Employee
    @GetMapping("/getEmp")
    public List<Employee> getEmployee(){
        return employeeRepository.findAll();
    }

//   2. To Get Company
    @GetMapping("/getComp")
    public List<Company> getCompany(){
        return companyRepository.findAll();
    }

//   3. Adding a new Company
    @PostMapping("/addCompany")
    public Company createCompany(@RequestBody Company company){
        if(company.getEmployeeList().isEmpty()){
            return companyRepository.save(company);
        }
        else{
            List<Employee> employeeList = company.getEmployeeList();
            employeeList.stream().forEach(e -> e.setCompany(company));  //for each employee setting current company

            company.setEmployeeList(employeeList);  //for this company setting the listOfEmployee
            return companyRepository.save(company);
        }
    }

//   4. Adding Employee by Company ID
    @PostMapping("/addEmployee/{id}")
    public Employee createEmployee(@PathVariable long id, @RequestBody Employee employee){
        Optional<Company> company = companyRepository.findById(id);

        if(company.isPresent()){
            employee.setCompany(company.get());

            return employeeRepository.save(employee);
        }
        else{
            return null;
        }
    }

//   5. Delete Employee by Employee ID
    @DeleteMapping("/deleteEmp/{id}")
    public String deleteEmployeeById(@PathVariable long id){
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);

        if(optionalEmployee.isPresent()) {
            employeeRepository.deleteById(id);
            return "Deleted..";
        }
        else{
            return "Employee Not Found";
        }
    }

//   6. Deleting Company by ID - CASCADE
    @DeleteMapping("/deleteComp/{id}")
    public String deleteCompanyById(@PathVariable long id){
        Optional<Company> optionalCompany = companyRepository.findById(id);

        if(optionalCompany.isPresent()) {
            companyRepository.deleteById(id);
            return "Deleted..";
        }
        else{
            return "Company Not Found";
        }
    }

//   7. Getting Employee by CompanyID - Specific method
    @GetMapping("/{id}/getEmployee")
    public List<Employee> getAllEmployees(@PathVariable long id){
        return employeeRepository.findByCompanyId(id);
    }

//   8. Update Company By ID
    @PutMapping("/updateCompany/{id}")
    public Company updateCompany(@PathVariable long id, @RequestBody Company updatedCompany){
        Optional<Company> optionalCompany = companyRepository.findById(id);

        if(optionalCompany.isPresent()){
            Company existingCompany = optionalCompany.get();

            updatedCompany.getEmployeeList().forEach(e -> e.setCompany(existingCompany));

            existingCompany.setName(updatedCompany.getName());
            existingCompany.setEmployeeList(updatedCompany.getEmployeeList());      // *****

            return companyRepository.save(existingCompany);
        }
        else{
            return null;
        }
    }

//   9. Getting specific employee of specific company
    @GetMapping("/{cid}/getEmployee/{eid}")
    public Employee getEmployee(@PathVariable("cid") long cid, @PathVariable("eid") long eid){
        List<Employee> employeeList = employeeRepository.findByCompanyId(cid);

        Optional<Employee> optionalEmployee = employeeList.stream().filter(e -> e.getId() == eid).findFirst();

        if(optionalEmployee.isPresent()){
            return optionalEmployee.get();
        }
        else{
            return null;
        }
    }

//   10. Delete Employee
    @DeleteMapping("/{cid}/deleteEmployee/{eid}")
    public String deleteEmployee(@PathVariable("cid") long cid, @PathVariable("eid") long eid){
        List<Employee> employeeList = employeeRepository.findByCompanyId(cid);

        Optional<Employee> empToDelete = employeeList.stream().filter(e -> e.getId() == eid).findFirst();

        if(empToDelete.isPresent()){
            Optional<Company> company = companyRepository.findById(cid);

            if (company.isPresent()){
                company.get().getEmployeeList().remove(empToDelete.get());      // *****

                employeeRepository.deleteById(eid);
                return "Deleted Successfully";
            }
            return "failed";
        }
        else{
            return "Employee Not Found";
        }
    }

}
