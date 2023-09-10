package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

public class RandomUtils {

    private static final Faker faker = new Faker();

    public static String generateUsername() {
        return faker.name().username();
    }

    public static String generatePassword() {
        return faker.internet().password();
    }
}
