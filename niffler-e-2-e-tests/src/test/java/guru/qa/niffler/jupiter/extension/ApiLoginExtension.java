package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.AuthServiceClient;
import guru.qa.niffler.api.context.CookieContext;
import guru.qa.niffler.api.context.SessionStorageContext;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.GenerateUser;
import guru.qa.niffler.jupiter.annotation.GeneratedUser;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Cookie;

import java.io.IOException;
import java.util.Map;

import static guru.qa.niffler.jupiter.extension.CreateUserExtension.NESTED;

public class ApiLoginExtension implements BeforeEachCallback, AfterTestExecutionCallback {

    private final AuthServiceClient authServiceClient = new AuthServiceClient();

//    @Override
//    public void beforeEach(ExtensionContext extensionContext) throws Exception {
//        ApiLogin annotation = extensionContext.getRequiredTestMethod().getAnnotation(ApiLogin.class);
//        if (annotation != null) {
//            GenerateUser user = annotation.user();
//            if (user.handleAnnotation()) {
//                UserJson createdUser = extensionContext.getStore(NESTED).get(
//                        GeneratedUser.Selector.NESTED,
//                        UserJson.class
//                );
//                doLogin(createdUser.getUsername(), createdUser.getPassword());
//            } else {
//                doLogin(annotation.username(), annotation.password());
//            }
//        }
//    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        ApiLogin annotation = extensionContext.getRequiredTestMethod().getAnnotation(ApiLogin.class);
        if (annotation != null) {
            GenerateUser user = annotation.user();
            if (user.handleAnnotation()) {
                Map<GeneratedUser.Selector, UserJson> usersMap = extensionContext.getStore(ExtensionContext.Namespace.GLOBAL)
                        .get(extensionContext.getUniqueId(), Map.class);

                if (usersMap != null && usersMap.containsKey(GeneratedUser.Selector.NESTED)) {
                    UserJson createdUser = usersMap.get(GeneratedUser.Selector.NESTED);
                    doLogin(createdUser.getUsername(), createdUser.getPassword());
                }
            } else {
                doLogin(annotation.username(), annotation.password());
            }
        }
    }

    private void doLogin(String username, String password) {
        SessionStorageContext sessionStorageContext = SessionStorageContext.getInstance();
        sessionStorageContext.init();

        try {
            authServiceClient.doLogin(username, password);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Selenide.open(Config.getInstance().nifflerFrontUrl());
        Selenide.sessionStorage().setItem("codeChallenge", sessionStorageContext.getCodeChallenge());
        Selenide.sessionStorage().setItem("id_token", sessionStorageContext.getToken());
        Selenide.sessionStorage().setItem("codeVerifier", sessionStorageContext.getCodeVerifier());
        Cookie jsessionIdCookie = new Cookie("JSESSIONID", CookieContext.getInstance().getJSessionIdCookieValue());
        WebDriverRunner.getWebDriver().manage().addCookie(jsessionIdCookie);
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        SessionStorageContext.getInstance().clearContext();
        CookieContext.getInstance().clearContext();
    }
}
