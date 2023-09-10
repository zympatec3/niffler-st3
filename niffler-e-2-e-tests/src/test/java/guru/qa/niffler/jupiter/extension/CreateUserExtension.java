package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.dao.impl.AuthUserDAOHibernate;
import guru.qa.niffler.db.dao.impl.UserdataUserDAOHibernate;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.model.userdata.UserDataUserEntity;
import guru.qa.niffler.jupiter.annotation.DBUser;
import guru.qa.niffler.utils.RandomUtils;
import org.junit.jupiter.api.extension.*;

import java.util.Arrays;

public class CreateUserExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {

    private static final ExtensionContext.Namespace USER_NAMESPACE = ExtensionContext.Namespace.create(CreateUserExtension.class);

    private static final AuthUserDAO authUserDAO = new AuthUserDAOHibernate();
    private static final UserDataUserDAO userDataUserDAO = new UserdataUserDAOHibernate();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        DBUser dbUserAnnotation = context.getRequiredTestMethod().getAnnotation(DBUser.class);
        if (dbUserAnnotation != null) {
            AuthUserEntity user = createUserEntityFromAnnotation(dbUserAnnotation);
            authUserDAO.createUser(user);

            UserDataUserEntity userData = new UserDataUserEntity();
            userData.setUsername(user.getUsername());
            userData.setCurrency(CurrencyValues.RUB);

            userDataUserDAO.createUserInUserData(userData);

            context.getStore(USER_NAMESPACE).put(getUserKey(context.getUniqueId()), user);
            context.getStore(USER_NAMESPACE).put(getUserDataKey(context.getUniqueId()), userData);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        AuthUserEntity user = context.getStore(USER_NAMESPACE).get(getUserKey(context.getUniqueId()), AuthUserEntity.class);
        UserDataUserEntity userData = context.getStore(USER_NAMESPACE).get(getUserDataKey(context.getUniqueId()), UserDataUserEntity.class);
        userDataUserDAO.deleteUserInUserData(userData);
        authUserDAO.deleteUser(user);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().isAssignableFrom(AuthUserEntity.class) &&
                extensionContext.getTestMethod().isPresent() &&
                extensionContext.getTestMethod().get().isAnnotationPresent(DBUser.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return extensionContext.getStore(USER_NAMESPACE).get(extensionContext.getUniqueId() + "user", AuthUserEntity.class);
    }

    private AuthUserEntity createUserEntityFromAnnotation(DBUser annotation) {
        AuthUserEntity user = new AuthUserEntity();
        String username = annotation.username().isEmpty() ? RandomUtils.generateUsername() : annotation.username();
        String password = annotation.password().isEmpty() ? RandomUtils.generatePassword() : annotation.password();

        user.setUsername(username);
        user.setPassword(password);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setAuthorities(Arrays.stream(Authority.values())
                .map(a -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(a);
                    ae.setUser(user);
                    return ae;
                }).toList());
        return user;
    }

    private String getUserKey(String uniqueId) {
        return uniqueId + "user";
    }

    private String getUserDataKey(String uniqueId) {
        return uniqueId + "userdata";
    }
}



