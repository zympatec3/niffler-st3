package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.GenerateUser;
import guru.qa.niffler.jupiter.annotation.GeneratedUser;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CreateUserExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace
            NESTED = ExtensionContext.Namespace.create(GeneratedUser.Selector.NESTED),
            OUTER = ExtensionContext.Namespace.create(GeneratedUser.Selector.OUTER);

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Map<GeneratedUser.Selector, UserJson> usersMap = new HashMap<>();

        Map<GeneratedUser.Selector, GenerateUser> usersForTest = usersForTest(extensionContext);
        for (Map.Entry<GeneratedUser.Selector, GenerateUser> entry : usersForTest.entrySet()) {


//
//            extensionContext.getStore(ExtensionContext.Namespace.create(entry.getKey()))
//                    .put(entry.getKey(), user);

            UserJson user = createUserForTest(entry.getValue());
            user.setFriends(createFriendsIfPresent(entry.getValue(), user));
            user.setIncomeInvitations(createIncomeInvitationsIfPresent(entry.getValue(), user));
            user.setOutcomeInvitations(createOutcomeInvitationsIfPresent(entry.getValue(), user));

            usersMap.put(entry.getKey(), user);
        }

        extensionContext.getStore(ExtensionContext.Namespace.GLOBAL).put(extensionContext.getUniqueId(), usersMap);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().isAnnotationPresent(GeneratedUser.class) &&
                parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        GeneratedUser generatedUser = parameterContext.getParameter().getAnnotation(GeneratedUser.class);

        Map<GeneratedUser.Selector, UserJson> usersMap = extensionContext.getStore(ExtensionContext.Namespace.GLOBAL)
                .get(extensionContext.getUniqueId(), Map.class);

        return usersMap.get(generatedUser.selector());
    }

    protected abstract UserJson createUserForTest(GenerateUser annotation);

    protected abstract List<UserJson> createFriendsIfPresent(GenerateUser annotation, UserJson currentUser);

    protected abstract List<UserJson> createIncomeInvitationsIfPresent(GenerateUser annotation, UserJson currentUser);

    protected abstract List<UserJson> createOutcomeInvitationsIfPresent(GenerateUser annotation, UserJson currentUser);


    private Map<GeneratedUser.Selector, GenerateUser> usersForTest(ExtensionContext extensionContext) {
        Map<GeneratedUser.Selector, GenerateUser> result = new HashMap<>();
        ApiLogin apiLogin = extensionContext.getRequiredTestMethod().getAnnotation(ApiLogin.class);
        if (apiLogin != null && apiLogin.user().handleAnnotation()) {
            result.put(GeneratedUser.Selector.NESTED, apiLogin.user());
        }
        GenerateUser outerUser = extensionContext.getRequiredTestMethod().getAnnotation(GenerateUser.class);
        if (outerUser != null && outerUser.handleAnnotation()) {
            result.put(GeneratedUser.Selector.OUTER, outerUser);
        }
        return result;
    }
}
