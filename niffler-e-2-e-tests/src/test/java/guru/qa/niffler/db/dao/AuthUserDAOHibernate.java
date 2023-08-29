package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.auth.AuthUserEntity;

import java.util.UUID;

public class AuthUserDAOHibernate implements AuthUserDAO {

    @Override
    public int createUser(AuthUserEntity user) {
        return 0;
    }

    @Override
    public AuthUserEntity updateUser(AuthUserEntity user) {
        return null;
    }

    @Override
    public void deleteUser(AuthUserEntity userId) {

    }

    @Override
    public AuthUserEntity getUserById(UUID userId) {
        return null;
    }
}
