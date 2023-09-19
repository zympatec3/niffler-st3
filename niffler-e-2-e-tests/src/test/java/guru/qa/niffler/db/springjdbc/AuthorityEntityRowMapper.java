package guru.qa.niffler.db.springjdbc;

import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthorityEntityRowMapper implements RowMapper<AuthorityEntity> {

    public static final AuthorityEntityRowMapper instance = new AuthorityEntityRowMapper();

    @Override
    public AuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthorityEntity user = new AuthorityEntity();
        user.setId(rs.getObject("id", UUID.class));
        user.setAuthority(Authority.valueOf(rs.getString("authority")));
        return user;
    }
}
