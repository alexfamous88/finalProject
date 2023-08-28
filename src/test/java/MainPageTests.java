import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class MainPageTests {

    WebDriver driver;
    WebDriverWait wait;

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


    // Переход с Главной страницы в Каталог
    @Test
    public void mainPage_Navigation_Catalog() {

        By catalogNavLocator = By.xpath("(//*[@id='menu-primary-menu']/*)[2]");
        By catalogExistLocator = By.xpath("//*[@id='title_bread_wrap']//span");

        var expectedTitleCatalog = "Каталог";

        driver.navigate().to("http://intershop5.skillbox.ru/");
        driver.findElement(catalogNavLocator).click();
        var actualTitleCatalog = driver.findElement(catalogExistLocator).getText();
        Assert.assertEquals("Переход в Каталог не осуществлен", expectedTitleCatalog, actualTitleCatalog);
    }


    // Переход с Главной страницы в Мой аккаунт
    @Test
    public void mainPage_Navigation_MyAccount() {

        By myAccountNavLocator = By.xpath("(//*[@id='menu-primary-menu']/*)[3]");
        By myAccountExistLocator = By.xpath("//*[@id='accesspress-breadcrumb']/span");

        var expectedTitleMyAccount = "Мой Аккаунт";

        driver.navigate().to("http://intershop5.skillbox.ru/");
        driver.findElement(myAccountNavLocator).click();
        var actualTitleMyAccount = driver.findElement(myAccountExistLocator).getText();
        Assert.assertEquals("Переход в Мой аккаунт не осуществлен", expectedTitleMyAccount, actualTitleMyAccount);
    }


    // Переход с Главной страницы в Корзину
    @Test
    public void mainPage_Navigation_Cart() {

        By cartNavLocator = By.xpath("(//*[@id='menu-primary-menu']/*)[4]");
        By cartExistLocator = By.xpath("//*[@id='accesspress-breadcrumb']/span");

        var expectedTitleCart = "Корзина";

        driver.navigate().to("http://intershop5.skillbox.ru/");
        driver.findElement(cartNavLocator).click();
        var actualTitleCart = driver.findElement(cartExistLocator).getText();
        Assert.assertEquals("Переход в Корзину не осуществлен", expectedTitleCart, actualTitleCart);
    }


    // Переход с Главной страницы в Оформление заказа
    @Test
    public void mainPage_Navigation_Order() {

        By orderNavLocator = By.xpath("(//*[@id='menu-primary-menu']/*)[5]");
        By orderExistLocator = By.xpath("//*[@id='accesspress-breadcrumb']/span");

        var expectedTitleOrder = "Корзина";

        driver.navigate().to("http://intershop5.skillbox.ru/");
        driver.findElement(orderNavLocator).click();
        var actualTitleOrder = driver.findElement(orderExistLocator).getText();
        Assert.assertEquals("Переход в Оформление заказа не осуществлен", expectedTitleOrder, actualTitleOrder);
    }


    // Поиск товара в хедере Главной страницы
    @Test
    public void mainPage_Searching() {

        // Набор локаторов
        By textInputLocator = By.xpath("//*[@class='searchform']/*[@type='text']");
        By buttonSearchLocator = By.xpath("//*[@class='searchform']/*[@type='submit']");
        By resultsOfSearchLocator = By.xpath("//*[@class='entry-title ak-container']");

        // Набор переменных с ожидаемым результатом
        var searchingObject = "стиральная машина";
        var expectedResult = ("РЕЗУЛЬТАТЫ ПОИСКА: “" + searchingObject + "”").toLowerCase();

        driver.navigate().to("http://intershop5.skillbox.ru/");
        driver.findElement(textInputLocator).sendKeys(searchingObject);
        driver.findElement(buttonSearchLocator).click();
        var actualResult = driver.findElement(resultsOfSearchLocator).getText().toLowerCase();
        Assert.assertEquals("Поиск осуществлен по другой категории", expectedResult, actualResult);
    }


    // Взаимодействие Главной страницы с промо-разделами
    @Test
    public void mainPage_Promo() {

        // Набор локаторов промо-элементов
        By firstPromoLocator = By.xpath("//*[@id='accesspress_storemo-2']");
        By secondPromoLocator = By.xpath("//*[@id='accesspress_storemo-3']");
        By thirdPromoLocator = By.xpath("//*[@id='accesspress_storemo-4']");

        // Набор локаторов заголовков промо
        By firstPromoTitle = By.xpath("//*[@class='entry-title ak-container']");
        By secondPromoTitle = By.xpath("//*[@class='entry-title ak-container']");
        By thirdPromoTitle = By.xpath("//*[@class='entry-title ak-container']");

        // Набор переменных с ожидаемым результатом
        var firstPromoExpect = "КНИГИ";
        var secondPromoExpect = "ПЛАНШЕТЫ";
        var thirdPromoExpect = "ФОТОАППАРАТЫ";

        // Проверка первого Промо
        driver.navigate().to("http://intershop5.skillbox.ru/");
        driver.findElement(firstPromoLocator).click();
        var actualResultFirstPromo = driver.findElement(firstPromoTitle).getText().toLowerCase();
        Assert.assertEquals("Ссылка для промо \"" + firstPromoExpect + "\" некорректна",
                firstPromoExpect.toLowerCase(), actualResultFirstPromo);

        // Проверка второго Промо
        driver.navigate().to("http://intershop5.skillbox.ru/");
        driver.findElement(secondPromoLocator).click();
        var actualResultSecondPromo = driver.findElement(secondPromoTitle).getText().toLowerCase();
        Assert.assertEquals("Ссылка для промо \"" + secondPromoExpect + "\" некорректна",
                secondPromoExpect.toLowerCase(), actualResultSecondPromo);

        // Проверка третьего Промо
        driver.navigate().to("http://intershop5.skillbox.ru/");
        driver.findElement(thirdPromoLocator).click();
        var actualResultThirdPromo = driver.findElement(thirdPromoTitle).getText().toLowerCase();
        Assert.assertEquals("Ссылка для промо \"" + thirdPromoExpect + "\" некорректна",
                thirdPromoExpect.toLowerCase(), actualResultThirdPromo);
    }


    // Проверка перехода к товару "Уже в продаже"
    @Test
    public void mainPage_NowAvailable_Link() {

        // Набор локаторов
        By bannerLinkLocator = By.xpath("//*[@id='accesspress_store_full_promo-2']//a");
        By widgetTextLocator = By.xpath("//*[@id='accesspress_store_full_promo-2']//*[@class='widget-title']");
        By goodNameLocator = By.xpath("//*[@class='promo-desc-title']");
        By goodByLinkLocator = By.xpath("//*[@id='primary']//h1");

        // Проверка перехода при клике на баннер
        driver.navigate().to("http://intershop5.skillbox.ru/");
        ((JavascriptExecutor) driver).executeScript("scroll(0,800)");
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(widgetTextLocator)));
        var expectedGood = driver.findElement(goodNameLocator).getText().toLowerCase();
        driver.findElement(bannerLinkLocator).click();
        var actualGood = driver.findElement(goodByLinkLocator).getText().toLowerCase();
        Assert.assertTrue("При переходе по баннеру открывается карточка другого товара",
                actualGood.startsWith(expectedGood));
    }


    // Проверка функционала "Просмотренные товары"
    @Test
    public void mainPage_ViewedProducts() {

        // Набор локаторов
        By catalogNavLocator = By.xpath("//*[@id='menu-item-46']");
        By anyGoodInCatalogLocator = By.xpath("(//*[@id='primary']//li[contains(@class, 'product')])[1]");
        By anyGoodInCatalogTitleLocator = By.xpath("//h1[@class='product_title entry-title']");
        By anyGoodInMainTitleLocator = By.xpath("(//*[@class='product_list_widget']//*[@class='product-title'])[1]");

        driver.navigate().to("http://intershop5.skillbox.ru/");
        driver.findElement(catalogNavLocator).click();
        driver.findElement(anyGoodInCatalogLocator).click();
        var expectedViewedGood = driver.findElement(anyGoodInCatalogTitleLocator).getText();
        driver.navigate().to("http://intershop5.skillbox.ru/");
        ((JavascriptExecutor) driver).executeScript("scroll(0,4000)");
        var actualViewedGood = driver.findElement(anyGoodInMainTitleLocator).getText();

        Assert.assertEquals("В просмотренных товарах не отображается нужный товар",
                expectedViewedGood, actualViewedGood);
    }


    // Проверка наличия контактов в шапке Главной
    @Test
    public void mainPage_ContactsAboveHeader_isPresent() {

        // Набор переменных с ожидаемым результатом
        var phoneExpected = "+7-999-123-12-12";
        var emailExpected = "skillbox@skillbox.ru";

        // Набор локаторов
        By phoneHeaderLocator = By.xpath("(//*[@id='custom_html-2']//a)[1]");
        By emailHeaderLocator = By.xpath("(//*[@id='custom_html-2']//a)[2]");

        driver.navigate().to("http://intershop5.skillbox.ru/");

        // Проверка соответствия номера телефона
        var actualPhone = driver.findElement(phoneHeaderLocator).getText();
        Assert.assertEquals("Номер телефона отличается от ожидаемого", phoneExpected, actualPhone);

        // Проверка соответствия Email
        var actualEmail = driver.findElement(emailHeaderLocator).getText();
        Assert.assertEquals("Email отличается от ожидаемого", emailExpected, actualEmail);
    }


    // Проверка наличия контактов в футере Главной
    @Test
    public void mainPage_Contacts_InFooter() {

        // Набор переменных с ожидаемым результатом
        var phoneExpected = "Телефон: +7-999-123-12-12";
        var emailExpected = "Email: skillbox@skillbox.ru";

        // Набор локаторов
        By phoneLocator = By.xpath("(//*[@id='top-footer']//*[@class='text-5-value'])[1]");
        By emailLocator = By.xpath("(//*[@id='top-footer']//*[@class='text-5-value'])[2]");

        driver.navigate().to("http://intershop5.skillbox.ru/");
        ((JavascriptExecutor) driver).executeScript("scroll(0,4000)");
        wait.until(ExpectedConditions.elementToBeClickable(phoneLocator));

        var actualPhone = driver.findElement(phoneLocator).getText();
        var actualEmail = driver.findElement(emailLocator).getText();

        Assert.assertEquals("Номер телефона не соответствует ожидаемому", phoneExpected, actualPhone);
        Assert.assertEquals("Email не соответствует ожидаемому", emailExpected, actualEmail);
    }


    // Проверка перехода "Все товары" из футера
    @Test
    public void mainPage_NavigationInFooter_AllGoods() {

        var allGoodsExpected = "ВСЕ ТОВАРЫ";

        By allGoodsLocator = By.xpath("//*[@id='pages-2']//*[contains(@class, 'page-item-33')]//a");

        driver.navigate().to("http://intershop5.skillbox.ru/");
        ((JavascriptExecutor) driver).executeScript("scroll(0,4000)");
        driver.findElement(allGoodsLocator).click();
        var allGoodsActual = driver.findElement(By.xpath("//*[@id='title_bread_wrap']//span")).getText();
        Assert.assertEquals("Переход в раздел \"Все товары\" не осуществлен",
                allGoodsExpected.toLowerCase(), allGoodsActual.toLowerCase());
    }


    // Проверка перехода "Главная" из футера
    @Test
    public void mainPage_NavigationInFooter_MainPage() {

        var mainPageExpected = "http://intershop5.skillbox.ru/";

        By mainPageLocator = By.xpath("//*[@id='pages-2']//*[contains(@class, 'page-item-39')]//a");

        driver.navigate().to("http://intershop5.skillbox.ru/");
        ((JavascriptExecutor) driver).executeScript("scroll(0,4000)");
        driver.findElement(mainPageLocator).click();
        var mainPageActual = driver.getCurrentUrl();
        Assert.assertEquals("Переход на Главную не осуществлен",
                mainPageExpected.toLowerCase(), mainPageActual.toLowerCase());
    }


    // Проверка перехода "Корзина" из футера
    @Test
    public void mainPage_NavigationInFooter_Cart() {

        var cartExpected = "Корзина";

        By cartLocator = By.xpath("//*[@id='pages-2']//*[contains(@class, 'page-item-20')]//a");

        driver.navigate().to("http://intershop5.skillbox.ru/");
        ((JavascriptExecutor) driver).executeScript("scroll(0,4000)");
        driver.findElement(cartLocator).click();
        var cartActual = driver.findElement(By.xpath("//*[@id='accesspress-breadcrumb']/span")).getText();
        Assert.assertEquals("Переход в Корзину не осуществлен",
                cartExpected.toLowerCase(), cartActual.toLowerCase());
    }


    // Проверка перехода "Мой аккаунт" из футера
    @Test
    public void mainPage_NavigationInFooter_MyAccount() {

        var myAccountExpected = "Мой Аккаунт";

        By myAccountLocator = By.xpath("//*[@id='pages-2']//*[contains(@class, 'page-item-22')]//a");

        driver.navigate().to("http://intershop5.skillbox.ru/");
        ((JavascriptExecutor) driver).executeScript("scroll(0,4000)");
        driver.findElement(myAccountLocator).click();
        var myAccountActual = driver.findElement(By.xpath("//*[@id='accesspress-breadcrumb']/span")).getText();
        Assert.assertEquals("Переход в Мой аккаунт не осуществлен",
                myAccountExpected.toLowerCase(), myAccountActual.toLowerCase());
    }


    // Проверка перехода "Оформление заказа" из футера
    @Test
    public void mainPage_NavigationInFooter_Order() {

        var orderExpected = "Корзина";

        By orderLocator = By.xpath("//*[@id='pages-2']//*[contains(@class, 'page-item-24')]//a");

        driver.navigate().to("http://intershop5.skillbox.ru/");
        ((JavascriptExecutor) driver).executeScript("scroll(0,4000)");
        driver.findElement(orderLocator).click();
        var orderActual = driver.findElement(By.xpath("//*[@id='accesspress-breadcrumb']/span")).getText();
        Assert.assertEquals("Переход в Оформление заказа не осуществлен",
                orderExpected.toLowerCase(), orderActual.toLowerCase());
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
}