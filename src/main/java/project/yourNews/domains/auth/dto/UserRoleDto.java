package project.yourNews.domains.auth.dto;

import lombok.Getter;

@Getter
public class UserRoleDto {

    private final String role;

    public UserRoleDto(String role) {
        this.role = role;
    }
}
