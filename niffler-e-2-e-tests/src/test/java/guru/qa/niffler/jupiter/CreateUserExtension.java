package guru.qa.niffler.jupiter;

import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.AuthUserDAOJdbc;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.utils.RandomUtils;
import org.junit.jupiter.api.extension.*;

import java.util.Arrays;

public class CreateUserExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {

    private static final ExtensionContext.Namespace USER_NAMESPACE = ExtensionContext.Namespace.create(CreateUserExtension.class);

    private static final AuthUserDAO authUserDAO = new AuthUserDAOJdbc();
    private static final UserDataUserDAO userDataUserDAO = new AuthUserDAOJdbc();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        DBUser dbUserAnnotation = context.getRequiredTestMethod().getAnnotation(DBUser.class);
        if (dbUserAnnotation != null) {
            UserEntity user = createUserEntityFromAnnotation(dbUserAnnotation);
            authUserDAO.createUser(user);
            userDataUserDAO.createUserInUserData(user);

            context.getStore(USER_NAMESPACE).put(context.getUniqueId(), user);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        var user = context.getStore(USER_NAMESPACE).get(context.getUniqueId(), UserEntity.class);
        userDataUserDAO.deleteUserByIdInUserData(user.getId());
        authUserDAO.deleteUserById(user.getId());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().isAssignableFrom(UserEntity.class) &&
                extensionContext.getTestMethod().isPresent() &&
                extensionContext.getTestMethod().get().isAnnotationPresent(DBUser.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return extensionContext.getStore(USER_NAMESPACE).get(extensionContext.getUniqueId());
    }

    private UserEntity createUserEntityFromAnnotation(DBUser annotation) {
        UserEntity user = new UserEntity();
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
                    return ae;
                }).toList());
        return user;
    }
}



