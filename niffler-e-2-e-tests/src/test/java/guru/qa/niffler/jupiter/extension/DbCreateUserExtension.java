package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.model.userdata.UserDataUserEntity;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.db.repository.UserRepositorySpringJdbc;
import guru.qa.niffler.jupiter.annotation.Friend;
import guru.qa.niffler.jupiter.annotation.GenerateUser;
import guru.qa.niffler.jupiter.annotation.IncomeInvitation;
import guru.qa.niffler.jupiter.annotation.OutcomeInvitation;
import guru.qa.niffler.model.UserJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static guru.qa.niffler.util.FakerUtils.generateRandomUsername;

public class DbCreateUserExtension extends CreateUserExtension {

    private static final String DEFAULT_PASSWORD = "12345";
    private final UserRepository userRepository = new UserRepositorySpringJdbc();

    @Override
    protected UserJson createUserForTest(GenerateUser annotation) {
        AuthUserEntity authUser = generateAuthUserEntity();
        userRepository.createUserForTest(authUser);
        UserJson result = UserJson.fromEntity(authUser);
        result.setPassword(DEFAULT_PASSWORD);
        return result;
    }

    @Override
    protected List<UserJson> createFriendsIfPresent(GenerateUser annotation, UserJson currentUser) {
        Friend friends = annotation.friends();
        List<UserJson> friendList = new ArrayList<>();
        if (friends.handleAnnotation()) {
            UserDataUserEntity currentUserDataEntity = userRepository.getUserData(AuthUserEntity.fromUserJson(currentUser));
            for (int i = 0; i < friends.count(); i++) {
                AuthUserEntity friendEntity = generateAuthUserEntity();
                userRepository.createUserForTest(friendEntity);
                UserDataUserEntity friendUserDataEntity = userRepository.getUserData(friendEntity);
                userRepository.addFriendForUser(currentUserDataEntity, friendUserDataEntity, false);
                friendList.add(UserJson.fromEntity(friendEntity));
            }
        }
        return friendList;
    }

    @Override
    protected List<UserJson> createIncomeInvitationsIfPresent(GenerateUser annotation, UserJson currentUser) {
        IncomeInvitation incomeInvitation = annotation.incomeInvitations();
        List<UserJson> incomeInvitationList = new ArrayList<>();
        if (incomeInvitation.handleAnnotation()) {
            UserDataUserEntity currentUserDataEntity = userRepository.getUserData(AuthUserEntity.fromUserJson(currentUser));
            for (int i = 0; i < incomeInvitation.count(); i++) {
                AuthUserEntity friendEntity = generateAuthUserEntity();
                userRepository.createUserForTest(friendEntity);
                UserDataUserEntity friendUserDataEntity = userRepository.getUserData(friendEntity);
                userRepository.addFriendForUser(friendUserDataEntity, currentUserDataEntity, true);
                incomeInvitationList.add(UserJson.fromEntity(friendEntity));
            }
        }
        return incomeInvitationList;
    }

    @Override
    protected List<UserJson> createOutcomeInvitationsIfPresent(GenerateUser annotation, UserJson currentUser) {
        OutcomeInvitation outcomeInvitation = annotation.outcomeInvitations();
        List<UserJson> outcomeInvitationList = new ArrayList<>();
        if (outcomeInvitation.handleAnnotation()) {
            UserDataUserEntity currentUserDataEntity = userRepository.getUserData(AuthUserEntity.fromUserJson(currentUser));
            for (int i = 0; i < outcomeInvitation.count(); i++) {
                AuthUserEntity friendEntity = generateAuthUserEntity();
                userRepository.createUserForTest(friendEntity);
                UserDataUserEntity friendUserDataEntity = userRepository.getUserData(friendEntity);
                userRepository.addFriendForUser(currentUserDataEntity, friendUserDataEntity, true);
                outcomeInvitationList.add(UserJson.fromEntity(friendEntity));
            }
        }
        return outcomeInvitationList;
    }

    private AuthUserEntity generateAuthUserEntity() {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(generateRandomUsername());
        authUser.setPassword(DEFAULT_PASSWORD);
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(new ArrayList<>(Arrays.stream(Authority.values())
                .map(a -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(a);
                    ae.setUser(authUser);
                    return ae;
                }).toList()));
        return authUser;
    }
}
