package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.AuthServiceClient;
import guru.qa.niffler.api.FriendsServiceClient;
import guru.qa.niffler.api.RegisterServiceClient;
import guru.qa.niffler.api.context.SessionStorageContext;
import guru.qa.niffler.jupiter.annotation.Friend;
import guru.qa.niffler.jupiter.annotation.GenerateUser;
import guru.qa.niffler.jupiter.annotation.IncomeInvitation;
import guru.qa.niffler.jupiter.annotation.OutcomeInvitation;
import guru.qa.niffler.model.UserJson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static guru.qa.niffler.util.FakerUtils.generateRandomUsername;

public class RestCreateUserExtension extends CreateUserExtension {

    private final RegisterServiceClient registerService = new RegisterServiceClient();

    private final AuthServiceClient authServiceClient = new AuthServiceClient();

    private final FriendsServiceClient friendsServiceClient = new FriendsServiceClient();
    private static final String DEFAULT_PASSWORD = "12345";

    @Override
    protected UserJson createUserForTest(GenerateUser annotation) {
        UserJson userJson = new UserJson();
        String username = generateRandomUsername();
        registerService.register(username, DEFAULT_PASSWORD);
        userJson.setUsername(username);
        userJson.setPassword(DEFAULT_PASSWORD);
        return userJson;
    }

    @Override
    protected List<UserJson> createFriendsIfPresent(GenerateUser annotation, UserJson currentUser) {
        Friend friends = annotation.friends();
        List<UserJson> friendList = new ArrayList<>();
        if (friends.handleAnnotation()) {
            for (int i = 0; i < friends.count(); i++) {
                UserJson friend = createUserForTest(annotation);
                addFriend(currentUser, friend);
                acceptInvitation(currentUser, friend);
                friendList.add(friend);
            }
        }
        return friendList;
    }

    @Override
    protected List<UserJson> createIncomeInvitationsIfPresent(GenerateUser annotation, UserJson currentUser) {
        IncomeInvitation incomeInvitation = annotation.incomeInvitations();
        List<UserJson> incomeInvitationList = new ArrayList<>();
        if (incomeInvitation.handleAnnotation()) {
            for (int i = 0; i < incomeInvitation.count(); i++) {
                UserJson friend = createUserForTest(annotation);
                addFriend(currentUser, friend);
                incomeInvitationList.add(friend);
            }
        }
        return incomeInvitationList;
    }

    @Override
    protected List<UserJson> createOutcomeInvitationsIfPresent(GenerateUser annotation, UserJson currentUser) {
        OutcomeInvitation outcomeInvitation = annotation.outcomeInvitations();
        List<UserJson> outcomeInvitationList = new ArrayList<>();
        if (outcomeInvitation.handleAnnotation()) {
            for (int i = 0; i < outcomeInvitation.count(); i++) {
                UserJson friend = createUserForTest(annotation);
                addFriend(friend, currentUser);
                outcomeInvitationList.add(friend);
            }
        }
        return outcomeInvitationList;
    }

    private void acceptInvitation(UserJson currentUser, UserJson friend) {
        try {
            authServiceClient.doLogin(friend.getUsername(), DEFAULT_PASSWORD);
            String tokenFriendUser = SessionStorageContext.getInstance().getToken();
            friendsServiceClient.acceptInvitation(tokenFriendUser, currentUser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addFriend(UserJson currentUser, UserJson friend) {
        try {
            authServiceClient.doLogin(currentUser.getUsername(), DEFAULT_PASSWORD);
            String tokenCurrentUser = SessionStorageContext.getInstance().getToken();
            friendsServiceClient.addFriend(tokenCurrentUser, friend);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
