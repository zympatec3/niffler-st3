package guru.qa.niffler.db.dao.impl;

import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserdataUserDAO;
import guru.qa.niffler.db.jdbc.DataSourceProvider;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.userdata.UserDataUserEntity;
import guru.qa.niffler.db.springjdbc.AuthUserEntityRowMapper;
import guru.qa.niffler.db.springjdbc.AuthorityEntityRowMapper;
import guru.qa.niffler.db.springjdbc.UserDataUserEntityRowMapper;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class UsersDAOSpringJdbc implements AuthUserDAO, UserdataUserDAO {

    private final TransactionTemplate authTransactionTpl;
    private final TransactionTemplate userdataTransactionTpl;
    private final JdbcTemplate authJdbcTpl;
    private final JdbcTemplate userdataJdbcTpl;

    public UsersDAOSpringJdbc() {
        JdbcTransactionManager authTm = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.AUTH));
        JdbcTransactionManager userdataTm = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA));

        this.authTransactionTpl = new TransactionTemplate(authTm);
        this.userdataTransactionTpl = new TransactionTemplate(userdataTm);
        this.authJdbcTpl = new JdbcTemplate(authTm.getDataSource());
        this.userdataJdbcTpl = new JdbcTemplate(userdataTm.getDataSource());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void createUser(AuthUserEntity user) {
        authTransactionTpl.execute(status -> {
            KeyHolder kh = new GeneratedKeyHolder();

            authJdbcTpl.update(con -> {
                PreparedStatement ps = con.prepareStatement("INSERT INTO users " +
                                "(username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                                "VALUES (?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, pe.encode(user.getPassword()));
                ps.setBoolean(3, user.getEnabled());
                ps.setBoolean(4, user.getAccountNonExpired());
                ps.setBoolean(5, user.getAccountNonLocked());
                ps.setBoolean(6, user.getCredentialsNonExpired());
                return ps;
            }, kh);
            final UUID generatedUserId = (UUID) kh.getKeyList().get(0).get("id");
            authJdbcTpl.batchUpdate("INSERT INTO authorities (user_id, authority) VALUES (?, ?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setObject(1, generatedUserId);
                    ps.setString(2, user.getAuthorities().get(i).getAuthority().name());
                }

                @Override
                public int getBatchSize() {
                    return user.getAuthorities().size();
                }
            });
            user.setId(generatedUserId);
            return null;
        });
    }

    @Override
    public AuthUserEntity updateUser(AuthUserEntity user) {
        authTransactionTpl.execute(status -> {
            authJdbcTpl.update("UPDATE users SET " +
                            "password = ?, " +
                            "enabled = ?, " +
                            "account_non_expired = ?, " +
                            "account_non_locked = ?, " +
                            "credentials_non_expired = ? " +
                            "WHERE id = ? ",
                    pe.encode(user.getPassword()),
                    user.getEnabled(),
                    user.getAccountNonExpired(),
                    user.getAccountNonLocked(),
                    user.getCredentialsNonExpired(),
                    user.getId()
            );
            authJdbcTpl.update("DELETE FROM authorities WHERE user_id = ?", user.getId());
            authJdbcTpl.batchUpdate("INSERT INTO authorities (user_id, authority) VALUES (?, ?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setObject(1, user.getId());
                    ps.setString(2, user.getAuthorities().get(i).getAuthority().name());
                }

                @Override
                public int getBatchSize() {
                    return user.getAuthorities().size();
                }
            });
            return null;
        });
        return getUserById(user.getId());
    }

    @Override
    public void deleteUser(AuthUserEntity user) {
        authTransactionTpl.execute(status -> {
            authJdbcTpl.update("DELETE FROM authorities WHERE user_id = ?", user.getId());
            authJdbcTpl.update("DELETE FROM users WHERE id = ?", user.getId());
            return null;
        });
    }

    @Override
    public AuthUserEntity getUserById(UUID userId) {
        AuthUserEntity authUser = authJdbcTpl.queryForObject(
                "SELECT * FROM users WHERE id = ?",
                AuthUserEntityRowMapper.instance, userId
        );
        authUser.setAuthorities(
                authJdbcTpl.query(
                        "SELECT * FROM authorities WHERE user_id = ?",
                        AuthorityEntityRowMapper.instance, userId
                ));
        return authUser;
    }

    @Override
    public int createUserInUserData(UserDataUserEntity user) {
        return userdataJdbcTpl.update(
                "INSERT INTO users (username, currency) VALUES (?, ?)",
                user.getUsername(),
                CurrencyValues.RUB.name()
        );
    }

    @Override
    public void deleteUserInUserData(UserDataUserEntity user) {
        userdataJdbcTpl.update("DELETE FROM users WHERE id = ?", user.getId());
    }

    @Override
    public UserDataUserEntity getUserInUserDataByUsername(String username) {
        return userdataJdbcTpl.queryForObject(
                "SELECT * FROM users WHERE username = ? ",
                UserDataUserEntityRowMapper.instance,
                username
        );
    }
}
