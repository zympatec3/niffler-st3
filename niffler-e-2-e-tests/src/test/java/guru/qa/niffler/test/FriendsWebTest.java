package guru.qa.niffler.test;


import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static guru.qa.niffler.jupiter.annotation.User.UserType.WITH_FRIENDS;

//@Execution(ExecutionMode.SAME_THREAD)
public class FriendsWebTest extends BaseWebTest {

    @BeforeEach
    void doLogin(@User(userType = WITH_FRIENDS) UserJson userForTest) {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(userForTest.getUsername());
        $("input[name='password']").setValue(userForTest.getPassword());
        $("button[type='submit']").click();
    }

    @Test
    @AllureId("101")
    @ResourceLock("lock")
    void friendShouldBeDisplayedInTable0(@User(userType = WITH_FRIENDS) UserJson userForTest) throws InterruptedException {
        Thread.sleep(3000);
        $x("//a[@href='/friends']").shouldBe(enabled).click();

        Allure.step(
                "Check you are friends in table", () ->
                        $(byText("You are friends")).shouldBe(visible));
    }

    @Test
    @AllureId("102")
    void friendShouldBeDisplayedInTable1(@User(userType = WITH_FRIENDS) UserJson userForTest) throws InterruptedException {
        $x("//a[@href='/friends']").shouldBe(enabled).click();

        Allure.step(
                "Check you are friends in table", () ->
                        $(byText("You are friends")).shouldBe(visible));
    }

    @Test
    @AllureId("103")
    void friendShouldBeDisplayedInTable2(@User(userType = WITH_FRIENDS) UserJson userForTest) throws InterruptedException {
        $x("//a[@href='/friends']").shouldBe(enabled).click();

        Allure.step(
                "Check you are friends in table", () ->
                        $(byText("You are friends")).shouldBe(visible));
    }
}
