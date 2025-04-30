package EffectiveMobile.Test.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class RegisterRequest {

    @NotBlank(message = "First name must not be blank")
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain only English letters")
    @Size(max = 50, message = "First name must be at most 50 characters")
    private String firstName;

    @NotBlank(message = "Second name must not be blank")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Second name must contain only English letters")
    @Size(max = 50, message = "Second name must be at most 50 characters")
    private String secondName;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a well-formed email address")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private Set<@NotBlank String> roles = new HashSet<>(Set.of("ROLE_USER"));

    public RegisterRequest() {
        // default constructor for Jackson
    }

    public RegisterRequest(String firstName,
                           String secondName,
                           String email,
                           String password,
                           Set<String> roles) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.password = password;
        if (roles != null && !roles.isEmpty()) {
            this.roles = roles;
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        if (roles != null && !roles.isEmpty()) {
            this.roles = roles;
        }
    }
}
