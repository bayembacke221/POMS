package sn.bmbacke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import sn.bmbacke.repository.EmployeeRepository;

@SpringBootApplication
@EnableAsync
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
