package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotation.DBUser;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class CreateUserTest extends BaseWebTest {

    private final static String user = "zympawkek102";
    private final static String password = "12345";

    @DBUser(username = user,
            password = password)
    @Test
    void mainPageShouldBeVisibleAfterLogin(AuthUserEntity currentUser) {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(currentUser.getUsername());
        $("input[name='password']").setValue(currentUser.getPassword());
        $("button[type='submit']").click();
        $(".main-content__section-stats").shouldBe(visible);
    }
}
