package sn.bmbacke;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sn.bmbacke.repository.EmployeeRepository;

@SpringBootApplication
public class POMS  implements CommandLineRunner {
    @Autowired private  EmployeeRepository  employeeRepository;
    public static void main(String[] args) {
        SpringApplication.run(POMS.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Employee count: " + employeeRepository.count());
    }
}
