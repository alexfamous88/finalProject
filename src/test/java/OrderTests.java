import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class OrderTests {

    WebDriver driver;
    WebDriverWait wait;

    // Для авторизации
    private static final String LOGIN = "kedr";
    private static final String PASSWORD = "12345";
    private static final String MAIL_PREFIX = "@mail.ru";
    private static final By inputLoginLocator = By.xpath("//*[@id='username']");
    private static final By inputPasswordLocator = By.xpath("//*[@id='password']");
    private static final By buttonSubmitLocator = By.xpath("//*[contains(@class, 'woocommerce-form-login__submit')]");

    // Для добавления в корзину
    private static final By buttonInCartLocator = (By.xpath("//*[@class='added_to_cart wc-forward']"));
    private static final By orderingButtonInCartLocator = By.xpath("//*[@class='checkout-button button alt wc-forward']");

    // Для удаления тестируемого товара из корзины
    private static final By removeInCartButtonLocator = By.xpath("//td[@class='product-remove']//a");

    // Для заполнения формы оформления заказа
    private static final By firstNameLocator = By.xpath("//*[@id='billing_first_name']");
    private static final By lastNameLocator = By.xpath("//*[@id='billing_last_name']");
    private static final By addressLocator = By.xpath("//*[@id='billing_address_1']");
    private static final By cityLocator = By.xpath("//*[@id='billing_city']");
    private static final By stateLocator = By.xpath("//*[@id='billing_state']");
    private static final By postcodeLocator = By.xpath("//*[@id='billing_postcode']");
    private static final By phoneLocator = By.xpath("//*[@id='billing_phone']");
    private static final By emailLocator = By.xpath("//*[@id='billing_email']");
    private static final By orderingButtonInOrderingLocator = By.xpath("//*[@id='place_order']");
    private static final String expectedTitle = "Заказ получен";
    private static final By titleLocator = By.xpath("//h2[@class='post-title']");
    private static final By loaderLocator = By.xpath("//*[@class='blockUI blockOverlay']");
    private static final By bankTransferLocator = By.xpath("//*[@id='payment_method_bacs']");
    private static final By paymentOnDeliveryLocator = By.xpath("//*[@id='payment_method_cod']");

    // Ошибки незаполненных полей
    private static final By firstNameErrorLocator = By.xpath("//*[@role='alert']/*[@data-id='billing_first_name']");
    private static final By lastNameErrorLocator = By.xpath("//*[@role='alert']/*[@data-id='billing_last_name']");
    private static final By addressErrorLocator = By.xpath("//*[@role='alert']/*[@data-id='billing_address_1']");
    private static final By cityErrorLocator = By.xpath("//*[@role='alert']/*[@data-id='billing_city']");
    private static final By stateErrorLocator = By.xpath("//*[@role='alert']/*[@data-id='billing_state']");
    private static final By postcodeErrorLocator = By.xpath("//*[@role='alert']/*[@data-id='billing_postcode']");
    private static final By phoneErrorLocator = By.xpath("//*[@role='alert']/*[@data-id='billing_phone']");
    private static final By emailErrorLocator = By.xpath("//*[@role='alert']/*[@data-id='billing_email']");
    private static final String expectedFirstNameError = "Имя для выставления счета обязательное поле.";
    private static final String expectedLastNameError = "Фамилия для выставления счета обязательное поле.";
    private static final String expectedAddressError = "Адрес для выставления счета обязательное поле.";
    private static final String expectedCityError = "Город / Населенный пункт для выставления счета обязательное поле.";
    private static final String expectedStateError = "Область для выставления счета обязательное поле.";
    private static final String expectedPostcodeError = "Почтовый индекс для выставления счета обязательное поле.";
    private static final String expectedPhoneError = "Телефон для выставления счета обязательное поле.";
    private static final String expectedEmailError = "Адрес почты для выставления счета обязательное поле.";

    // Для применения купона
    private static final By couponLinkLocator = By.xpath("//*[@class='showcoupon']");
    private static final By couponInputLocator = By.xpath("//*[@name='coupon_code']");
    private static final String expectedMessage = "Купон успешно добавлен.";
    private static final String expectedMessageRemove = "Купон удален.";
    private static final String coupon = "sert500";
    private static final By couponSubmitLocator = By.xpath("//*[@value='Применить купон']");
    private static final By couponSuccessMessageLocator = By.xpath("//*[@role='alert']");
    private static final By removeCouponLocator = By.xpath("//*[@class='woocommerce-remove-coupon']");

    // Для проверки в листе заказа
    private static final By totalPriceInOrderList = By.xpath("//*[@class='woocommerce-Price-amount amount']");
    private static final By numberInOrderList = By.xpath("//*[@class='woocommerce-order-overview__order order']//strong");
    private static final By dateInOrderList = By.xpath("//*[@class='woocommerce-order-overview__date date']//strong");

    // Для проверки в Мой аккаунт
    private static final By totalPriceInAccount = By.xpath("(//*[@class='woocommerce-Price-amount amount'])[1]");
    private static final By numberInAccount = By.xpath("(//*[@data-title='Заказ']//*)[1]");
    private static final By dateInAccount = By.xpath("(//*[@data-title='Дата']//*)[1]");

    @Before
    public void setUp() {
        driver = new ChromeDriver();
        Dimension size = new Dimension(1920, 1080);
        driver.manage().window().setSize(size);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(5));
    }

    @After
    public void tearDown() throws IOException {
        // Для снятия скриншота
        var sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(sourceFile, new File(
                "./screenshots/screenshot.png"));
        driver.quit();
    }


    // Оформление заказа и оплата банковским переводом
    @Test
    public void ordering_PositiveTest_BankTransfer() {

        // Авторизация пользователя
        driver.navigate().to("http://intershop5.skillbox.ru/my-account/");
        driver.findElement(inputLoginLocator).sendKeys(LOGIN);
        driver.findElement(inputPasswordLocator).sendKeys(PASSWORD);
        driver.findElement(buttonSubmitLocator).click();

        // Добавление товара в корзину
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ")) {
                driver.findElement(buttonLocator).click();
                driver.findElement(buttonInCartLocator).click();
                i = -1;
            }
        }
        driver.findElement(orderingButtonInCartLocator).click(); // клик по кнопке оформления заказа в корзине

        // Заполнение данных формы
        driver.findElement(firstNameLocator).clear();
        driver.findElement(firstNameLocator).sendKeys("Тестер");
        driver.findElement(lastNameLocator).clear();
        driver.findElement(lastNameLocator).sendKeys("Тестеров");
        driver.findElement(addressLocator).clear();
        driver.findElement(addressLocator).sendKeys("Пушкина 88");
        driver.findElement(cityLocator).clear();
        driver.findElement(cityLocator).sendKeys("Львов");
        driver.findElement(stateLocator).clear();
        driver.findElement(stateLocator).sendKeys("Львовская");
        driver.findElement(postcodeLocator).clear();
        driver.findElement(postcodeLocator).sendKeys("79007");
        driver.findElement(phoneLocator).clear();
        driver.findElement(phoneLocator).sendKeys("88005553535");
        driver.findElement(emailLocator).clear();
        driver.findElement(emailLocator).sendKeys(LOGIN + MAIL_PREFIX);
        driver.findElement(bankTransferLocator).click(); // выбор оплаты банковским переводом
        driver.findElement(orderingButtonInOrderingLocator).click(); // клик по кнопке оформления заказа в оформлении заказа

        wait.until(ExpectedConditions.invisibilityOfElementLocated(loaderLocator));
        var actualTitle = driver.findElement(titleLocator).getText();
        Assert.assertEquals("Заказ не был оформлен", expectedTitle, actualTitle);
    }


    // Оформление заказа и оплата при получении
    @Test
    public void ordering_PositiveTest_Payment_on_Delivery() {

        // Авторизация пользователя
        driver.navigate().to("http://intershop5.skillbox.ru/my-account/");
        driver.findElement(inputLoginLocator).sendKeys(LOGIN);
        driver.findElement(inputPasswordLocator).sendKeys(PASSWORD);
        driver.findElement(buttonSubmitLocator).click();

        // Добавление товара в корзину
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ")) {
                driver.findElement(buttonLocator).click();
                driver.findElement(buttonInCartLocator).click();
                i = -1;
            }
        }
        driver.findElement(orderingButtonInCartLocator).click(); // клик по кнопке оформления заказа в корзине

        // Заполнение данных формы
        driver.findElement(firstNameLocator).clear();
        driver.findElement(firstNameLocator).sendKeys("Тестер");
        driver.findElement(lastNameLocator).clear();
        driver.findElement(lastNameLocator).sendKeys("Тестеров");
        driver.findElement(addressLocator).clear();
        driver.findElement(addressLocator).sendKeys("Пушкина 88");
        driver.findElement(cityLocator).clear();
        driver.findElement(cityLocator).sendKeys("Львов");
        driver.findElement(stateLocator).clear();
        driver.findElement(stateLocator).sendKeys("Львовская");
        driver.findElement(postcodeLocator).clear();
        driver.findElement(postcodeLocator).sendKeys("79007");
        driver.findElement(phoneLocator).clear();
        driver.findElement(phoneLocator).sendKeys("88005553535");
        driver.findElement(emailLocator).clear();
        driver.findElement(emailLocator).sendKeys(LOGIN + MAIL_PREFIX);
        driver.findElement(paymentOnDeliveryLocator).click(); // выбор оплаты при доставке
        driver.findElement(orderingButtonInOrderingLocator).click(); // клик по кнопке оформления заказа в оформлении заказа

        wait.until(ExpectedConditions.invisibilityOfElementLocated(loaderLocator));
        var actualTitle = driver.findElement(titleLocator).getText();
        Assert.assertEquals("Заказ не был оформлен", expectedTitle, actualTitle);
    }


    // Оформление заказа без заполнения полей формы
    @Test
    public void ordering_NegativeTest_EmptySpaces() {

        // Авторизация пользователя
        driver.navigate().to("http://intershop5.skillbox.ru/my-account/");
        driver.findElement(inputLoginLocator).sendKeys(LOGIN);
        driver.findElement(inputPasswordLocator).sendKeys(PASSWORD);
        driver.findElement(buttonSubmitLocator).click();

        // Добавление товара в корзину
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ")) {
                driver.findElement(buttonLocator).click();
                driver.findElement(buttonInCartLocator).click();
                i = -1;
            }
        }
        driver.findElement(orderingButtonInCartLocator).click(); // клик по кнопке оформления заказа в корзине

        // Заполнение данных формы
        driver.findElement(firstNameLocator).clear();
        driver.findElement(lastNameLocator).clear();
        driver.findElement(addressLocator).clear();
        driver.findElement(cityLocator).clear();
        driver.findElement(stateLocator).clear();
        driver.findElement(postcodeLocator).clear();
        driver.findElement(phoneLocator).clear();
        driver.findElement(emailLocator).clear();
        driver.findElement(orderingButtonInOrderingLocator).click(); // клик по кнопке оформления заказа в оформлении заказа

        wait.until(ExpectedConditions.invisibilityOfElementLocated(loaderLocator));

        var actualFirstNameError = driver.findElement(firstNameErrorLocator).getText();
        Assert.assertEquals("Некорректная ошибка имени", expectedFirstNameError, actualFirstNameError);
        var actualLastNameError = driver.findElement(lastNameErrorLocator).getText();
        Assert.assertEquals("Некорректная ошибка фамилии", expectedLastNameError, actualLastNameError);
        var actualAddressError = driver.findElement(addressErrorLocator).getText();
        Assert.assertEquals("Некорректная ошибка адреса", expectedAddressError, actualAddressError);
        var actualCityError = driver.findElement(cityErrorLocator).getText();
        Assert.assertEquals("Некорректная ошибка города", expectedCityError, actualCityError);
        var actualStateError = driver.findElement(stateErrorLocator).getText();
        Assert.assertEquals("Некорректная ошибка области", expectedStateError, actualStateError);
        var actualPostcodeError = driver.findElement(postcodeErrorLocator).getText();
        Assert.assertEquals("Некорректная ошибка почтового индекса", expectedPostcodeError, actualPostcodeError);
        var actualPhoneError = driver.findElement(phoneErrorLocator).getText();
        Assert.assertEquals("Некорректная ошибка телефона", expectedPhoneError, actualPhoneError);
        var actualEmailError = driver.findElement(emailErrorLocator).getText();
        Assert.assertEquals("Некорректная ошибка телефона", expectedEmailError, actualEmailError);
    }


    // Применение купона
    @Test
    public void ordering_PositiveTest_ApplyCoupon() {

        // Авторизация пользователя
        driver.navigate().to("http://intershop5.skillbox.ru/my-account/");
        driver.findElement(inputLoginLocator).sendKeys(LOGIN);
        driver.findElement(inputPasswordLocator).sendKeys(PASSWORD);
        driver.findElement(buttonSubmitLocator).click();

        // Добавление товара в корзину
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ")) {
                driver.findElement(buttonLocator).click();
                driver.findElement(buttonInCartLocator).click();
                i = -1;
            }
        }
        driver.findElement(orderingButtonInCartLocator).click(); // клик по кнопке оформления заказа в корзине

        // Ввод купона
        driver.findElement(couponLinkLocator).click();
        driver.findElement(couponInputLocator).sendKeys(coupon);
        driver.findElement(couponSubmitLocator).click();

        // Проверка сообщения о применении купона
        var actualMessage = driver.findElement(couponSuccessMessageLocator).getText();
        Assert.assertEquals("Некорректное сообщение о применении купона", expectedMessage, actualMessage);

        // Удаление купона для последующих корректных проверок
        driver.findElement(removeCouponLocator).click();

        // Удаление товара из корзины для последующих корректных проверок
        driver.navigate().to("http://intershop5.skillbox.ru/cart/");
        driver.findElement(removeInCartButtonLocator).click();
    }


    // Удаление купона
    @Test
    public void ordering_PositiveTest_RemoveCoupon() {

        // Авторизация пользователя
        driver.navigate().to("http://intershop5.skillbox.ru/my-account/");
        driver.findElement(inputLoginLocator).sendKeys(LOGIN);
        driver.findElement(inputPasswordLocator).sendKeys(PASSWORD);
        driver.findElement(buttonSubmitLocator).click();

        // Добавление товара в корзину
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ")) {
                driver.findElement(buttonLocator).click();
                driver.findElement(buttonInCartLocator).click();
                i = -1;
            }
        }
        driver.findElement(orderingButtonInCartLocator).click(); // клик по кнопке оформления заказа в корзине

        // Ввод купона
        driver.findElement(couponLinkLocator).click();
        driver.findElement(couponInputLocator).sendKeys(coupon);
        driver.findElement(couponSubmitLocator).click();

        // Удаление купона
        driver.findElement(removeCouponLocator).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(removeCouponLocator));

        // Проверка сообщения об удалении купона
        var actualMessage = driver.findElement(couponSuccessMessageLocator).getText();
        Assert.assertEquals("Некорректное сообщение о применении купона", expectedMessageRemove, actualMessage);

        // Удаление товара из корзины для последующих корректных проверок
        driver.navigate().to("http://intershop5.skillbox.ru/cart/");
        driver.findElement(removeInCartButtonLocator).click();
    }


    // Проверка соответствия номера заказа в Мой аккаунт
    @Test
    public void ordering_NumberOfOrder_IdentityInMyAccount() {

        // Авторизация пользователя
        driver.navigate().to("http://intershop5.skillbox.ru/my-account/");
        driver.findElement(inputLoginLocator).sendKeys(LOGIN);
        driver.findElement(inputPasswordLocator).sendKeys(PASSWORD);
        driver.findElement(buttonSubmitLocator).click();

        // Добавление товара в корзину
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ")) {
                driver.findElement(buttonLocator).click();
                driver.findElement(buttonInCartLocator).click();
                i = -1;
            }
        }
        driver.findElement(orderingButtonInCartLocator).click(); // клик по кнопке оформления заказа в корзине

        // Заполнение данных формы
        driver.findElement(firstNameLocator).clear();
        driver.findElement(firstNameLocator).sendKeys("Тестер");
        driver.findElement(lastNameLocator).clear();
        driver.findElement(lastNameLocator).sendKeys("Тестеров");
        driver.findElement(addressLocator).clear();
        driver.findElement(addressLocator).sendKeys("Пушкина 88");
        driver.findElement(cityLocator).clear();
        driver.findElement(cityLocator).sendKeys("Львов");
        driver.findElement(stateLocator).clear();
        driver.findElement(stateLocator).sendKeys("Львовская");
        driver.findElement(postcodeLocator).clear();
        driver.findElement(postcodeLocator).sendKeys("79007");
        driver.findElement(phoneLocator).clear();
        driver.findElement(phoneLocator).sendKeys("88005553535");
        driver.findElement(emailLocator).clear();
        driver.findElement(emailLocator).sendKeys(LOGIN + MAIL_PREFIX);
        driver.findElement(bankTransferLocator).click(); // выбор оплаты банковским переводом
        driver.findElement(orderingButtonInOrderingLocator).click(); // клик по кнопке оформления заказа в оформлении заказа

        wait.until(ExpectedConditions.invisibilityOfElementLocated(loaderLocator));

        // Проверка соответствия номера заказа
        var actualNumber = driver.findElement(numberInOrderList).getText();
        driver.navigate().to("http://intershop5.skillbox.ru/my-account/orders/");
        var expectedNumber = driver.findElement(numberInAccount).getText().replace("№", "");
        Assert.assertEquals("Номер заказа в аккаунте не соответствует номеру в заказе",
                expectedNumber, actualNumber);
    }


    // Проверка соответствия даты заказа в Мой аккаунт
    @Test
    public void ordering_DateOfOrder_IdentityInMyAccount() {

        // Авторизация пользователя
        driver.navigate().to("http://intershop5.skillbox.ru/my-account/");
        driver.findElement(inputLoginLocator).sendKeys(LOGIN);
        driver.findElement(inputPasswordLocator).sendKeys(PASSWORD);
        driver.findElement(buttonSubmitLocator).click();

        // Добавление товара в корзину
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ")) {
                driver.findElement(buttonLocator).click();
                driver.findElement(buttonInCartLocator).click();
                i = -1;
            }
        }
        driver.findElement(orderingButtonInCartLocator).click(); // клик по кнопке оформления заказа в корзине

        // Заполнение данных формы
        driver.findElement(firstNameLocator).clear();
        driver.findElement(firstNameLocator).sendKeys("Тестер");
        driver.findElement(lastNameLocator).clear();
        driver.findElement(lastNameLocator).sendKeys("Тестеров");
        driver.findElement(addressLocator).clear();
        driver.findElement(addressLocator).sendKeys("Пушкина 88");
        driver.findElement(cityLocator).clear();
        driver.findElement(cityLocator).sendKeys("Львов");
        driver.findElement(stateLocator).clear();
        driver.findElement(stateLocator).sendKeys("Львовская");
        driver.findElement(postcodeLocator).clear();
        driver.findElement(postcodeLocator).sendKeys("79007");
        driver.findElement(phoneLocator).clear();
        driver.findElement(phoneLocator).sendKeys("88005553535");
        driver.findElement(emailLocator).clear();
        driver.findElement(emailLocator).sendKeys(LOGIN + MAIL_PREFIX);
        driver.findElement(bankTransferLocator).click(); // выбор оплаты банковским переводом
        driver.findElement(orderingButtonInOrderingLocator).click(); // клик по кнопке оформления заказа в оформлении заказа

        wait.until(ExpectedConditions.invisibilityOfElementLocated(loaderLocator));

        // Проверка соответствия номера заказа
        var actualDate = driver.findElement(dateInOrderList).getText();
        driver.navigate().to("http://intershop5.skillbox.ru/my-account/orders/");
        var expectedDate = driver.findElement(dateInAccount).getText();
        Assert.assertEquals("Дата заказа в аккаунте не соответствует номеру в заказе",
                expectedDate, actualDate);
    }


    // Проверка соответствия суммы заказа в Мой аккаунт
    @Test
    public void ordering_PriceOfOrder_IdentityInMyAccount() {

        // Авторизация пользователя
        driver.navigate().to("http://intershop5.skillbox.ru/my-account/");
        driver.findElement(inputLoginLocator).sendKeys(LOGIN);
        driver.findElement(inputPasswordLocator).sendKeys(PASSWORD);
        driver.findElement(buttonSubmitLocator).click();

        // Добавление товара в корзину
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ")) {
                driver.findElement(buttonLocator).click();
                driver.findElement(buttonInCartLocator).click();
                i = -1;
            }
        }
        driver.findElement(orderingButtonInCartLocator).click(); // клик по кнопке оформления заказа в корзине

        // Заполнение данных формы
        driver.findElement(firstNameLocator).clear();
        driver.findElement(firstNameLocator).sendKeys("Тестер");
        driver.findElement(lastNameLocator).clear();
        driver.findElement(lastNameLocator).sendKeys("Тестеров");
        driver.findElement(addressLocator).clear();
        driver.findElement(addressLocator).sendKeys("Пушкина 88");
        driver.findElement(cityLocator).clear();
        driver.findElement(cityLocator).sendKeys("Львов");
        driver.findElement(stateLocator).clear();
        driver.findElement(stateLocator).sendKeys("Львовская");
        driver.findElement(postcodeLocator).clear();
        driver.findElement(postcodeLocator).sendKeys("79007");
        driver.findElement(phoneLocator).clear();
        driver.findElement(phoneLocator).sendKeys("88005553535");
        driver.findElement(emailLocator).clear();
        driver.findElement(emailLocator).sendKeys(LOGIN + MAIL_PREFIX);
        driver.findElement(bankTransferLocator).click(); // выбор оплаты банковским переводом
        driver.findElement(orderingButtonInOrderingLocator).click(); // клик по кнопке оформления заказа в оформлении заказа

        wait.until(ExpectedConditions.invisibilityOfElementLocated(loaderLocator));

        // Проверка соответствия номера заказа
        var actualPrice = driver.findElement(totalPriceInOrderList).getText();
        driver.navigate().to("http://intershop5.skillbox.ru/my-account/orders/");
        var expectedPrice = driver.findElement(totalPriceInAccount).getText();
        Assert.assertEquals("Стоимость заказа в аккаунте не соответствует номеру в заказе",
                expectedPrice, actualPrice);
    }
}