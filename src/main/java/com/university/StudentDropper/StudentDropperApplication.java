package com.university.StudentDropper;

import com.university.StudentDropper.model.Destination;
import com.university.StudentDropper.model.Student;
import com.university.StudentDropper.repository.DestinationRepository;
import com.university.StudentDropper.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class StudentDropperApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentDropperApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(StudentRepository repository, DestinationRepository destinationRepository, PasswordEncoder encoder) {
		return args -> {
			if (repository.count() == 0) {
				String encodedPassword = encoder.encode("1234");
				Student student = new Student();
				student.setAge(23);
				student.setPassword(encodedPassword);
				student.setName("Elmin");
				student.setSurname("Mugalov");
				student.setEmail("elmin@gmail.com");
				student.setRole("ROLE_ADMIN");
				student.setPreferences(null);
				student.setId(null);
				repository.save(student);
			}

			if (destinationRepository.count() == 0) {
				destinationRepository.save(new Destination(null, "Harborview Academy of Arts", 10));
				destinationRepository.save(new Destination(null, "Crestview International College", 10));
				destinationRepository.save(new Destination(null, "Meridian Heights University", 10));
				destinationRepository.save(new Destination(null, "Summit Global Institute", 10));
			}
		};
	}


}
