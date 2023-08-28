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

public class AuthorizationTests {

    WebDriver driver;
    WebDriverWait wait;

    private static final String LOGIN = "kedr";
    private static final String PASSWORD = "12345";
    private static final String MAIL_PREFIX = "@mail.ru";
    private static final String DATA_PREFIX = String.valueOf(Math.round(Math.random() * 9999999 + 1));

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


    // Проверка ссылки для авторизации в хедере Главной
    @Test
    public void mainPage_Authorization_InHeader() {

        // Набор локаторов
        By authorizationLinkLocator = By.xpath("//*[@class='login-woocommerce']");
        By accountTitleLocator = By.xpath("//*[@id='accesspress-breadcrumb']/*[@class='current']");

        // Набор переменных с ожидаемым результатом
        var expectedTitle = "Мой Аккаунт";

        driver.navigate().to("http://intershop5.skillbox.ru/");

        driver.findElement(authorizationLinkLocator).click();
        var actualTitle = driver.findElement(accountTitleLocator).getText();

        Assert.assertEquals("Переход в Мой Аккаунт не осуществлен", expectedTitle, actualTitle);
    }


    // Переход к форме регистрации через ссылку в хедере Главной
    @Test
    public void registration_FromHeaderInMainPage() {

        var registrationExpected = "Регистрация";

        By accountHeaderLink = By.xpath("//*[@class='account']");
        By registrationButtonLocator = By.xpath("//*[@class='custom-register-button']");

        driver.navigate().to("http://intershop5.skillbox.ru/");

        // Переход в Мой аккаунт
        driver.findElement(accountHeaderLink).click();
        // Переход к форме регистрации
        driver.findElement(registrationButtonLocator).click();

        var registrationActual = driver.findElement(By.xpath("//*[@id='accesspress-breadcrumb']/span")).getText();
        Assert.assertEquals("Переход на страницу регистрации не осуществлен",
                registrationExpected.toLowerCase(), registrationActual.toLowerCase());
    }


    // Проверка перехода "Регистрация" из футера
    @Test
    public void mainPage_NavigationInFooter_Registration() {

        var registrationExpected = "Регистрация";

        By registrationLocator = By.xpath("//*[@id='pages-2']//*[contains(@class, 'page-item-141')]//a");

        driver.navigate().to("http://intershop5.skillbox.ru/");
        ((JavascriptExecutor) driver).executeScript("scroll(0,4000)");
        driver.findElement(registrationLocator).click();
        var registrationActual = driver.findElement(By.xpath("//*[@id='accesspress-breadcrumb']/span")).getText();
        Assert.assertEquals("Переход на страницу регистрации не осуществлен",
                registrationExpected.toLowerCase(), registrationActual.toLowerCase());
    }


