package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.DBUser;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class CreateUserTest extends BaseWebTest {

    @DBUser()
    @ApiLogin()
    @Test
    void mainPageShouldBeVisibleAfterLogin() {
        open(CFG.nifflerFrontUrl() + "/main");

        $(".main-content__section-stats").shouldBe(visible);
    }
}
