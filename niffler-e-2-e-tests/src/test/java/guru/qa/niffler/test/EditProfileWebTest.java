package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.jupiter.DBUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class EditProfileWebTest extends BaseWebTest {

    private final static String user = "profile10";
    private final static String password = "12345";
    private final static String firstName = "Valentin";
    private final static String surname = "Kuznetsov";

    @BeforeEach
    void login(UserEntity currentUser) {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(currentUser.getUsername());
        $("input[name='password']").setValue(currentUser.getPassword());
        $("button[type='submit']").click();
        $(".main-content__section-stats").shouldBe(visible);
    }

    @DBUser(username = user,
            password = password)
    @Test
    void profileUpdatedMessageShouldBeVisible(UserEntity currentUser) {
        $x("//a[@href='/profile']").click();
        $x("//input[@name='firstname']").setValue(firstName);
        $x("//input[@name='surname']").setValue(surname);
        $x("//button[text()='Submit']").scrollTo().click();
        $x("//div[text()='Profile updated!']").shouldBe(visible);
    }
}
