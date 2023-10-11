package guru.qa.niffler.db.model.auth;

import guru.qa.niffler.model.UserJson;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static jakarta.persistence.FetchType.EAGER;

@Entity
@Table(name = "\"user\"")
public class AuthUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(name = "account_non_expired", nullable = false)
    private Boolean accountNonExpired;

    @Column(name = "account_non_locked", nullable = false)
    private Boolean accountNonLocked;

    @Column(name = "credentials_non_expired", nullable = false)
    private Boolean credentialsNonExpired;

    @OneToMany(fetch = EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private List<AuthorityEntity> authorities = new ArrayList<>();

    public AuthUserEntity() {
    }

    public AuthUserEntity(AuthUserEntity other) {
        this.id = other.id;
        this.username = other.username;
        this.password = other.password;
        this.enabled = other.enabled;
        this.accountNonExpired = other.accountNonExpired;
        this.accountNonLocked = other.accountNonLocked;
        this.credentialsNonExpired = other.credentialsNonExpired;
        this.authorities = new ArrayList<>();
        for (AuthorityEntity authority : other.getAuthorities()) {
            AuthorityEntity newAuthority = new AuthorityEntity(authority);
            newAuthority.setUser(this); // set the back-reference
            this.authorities.add(newAuthority);
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public Boolean getAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public Boolean getCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public List<AuthorityEntity> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<AuthorityEntity> authorities) {
        this.authorities = authorities;
    }

    public void addAuthorities(AuthorityEntity... authorities) {
        this.authorities.addAll(List.of(authorities));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthUserEntity that = (AuthUserEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(enabled, that.enabled) && Objects.equals(accountNonExpired, that.accountNonExpired) && Objects.equals(accountNonLocked, that.accountNonLocked) && Objects.equals(credentialsNonExpired, that.credentialsNonExpired) && Objects.equals(authorities, that.authorities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, enabled, accountNonExpired, accountNonLocked, credentialsNonExpired, authorities);
    }

    public static AuthUserEntity fromUserJson(UserJson userJson) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(userJson.getUsername());
        return authUser;
    }
}
