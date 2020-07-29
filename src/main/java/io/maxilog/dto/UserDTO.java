package io.maxilog.dto;

import org.jose4j.jwt.JwtClaims;

public class UserDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String email;

    public UserDTO() {
    }

    public UserDTO(JwtClaims claims) {
        this.username = claims.getClaimValueAsString("preferred_username");
        this.email = claims.getClaimValueAsString("email");
        this.firstName = claims.getClaimValueAsString("given_name");
        this.lastName = claims.getClaimValueAsString("family_name");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}