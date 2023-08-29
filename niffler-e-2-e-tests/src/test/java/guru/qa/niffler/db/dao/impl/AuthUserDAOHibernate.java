package guru.qa.niffler.db.dao.impl;

import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.jpa.EntityManagerFactoryProvider;
import guru.qa.niffler.db.jpa.JpaService;
import guru.qa.niffler.db.model.auth.AuthUserEntity;

import java.util.UUID;

public class AuthUserDAOHibernate extends JpaService implements AuthUserDAO {

    public AuthUserDAOHibernate() {
        super(EntityManagerFactoryProvider.INSTANCE.getDataSource(ServiceDB.AUTH).createEntityManager());
    }

    @Override
    public int createUser(AuthUserEntity user) {
        AuthUserEntity userCopy = new AuthUserEntity(user);
        userCopy.setPassword(pe.encode(user.getPassword()));

        persist(userCopy);
        user.setId(userCopy.getId());

        return 0;
    }

    @Override
    public AuthUserEntity updateUser(AuthUserEntity user) {
        return merge(user);
    }

    @Override
    public void deleteUser(AuthUserEntity user) {
        AuthUserEntity userToDelete = em.find(AuthUserEntity.class, user.getId());
        remove(userToDelete);
    }

    @Override
    public AuthUserEntity getUserById(UUID userId) {
        return em.createQuery("select u from AuthUserEntity u where u.id=:userId", AuthUserEntity.class)
                .setParameter("id", userId)
                .getSingleResult();
    }
}
