package guru.qa.niffler.test.grpc;

import guru.qa.grpc.niffler.grpc.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class NifflerCurrencyGrpcTest extends BaseGrpcTest {

    @ParameterizedTest(name = "Запрос на все валюты возвращает валюту {0}")
    @EnumSource(value = CurrencyValues.class, names = {"RUB", "USD", "KZT", "EUR"})
    void getAllCurrenciesShouldReturnEveryCurrency(CurrencyValues currency) {
        CurrencyResponse response = currencyStub.getAllCurrencies(EMPTY);
        assertEquals(4, response.getAllCurrenciesList().size());
        assertTrue(response.getAllCurrenciesList().stream().anyMatch(c -> c.getCurrency().equals(currency)));
    }

    static Stream<Arguments> calculateRateTest() {
        return Stream.of(
                Arguments.of(CurrencyValues.USD, CurrencyValues.RUB, 10.0, 666.67),
                Arguments.of(CurrencyValues.RUB, CurrencyValues.USD, 10.0, 0.15),
                Arguments.of(CurrencyValues.KZT, CurrencyValues.USD, -10.0, -0.02),
                Arguments.of(CurrencyValues.USD, CurrencyValues.KZT, 10.0, 4761.9),
                Arguments.of(CurrencyValues.KZT, CurrencyValues.EUR, 10.0, 0.02),
                Arguments.of(CurrencyValues.EUR, CurrencyValues.USD, 10.0, 10.8),
                Arguments.of(CurrencyValues.EUR, CurrencyValues.RUB, -10.0, -720.0)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "Конвертация из {2} {0} должна вернуть {3} {1}")
    void calculateRateTest(CurrencyValues spendCurrency, CurrencyValues desiredCurrency, double spendAmount, double expectedAmount) {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(spendCurrency)
                .setDesiredCurrency(desiredCurrency)
                .setAmount(spendAmount)
                .build();

        CalculateResponse response = currencyStub.calculateRate(request);

        assertEquals(expectedAmount, response.getCalculatedAmount());
    }

    @ParameterizedTest(name = "Конвертация {0} 10.0 в ту же валюту должна вернуть 10.0")
    @EnumSource(value = CurrencyValues.class, names = {"RUB", "USD", "KZT", "EUR"})
    void convertToSameCurrencyShouldReturnSameAmount(CurrencyValues currency) {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(currency)
                .setDesiredCurrency(currency)
                .setAmount(10.0)
                .build();

        CalculateResponse response = currencyStub.calculateRate(request);
        assertEquals(10.0, response.getCalculatedAmount());
    }

    @Test
    void convertWithZeroAmountShouldReturnZero() {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.USD)
                .setDesiredCurrency(CurrencyValues.RUB)
                .setAmount(0.0)
                .build();

        CalculateResponse response = currencyStub.calculateRate(request);
        assertEquals(0, response.getCalculatedAmount());
    }

    @Test
    void convertWithMissingCurrencyShouldFail() {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.USD)
                .setDesiredCurrency(CurrencyValues.UNSPECIFIED)
                .setAmount(10.0)
                .build();

        assertThrows(Exception.class, () -> currencyStub.calculateRate(request));
    }
}
