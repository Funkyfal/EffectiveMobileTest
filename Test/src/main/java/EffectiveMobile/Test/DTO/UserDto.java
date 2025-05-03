package EffectiveMobile.Test.DTO;

import EffectiveMobile.Test.entities.User;

import java.util.Set;

public class UserDto {
    private Long id;
    private String firstName;
    private String secondName;
    private String email;
    private Set<String> roles;
    private boolean enabled;

    public static UserDto fromEntity(User u) {
        UserDto dto = new UserDto();
        dto.setId(u.getId());
        dto.setFirstName(u.getFirstName());
        dto.setSecondName(u.getSecondName());
        dto.setEmail(u.getEmail());
        dto.setRoles(u.getRoles());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
