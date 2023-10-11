package guru.qa.niffler.test;


import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import static com.codeborne.selenide.Selenide.open;
import static guru.qa.niffler.jupiter.annotation.GeneratedUser.Selector.NESTED;
import static guru.qa.niffler.jupiter.annotation.GeneratedUser.Selector.OUTER;
import static guru.qa.niffler.jupiter.annotation.User.UserType.WITH_FRIENDS;

//@Execution(ExecutionMode.SAME_THREAD)
public class FriendsWebTest extends BaseWebTest {

//    @BeforeEach
//    void doLogin(@User(userType = WITH_FRIENDS) UserJson userForTest) {
//        Selenide.open("http://127.0.0.1:3000/main");
//        $("a[href*='redirect']").click();
//        $("input[name='username']").setValue(userForTest.getUsername());
//        $("input[name='password']").setValue(userForTest.getPassword());
//        $("button[type='submit']").click();
//    }

    @Test
    @AllureId("101")
    @ResourceLock("lock")
    void friendShouldBeDisplayedInTable0(@User(userType = WITH_FRIENDS) UserJson userForTest) throws InterruptedException {
        Thread.sleep(3000);
    }

    @Test
    @AllureId("102")
    void friendShouldBeDisplayedInTable1(@User(userType = WITH_FRIENDS) UserJson userForTest) throws InterruptedException {
        Thread.sleep(3000);
    }

    @Test
    @AllureId("103")
    void friendShouldBeDisplayedInTable2(@User(userType = WITH_FRIENDS) UserJson userForTest) throws InterruptedException {
        Thread.sleep(3000);
    }


    @ApiLogin(
            user = @GenerateUser(
                    incomeInvitations = @IncomeInvitation(
                            count = 3
                    )
            )
    )
    @GenerateUser
    @Test
    @AllureId("21324")
    void incomeInvitationShouldBePresentInTable(@GeneratedUser(selector = NESTED) UserJson userForTest,
                                                @GeneratedUser(selector = OUTER) UserJson another) {
        open(CFG.nifflerFrontUrl() + "/main");
        System.out.println();
    }

}
