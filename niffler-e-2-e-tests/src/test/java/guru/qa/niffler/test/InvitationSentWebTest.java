package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static guru.qa.niffler.jupiter.annotation.User.UserType.INVITATION_SENT;

public class InvitationSentWebTest extends BaseWebTest {

    @BeforeEach
    void doLogin(@User(userType = INVITATION_SENT) UserJson userForTest) {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(userForTest.getUsername());
        $("input[name='password']").setValue(userForTest.getPassword());
        $("button[type='submit']").click();
    }

    @Test
    @AllureId("104")
    void invitationSentShouldBeDisplayedInTable0() throws InterruptedException {
        $x("//a[@href='/people']").shouldBe(enabled).click();

        Allure.step(
                "Check pending invitation", () ->
                        $(byText("Pending invitation")).shouldBe(visible));
    }

    @Test
    @AllureId("105")
    void invitationSentShouldBeDisplayedInTable1(@User(userType = INVITATION_SENT) UserJson userForTest) throws InterruptedException {
        $x("//a[@href='/people']").shouldBe(enabled).click();

        Allure.step(
                "Check pending invitation", () ->
                        $(byText("Pending invitation")).shouldBe(visible));
    }

    @Test
    @AllureId("106")
    void invitationSentShouldBeDisplayedInTable2(@User(userType = INVITATION_SENT) UserJson userForTest) throws InterruptedException {
        $x("//a[@href='/people']").shouldBe(enabled).click();

        Allure.step(
                "Check pending invitation", () ->
                        $(byText("Pending invitation")).shouldBe(visible));
    }
}
