package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static guru.qa.niffler.jupiter.User.UserType.INVITATION_RECEIVED;

public class InvitationReceivedWebTest extends BaseWebTest {

    @BeforeEach
    void doLogin(@User(userType = INVITATION_RECEIVED) UserJson userForTest) {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(userForTest.getUsername());
        $("input[name='password']").setValue(userForTest.getPassword());
        $("button[type='submit']").click();
    }

    @Test
    @AllureId("107")
    void invitationReceivedShouldBeDisplayedInTable0(@User(userType = INVITATION_RECEIVED) UserJson userForTest) throws InterruptedException {
        $x("//a[@href='/friends']").shouldBe(enabled).click();

        Allure.step(
                "Check received invitation", () ->
                        $x("//div[@data-tooltip-id='submit-invitation']").shouldBe(visible));
    }

    @Test
    @AllureId("108")
    void invitationReceivedShouldBeDisplayedInTable1(@User(userType = INVITATION_RECEIVED) UserJson userForTest) throws InterruptedException {
        $x("//a[@href='/friends']").shouldBe(enabled).click();

        Allure.step(
                "Check received invitation", () ->
                        $x("//div[@data-tooltip-id='submit-invitation']").shouldBe(visible));
    }

    @Test
    @AllureId("109")
    void invitationReceivedShouldBeDisplayedInTable2(@User(userType = INVITATION_RECEIVED) UserJson userForTest) throws InterruptedException {
        $x("//a[@href='/friends']").shouldBe(enabled).click();

        Allure.step(
                "Check received invitation", () ->
                        $x("//div[@data-tooltip-id='submit-invitation']").shouldBe(visible));
    }
}
