package com.Documnid.documind_enterprise.auth;

import com.Documnid.documind_enterprise.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1.make all departments available in the system (if not already present)
        List<String[]> enterpriseDepartments = List.of(
                new String[]{"Human Resources (HR)", "Talent acquisition, ATS CV processing and employee management"},
                new String[]{"Finance & Accounting", "Invoices, billing, tax and expenditure audits"},
                new String[]{"Procurement & Supply Chain", "Vendor quotes, purchase orders and goods received notes"},
                new String[]{"Legal & Compliance", "Corporate contracts, non-disclosure agreements and regulatory policies"},
                new String[]{"Operations & Logistics", "Daily operations, consignment tracking and supply logs"},
                new String[]{"IT & Systems", "Core software infrastructure, security and access management"}
        );

        for (String[] deptData : enterpriseDepartments) {
            departmentRepository.findByName(deptData[0])
                    .orElseGet(() -> departmentRepository.save(
                            Department.builder()
                                    .name(deptData[0])
                                    .description(deptData[1])
                                    .build()
                    ));
        }

        // 2. Create a default super admin user if not already present
        if (!userRepository.existsByUsername("admin")) {
            Department itDept = departmentRepository.findByName("IT & Systems").orElse(null);

            User admin = User.builder()
                    .username("admin")
                    .email("admin@documind.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(RoleType.ROLE_SUPER_ADMIN)
                    .department(itDept)
                    .active(true)
                    .build();

            userRepository.save(admin);
            log.info("=================================================");
            log.info(">>> ENTERPRISE DEPARTMENTS & ADMIN INITIALIZED <<<");
            log.info(">>> Username: admin | Password: admin123");
            log.info("=================================================");
        }
    }
}