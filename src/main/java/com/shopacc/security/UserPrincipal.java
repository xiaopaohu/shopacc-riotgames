package com.shopacc.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetails implementation
 * Đại diện cho user đã authenticated (Staff hoặc Customer)
 */
@Getter
public class UserPrincipal implements UserDetails {

    private final Integer id;
    private final String email;
    private final String passwordHash;
    private final String fullName;
    private final String role;
    private final UserType userType;
    private final boolean deleted;

    public enum UserType {
        STAFF, CUSTOMER
    }

    // Constructor cho Staff
    public static UserPrincipal createStaff(Integer staffId, String email, String passwordHash,
                                            String fullName, String role, boolean deleted) {
        return new UserPrincipal(staffId, email, passwordHash, fullName, role, UserType.STAFF, deleted);
    }

    // Constructor cho Customer
    public static UserPrincipal createCustomer(Integer customerId, String email, String passwordHash,
                                               String fullName, boolean deleted) {
        return new UserPrincipal(customerId, email, passwordHash, fullName, "CUSTOMER", UserType.CUSTOMER, deleted);
    }

    private UserPrincipal(Integer id, String email, String passwordHash, String fullName,
                          String role, UserType userType, boolean deleted) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.userType = userType;
        this.deleted = deleted;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !deleted;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !deleted;
    }

    // Helper methods
    public boolean isStaff() {
        return userType == UserType.STAFF;
    }

    public boolean isCustomer() {
        return userType == UserType.CUSTOMER;
    }

    public boolean hasRole(String checkRole) {
        return this.role.equalsIgnoreCase(checkRole);
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                ", userType=" + userType +
                '}';
    }
}