    // Проверка авторизации из Оформления заказа с помощью логина
    @Test
    public void fromOrder_AuthorizationByLogin() {

        By buttonInCartLocator = (By.xpath("//*[@class='added_to_cart wc-forward']"));
        By authorizationLinkLocator = By.xpath("//*[@class='showlogin']");
        By inputLoginLocator = By.xpath("//*[@id='username']");
        By inputPasswordLocator = By.xpath("//*[@id='password']");
        By submitButtonLocator = By.xpath("//button[@class='woocommerce-button button woocommerce-form-login__submit']");
        By orderTitleLocator = By.xpath("//*[@class='woocommerce-billing-fields']//h3");

        var expectedTitle = "Детали заказа";

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        // Добавление товара в корзину
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ")) {
                driver.findElement(buttonLocator).click();
                wait.until(ExpectedConditions.elementToBeClickable(buttonInCartLocator));
                i = -1;
            }
        }
        // Переход в Оформление заказа
        driver.navigate().to("http://intershop5.skillbox.ru/checkout/");

        // Клик по ссылке "Авторизоваться"
        driver.findElement(authorizationLinkLocator).click();

        // Ввод логина и пароля
        driver.findElement(inputLoginLocator).sendKeys(LOGIN);
        driver.findElement(inputPasswordLocator).sendKeys(PASSWORD);

        // Подтверждение авторизации
        driver.findElement(submitButtonLocator).click();
        var actualTitle = driver.findElement(orderTitleLocator).getText();

        Assert.assertEquals("Авторизация неуспешна", expectedTitle, actualTitle);
    }


    // Проверка авторизации из Оформления заказа с помощью email
    @Test
    public void fromOrder_AuthorizationByEmail() {

        By buttonInCartLocator = (By.xpath("//*[@class='added_to_cart wc-forward']"));
        By authorizationLinkLocator = By.xpath("//*[@class='showlogin']");
        By inputLoginLocator = By.xpath("//*[@id='username']");
        By inputPasswordLocator = By.xpath("//*[@id='password']");
        By submitButtonLocator = By.xpath("//button[@class='woocommerce-button button woocommerce-form-login__submit']");
        By orderTitleLocator = By.xpath("//*[@class='woocommerce-billing-fields']//h3");

        var expectedTitle = "Детали заказа";

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        // Добавление товара в корзину
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ")) {
                driver.findElement(buttonLocator).click();
                wait.until(ExpectedConditions.elementToBeClickable(buttonInCartLocator));
                i = -1;
            }
        }
        // Переход в Оформление заказа
        driver.navigate().to("http://intershop5.skillbox.ru/checkout/");

        // Клик по ссылке "Авторизоваться"
        driver.findElement(authorizationLinkLocator).click();

        // Ввод email и пароля
        driver.findElement(inputLoginLocator).sendKeys(LOGIN + MAIL_PREFIX);
        driver.findElement(inputPasswordLocator).sendKeys(PASSWORD);

        // Подтверждение авторизации
        driver.findElement(submitButtonLocator).click();
        var actualTitle = driver.findElement(orderTitleLocator).getText();

        Assert.assertEquals("Авторизация неуспешна", expectedTitle, actualTitle);
    }


    // Регистрация пользователя с валидными значениями
    @Test
    public void registration_FromHeaderInMainPage_ValidData() {

        By accountHeaderLink = By.xpath("//*[@class='account']");
        By registrationButtonLocator = By.xpath("//*[@class='custom-register-button']");
        By loginLocator = By.xpath("//*[@id='reg_username']");
        By emailLocator = By.xpath("//*[@id='reg_email']");
        By passwordLocator = By.xpath("//*[@id='reg_password']");
        By submitRegistrationButtonLocator = By.xpath("//*[@name='register']");
        By successRegistrationLocator = By.xpath("//*[@class='content-page']");

        var expectedMessage = "Регистрация завершена";

        driver.navigate().to("http://intershop5.skillbox.ru/");

        // Переход в Мой аккаунт
        driver.findElement(accountHeaderLink).click();
        // Переход к форме регистрации
        driver.findElement(registrationButtonLocator).click();

        // Ввод валидных значений в поля формы
        driver.findElement(loginLocator).sendKeys(LOGIN + DATA_PREFIX);
        driver.findElement(emailLocator).sendKeys(LOGIN + DATA_PREFIX + MAIL_PREFIX);
        driver.findElement(passwordLocator).sendKeys(PASSWORD);
        driver.findElement(submitRegistrationButtonLocator).click();

        var actualMessage = driver.findElement(successRegistrationLocator).getText();

        Assert.assertEquals("Регистрация неуспешна", expectedMessage, actualMessage);
    }


    // Регистрация пользователя без пароля
    @Test
    public void registration_FromHeaderInMainPage_EmptyPassword() {

        By accountHeaderLink = By.xpath("//*[@class='account']");
        By registrationButtonLocator = By.xpath("//*[@class='custom-register-button']");
        By loginLocator = By.xpath("//*[@id='reg_username']");
        By emailLocator = By.xpath("//*[@id='reg_email']");
        By submitRegistrationButtonLocator = By.xpath("//*[@name='register']");
        By errorMessageLocator = By.xpath("//*[@role='alert']");

        var expectedMessage = "Error: Введите пароль для регистрации.";

        driver.navigate().to("http://intershop5.skillbox.ru/");

        // Переход в Мой аккаунт
        driver.findElement(accountHeaderLink).click();
        // Переход к форме регистрации
        driver.findElement(registrationButtonLocator).click();

        // Ввод валидных значений в поля формы без ввода пароля
        driver.findElement(loginLocator).sendKeys(LOGIN + DATA_PREFIX);
        driver.findElement(emailLocator).sendKeys(LOGIN + DATA_PREFIX + MAIL_PREFIX);
        driver.findElement(submitRegistrationButtonLocator).click();

        var actualMessage = driver.findElement(errorMessageLocator).getText();

        Assert.assertEquals("Пользователь зарегистрирован без пароля", expectedMessage, actualMessage);
    }


    // Регистрация пользователя без логина
    @Test
    public void registration_FromHeaderInMainPage_EmptyLogin() {

        By accountHeaderLink = By.xpath("//*[@class='account']");
        By registrationButtonLocator = By.xpath("//*[@class='custom-register-button']");
        By loginLocator = By.xpath("//*[@id='reg_username']");
        By emailLocator = By.xpath("//*[@id='reg_email']");
        By passwordLocator = By.xpath("//*[@id='reg_password']");
        By submitRegistrationButtonLocator = By.xpath("//*[@name='register']");
        By errorMessageLocator = By.xpath("//*[@role='alert']");

        var expectedMessage = "Error: Пожалуйста введите корректное имя пользователя.";

        driver.navigate().to("http://intershop5.skillbox.ru/");

        // Переход в Мой аккаунт
        driver.findElement(accountHeaderLink).click();
        // Переход к форме регистрации
        driver.findElement(registrationButtonLocator).click();

        // Ввод валидных значений в поля формы без логина
        driver.findElement(emailLocator).sendKeys(LOGIN + DATA_PREFIX + MAIL_PREFIX);
        driver.findElement(passwordLocator).sendKeys(PASSWORD);
        driver.findElement(submitRegistrationButtonLocator).click();

        var actualMessage = driver.findElement(errorMessageLocator).getText();

        Assert.assertEquals("Пользователь зарегистрирован без логина", expectedMessage, actualMessage);
    }


    // Регистрация пользователя без email
    @Test
    public void registration_FromHeaderInMainPage_EmptyEmail() {

        By accountHeaderLink = By.xpath("//*[@class='account']");
        By registrationButtonLocator = By.xpath("//*[@class='custom-register-button']");
        By loginLocator = By.xpath("//*[@id='reg_username']");
        By passwordLocator = By.xpath("//*[@id='reg_password']");
        By submitRegistrationButtonLocator = By.xpath("//*[@name='register']");
        By errorMessageLocator = By.xpath("//*[@role='alert']");

        var expectedMessage = "Error: Пожалуйста, введите корректный email.";

        driver.navigate().to("http://intershop5.skillbox.ru/");

        // Переход в Мой аккаунт
        driver.findElement(accountHeaderLink).click();
        // Переход к форме регистрации
        driver.findElement(registrationButtonLocator).click();

        // Ввод валидных значений в поля формы без email
        driver.findElement(loginLocator).sendKeys(LOGIN + DATA_PREFIX);
        driver.findElement(passwordLocator).sendKeys(PASSWORD);
        driver.findElement(submitRegistrationButtonLocator).click();

        var actualMessage = driver.findElement(errorMessageLocator).getText();

        Assert.assertEquals("Пользователь зарегистрирован без email", expectedMessage, actualMessage);
    }


    // Авторизация пользователя с логином
    @Test
    public void authorization_Success_ByLogin() {

        By inputLoginLocator = By.xpath("//*[@id='username']");
        By inputPasswordLocator = By.xpath("//*[@id='password']");
        By buttonSubmitLocator = By.xpath("//*[contains(@class, 'woocommerce-form-login__submit')]");
        By authorizationMessageLocator = By.xpath("(//*[@class='woocommerce-MyAccount-content']//p)[1]");

        var expectedAuthorizationMessage = "Привет " + LOGIN + " (Выйти)";

        driver.navigate().to("http://intershop5.skillbox.ru/my-account/");

        // Ввод значений логина и пароля
        driver.findElement(inputLoginLocator).sendKeys(LOGIN);
        driver.findElement(inputPasswordLocator).sendKeys(PASSWORD);
        driver.findElement(buttonSubmitLocator).click();

        var actualAuthorizationMessage = driver.findElement(authorizationMessageLocator).getText();
        Assert.assertEquals("Ошибка авторизации", expectedAuthorizationMessage, actualAuthorizationMessage);
    }


    // Авторизация пользователя с email
    @Test
    public void authorization_Success_ByPassword() {

        By inputLoginLocator = By.xpath("//*[@id='username']");
        By inputPasswordLocator = By.xpath("//*[@id='password']");
        By buttonSubmitLocator = By.xpath("//*[contains(@class, 'woocommerce-form-login__submit')]");
        By authorizationMessageLocator = By.xpath("(//*[@class='woocommerce-MyAccount-content']//p)[1]");

        var expectedAuthorizationMessage = "Привет " + LOGIN + " (Выйти)";

        driver.navigate().to("http://intershop5.skillbox.ru/my-account/");

        // Ввод значений логина и пароля
        driver.findElement(inputLoginLocator).sendKeys(LOGIN + MAIL_PREFIX);
        driver.findElement(inputPasswordLocator).sendKeys(PASSWORD);
        driver.findElement(buttonSubmitLocator).click();

        var actualAuthorizationMessage = driver.findElement(authorizationMessageLocator).getText();
        Assert.assertEquals("Ошибка авторизации", expectedAuthorizationMessage, actualAuthorizationMessage);
    }


    // Авторизация пользователя с "Запомнить меня"
    @Test
    public void authorization_WithRememberMe() {

        By inputLoginLocator = By.xpath("//*[@id='username']");
        By inputPasswordLocator = By.xpath("//*[@id='password']");
        By buttonSubmitLocator = By.xpath("//*[contains(@class, 'woocommerce-form-login__submit')]");
        By rememberMeCheckBoxLocator = By.xpath("//*[@id='rememberme']");
        By logOutLocator = By.xpath("(//*[@class='woocommerce-MyAccount-content']//a)[1]");

        var expectedLoginData = LOGIN;
        var expectedPasswordData = PASSWORD;

        driver.navigate().to("http://intershop5.skillbox.ru/my-account/");

        // Ввод значений логина и пароля
        driver.findElement(inputLoginLocator).sendKeys(LOGIN);
        driver.findElement(inputPasswordLocator).sendKeys(PASSWORD);
        driver.findElement(rememberMeCheckBoxLocator).click(); // запомнить меня
        driver.findElement(buttonSubmitLocator).click(); // войти

        driver.findElement(logOutLocator).click(); // выход из аккаунта

        // Проверка сохранения данных в форме авторизации
        var actualLoginData = driver.findElement(inputLoginLocator).getText();
        var actualPasswordData = driver.findElement(inputPasswordLocator).getText();

        Assert.assertEquals("Данные логина не сохраняются в форме", expectedLoginData, actualLoginData);
        Assert.assertEquals("Данные пароля не сохраняются в форме", expectedPasswordData, actualPasswordData);
    }


    // Авторизация пользователя без логина
    @Test
    public void authorization_WithoutLogin() {

        By inputPasswordLocator = By.xpath("//*[@id='password']");
        By buttonSubmitLocator = By.xpath("//*[contains(@class, 'woocommerce-form-login__submit')]");
        By errorMessage = By.xpath("//*[@role='alert']");

        var expectedErrorMessage = "Error: Имя пользователя обязательно.";

        driver.navigate().to("http://intershop5.skillbox.ru/my-account/");

        // Ввод значений логина и пароля
        driver.findElement(inputPasswordLocator).sendKeys(PASSWORD);
        driver.findElement(buttonSubmitLocator).click(); // войти

        var actualErrorMessage = driver.findElement(errorMessage).getText(); // получение текста сообщения об ошибке
        Assert.assertEquals("Некорректная ошибка", expectedErrorMessage, actualErrorMessage);
    }


    // Авторизация пользователя без логина
    @Test
    public void authorization_WithoutPassword() {

        By inputLoginLocator = By.xpath("//*[@id='username']");
        By buttonSubmitLocator = By.xpath("//*[contains(@class, 'woocommerce-form-login__submit')]");
        By errorMessage = By.xpath("//*[@role='alert']");

        var expectedErrorMessage = "Пароль обязателен.";

        driver.navigate().to("http://intershop5.skillbox.ru/my-account/");

        // Ввод значений логина и пароля
        driver.findElement(inputLoginLocator).sendKeys(LOGIN);
        driver.findElement(buttonSubmitLocator).click(); // войти

        var actualErrorMessage = driver.findElement(errorMessage).getText(); // получение текста сообщения об ошибке
        Assert.assertEquals("Некорректная ошибка", expectedErrorMessage, actualErrorMessage);
    }


    // Восстановление пароля существующего пользователя с помощью логина
    @Test
    public void passwordRecovery_RealUser_ByLogin() {

        By passwordRecoveryLinkLocator = By.xpath("//*[@class='woocommerce-LostPassword lost_password']//a");
        By inputLoginLocator = By.xpath("//*[@id='user_login']");
        By resetPasswordButtonLocator = By.xpath("//*[@value='Reset password']");
        By recoveryMessageLocator = By.xpath("//*[@role='alert']");

        var expectedMessage = "Password reset email has been sent.";

        driver.navigate().to("http://intershop5.skillbox.ru/my-account/");
        driver.findElement(passwordRecoveryLinkLocator).click();

        // Ввод значений логина и пароля
        driver.findElement(inputLoginLocator).sendKeys(LOGIN);
        driver.findElement(resetPasswordButtonLocator).click();

        var actualMessage = driver.findElement(recoveryMessageLocator).getText();
        Assert.assertEquals("Пароль не восстановлен", expectedMessage, actualMessage);
    }


    // Восстановление пароля существующего пользователя с помощью email
    @Test
    public void passwordRecovery_RealUser_ByEmail() {

        By passwordRecoveryLinkLocator = By.xpath("//*[@class='woocommerce-LostPassword lost_password']//a");
        By inputLoginLocator = By.xpath("//*[@id='user_login']");
        By resetPasswordButtonLocator = By.xpath("//*[@value='Reset password']");
        By recoveryMessageLocator = By.xpath("//*[@role='alert']");

        var expectedMessage = "Password reset email has been sent.";

        driver.navigate().to("http://intershop5.skillbox.ru/my-account/");
        driver.findElement(passwordRecoveryLinkLocator).click();

        // Ввод значений логина и пароля
        driver.findElement(inputLoginLocator).sendKeys(LOGIN + MAIL_PREFIX);
        driver.findElement(resetPasswordButtonLocator).click();

        var actualMessage = driver.findElement(recoveryMessageLocator).getText();
        Assert.assertEquals("Пароль не восстановлен", expectedMessage, actualMessage);
    }


    // Восстановление пароля несуществующего пользователя
    @Test
    public void passwordRecovery_UnrealUser() {

        By passwordRecoveryLinkLocator = By.xpath("//*[@class='woocommerce-LostPassword lost_password']//a");
        By inputLoginLocator = By.xpath("//*[@id='user_login']");
        By resetPasswordButtonLocator = By.xpath("//*[@value='Reset password']");
        By recoveryMessageLocator = By.xpath("//*[@role='alert']");

        var expectedMessage = "Неверное имя пользователя или почта.";

        driver.navigate().to("http://intershop5.skillbox.ru/my-account/");
        driver.findElement(passwordRecoveryLinkLocator).click();

        // Ввод значений логина и пароля
        driver.findElement(inputLoginLocator).sendKeys("eafwsbeafu46531");
        driver.findElement(resetPasswordButtonLocator).click();

        var actualMessage = driver.findElement(recoveryMessageLocator).getText();
        Assert.assertEquals("Попытка восстановить несуществующего пользователя успешна",
                expectedMessage, actualMessage);
    }
}