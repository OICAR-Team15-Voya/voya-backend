package hr.algebra.voyabackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//    PasswordEncoder is used to encode the password before saving it to the database.
//    BCryptPasswordEncoder is a strong password encoder that uses a salt to increase the security of the password.
//    This class provides us with done methods for hashing and verifying passwords.
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}