package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.mapper.AuthorityEntityRowMapper;
import guru.qa.niffler.db.mapper.UserEntityRowMapper;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.UserEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

public class AuthUserDAOSpringJdbc implements AuthUserDAO, UserDataUserDAO {

    private final TransactionTemplate authTtpl;
    private final TransactionTemplate userdataTtpl;
    private final JdbcTemplate authJdbcTemplate;
    private final JdbcTemplate userdataJdbcTemplate;

    public AuthUserDAOSpringJdbc() {
        JdbcTransactionManager authTm = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.AUTH));
        JdbcTransactionManager userdataTm = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA));

        this.authTtpl = new TransactionTemplate(authTm);
        this.userdataTtpl = new TransactionTemplate(userdataTm);
        this.authJdbcTemplate = new JdbcTemplate(authTm.getDataSource());
        this.userdataJdbcTemplate = new JdbcTemplate(userdataTm.getDataSource());
    }

    @Override
    @SuppressWarnings("unchecked")
    public int createUser(UserEntity user) {
        return authTtpl.execute(status -> {
            KeyHolder kh = new GeneratedKeyHolder();

            authJdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement("INSERT INTO users (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, pe.encode(user.getPassword()));
                ps.setBoolean(3, user.getEnabled());
                ps.setBoolean(4, user.getAccountNonExpired());
                ps.setBoolean(5, user.getAccountNonLocked());
                ps.setBoolean(6, user.getCredentialsNonExpired());
                return ps;
            }, kh);
            final UUID userId = (UUID) kh.getKeyList().get(0).get("id");
            user.setId(userId);
            authJdbcTemplate.batchUpdate("INSERT INTO authorities (user_id, authority) VALUES (?, ?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setObject(1, userId);
                    ps.setObject(2, Authority.values()[i].name());
                }

                @Override
                public int getBatchSize() {
                    return Authority.values().length;
                }
            });
            return 1;
        });
    }

    @Override
    public UserEntity updateUser(UserEntity user) {
        authJdbcTemplate.update("UPDATE users SET password = ?, enabled = ?, account_non_expired = ?," +
                        "account_non_locked = ? WHERE id = ?",
                pe.encode(user.getPassword()),
                user.getEnabled(),
                user.getAccountNonExpired(),
                user.getAccountNonLocked(),
                user.getId()
        );
        return getUserById(user.getId());
    }

    @Override
    public void deleteUserById(UUID userId) {
        authTtpl.execute(status -> {
            authJdbcTemplate.update(con -> {
                PreparedStatement authorityPs = con.prepareStatement("DELETE from authorities WHERE user_id = ?");
                authorityPs.setObject(1, userId);
                return authorityPs;
            });
            authJdbcTemplate.update(con -> {
                PreparedStatement usersPs = con.prepareStatement("DELETE from users WHERE id = ?");
                usersPs.setObject(1, userId);
                return usersPs;
            });
            return 0;
        });
    }

    @Override
    public UserEntity getUserById(UUID userId) {
        UserEntity user = authJdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE id = ?",
                UserEntityRowMapper.instance,
                userId
        );

        List<AuthorityEntity> authorities = authJdbcTemplate.query(
                "SELECT * FROM authorities WHERE user_id = ?",
                AuthorityEntityRowMapper.instance,
                userId
        );

        user.setAuthorities(authorities);
        return user;
    }

    @Override
    public int createUserInUserData(UserEntity user) {
        return userdataJdbcTemplate.update(
                "INSERT INTO users (id, username, currency) VALUES (?, ?, ?)",
                user.getId(),
                user.getUsername(),
                CurrencyValues.RUB.name()
        );
    }

    @Override
    public void deleteUserByIdInUserData(UUID userId) {
        userdataJdbcTemplate.update("DELETE FROM users WHERE id = ?", userId);
    }

    @Override
    public void deleteUserByUsernameInUserData(String username) {
        userdataJdbcTemplate.update("DELETE FROM users WHERE username = ?", username);
    }
}
