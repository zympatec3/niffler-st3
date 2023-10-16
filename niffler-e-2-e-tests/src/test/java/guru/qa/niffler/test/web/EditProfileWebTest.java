package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotation.DBUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class EditProfileWebTest extends BaseWebTest {

    private final static String firstName = "Valentin";
    private final static String surname = "Kuznetsov";

    @BeforeEach
    void login(AuthUserEntity currentUser) {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(currentUser.getUsername());
        $("input[name='password']").setValue(currentUser.getPassword());
        $("button[type='submit']").click();
        $(".main-content__section-stats").shouldBe(visible);
    }

    @DBUser()
    @Test
    void profileUpdatedMessageShouldBeVisible(AuthUserEntity currentUser) {
        $x("//a[@href='/profile']").click();
        $x("//input[@name='firstname']").setValue(firstName);
        $x("//input[@name='surname']").setValue(surname);
        $x("//button[text()='Submit']").scrollTo().click();
        $x("//div[text()='Profile updated!']").shouldBe(visible);
    }
}
