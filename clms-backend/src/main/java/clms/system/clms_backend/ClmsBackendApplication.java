package clms.system.clms_backend;

import clms.system.clms_backend.model.User;
import clms.system.clms_backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ClmsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClmsBackendApplication.class, args);
	}

	/**
	 * Seed a default admin user if none exists.
	 */
	@Bean
	public CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByUsername("admin").isEmpty()) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("admin123"));
				admin.setEmail("admin@clms.local");
				admin.setFirstName("System");
				admin.setLastName("Administrator");
				admin.setRole(User.UserRole.ADMIN);
				admin.setRequestedRole(User.UserRole.ADMIN);
				admin.setActive(true);
				admin.setApproved(true);
				userRepository.save(admin);
			}
		};
	}

}
