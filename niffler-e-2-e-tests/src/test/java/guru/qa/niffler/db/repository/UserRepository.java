package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.userdata.UserDataUserEntity;

public interface UserRepository {
    void createUserForTest(AuthUserEntity user);

    void removeAfterTest(AuthUserEntity user);

    UserDataUserEntity getUserData(AuthUserEntity user);

    void addFriendForUser(UserDataUserEntity user, UserDataUserEntity friend, boolean pending);
}
