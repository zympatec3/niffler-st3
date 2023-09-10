package guru.qa.niffler.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import org.springframework.jdbc.core.RowMapper;

public class AuthorityEntityRowMapper implements RowMapper<AuthorityEntity> {

    public static final AuthorityEntityRowMapper instance = new AuthorityEntityRowMapper();

    @Override
    public AuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthorityEntity authorityEntity = new AuthorityEntity();
        authorityEntity.setAuthority(Authority.valueOf(rs.getString("authority").toLowerCase()));
        return authorityEntity;
    }
}

