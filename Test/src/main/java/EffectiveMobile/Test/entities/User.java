package EffectiveMobile.Test.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name must not be blank")
    @Pattern(
            regexp = "^[A-Za-z]+$",
            message = "First name must contain only English letters"
    )
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Second name must not be blank")
    @Pattern(
            regexp = "^[A-Za-z]+$",
            message = "Second name must contain only English letters"
    )
    @Column(name = "second_name", nullable = false, length = 50)
    private String secondName;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a well-formed email address")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotBlank(message = "First name must not be blank") @Pattern(
            regexp = "^[A-Za-z]+$",
            message = "First name must contain only English letters"
    ) String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(@NotBlank(message = "Second name must not be blank") @Pattern(
            regexp = "^[A-Za-z]+$",
            message = "Second name must contain only English letters"
    ) String secondName) {
        this.secondName = secondName;
    }

    public @NotBlank(message = "Email must not be blank") @Email(message = "Email must be a well-formed email address") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email must not be blank") @Email(message = "Email must be a well-formed email address") String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(@NotBlank(message = "Password must not be blank") String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}