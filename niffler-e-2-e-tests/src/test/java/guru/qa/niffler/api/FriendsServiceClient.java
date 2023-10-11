package guru.qa.niffler.api;

import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Step;

import java.io.IOException;

public class FriendsServiceClient extends RestService {

    public FriendsServiceClient() {
        super(CFG.nifflerUserDataUrl());
    }

    private final FriendsService friendsService = retrofit.create(FriendsService.class);

    @Step("Add friend")
    public void addFriend(String token, UserJson userJson) {
        try {
            friendsService.addFriend("Bearer " + token, userJson).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Step("Accept invitation")
    public void acceptInvitation(String token, UserJson userJson) {
        try {
            friendsService.acceptInvitation("Bearer " + token, userJson).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
