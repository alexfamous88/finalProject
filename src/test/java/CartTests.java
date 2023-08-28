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

public class CartTests {

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


    // Изменение количества товара в корзине
    @Test
    public void changeQuantity_InCart() {

        By buttonInCartLocator = By.xpath("//*[@class='added_to_cart wc-forward']");
        By quantityInputLocator = By.xpath("//*[contains(@id, 'quantity')]");
        By loaderLocator = By.xpath("//form[contains(@class, 'processing')]");
        By messageLocator = By.xpath("//*[@role='alert']");
        var changeQuantityNum = "2";
        var expectedMessage = "Cart updated.";
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        // Цикл с поиском первого товара из списка в наличии и его проверки с добавлением в корзину
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ")) {
                driver.findElement(buttonLocator).click();
                driver.findElement(buttonInCartLocator).click();
                i = -1;
            }
        }
        // Изменение количества товара в корзине
        driver.findElement(quantityInputLocator).click();
        driver.findElement(quantityInputLocator).sendKeys(Keys.BACK_SPACE);
        driver.findElement(quantityInputLocator).sendKeys(changeQuantityNum);
        driver.findElement(quantityInputLocator).sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(loaderLocator));

        // Проверка соответствия сообщения об обновлении корзины
        var actualMessage = driver.findElement(messageLocator).getText();
        Assert.assertEquals("Сообщение об обновлении корзины некорректно",
                expectedMessage, actualMessage);
    }


    // Удаление товара из корзины
    @Test
    public void removeGood_InCart() {
        By buttonInCartLocator = By.xpath("//*[@class='added_to_cart wc-forward']");
        By removeButtonLocator = By.xpath("//td[@class='product-remove']//a");
        By emptyCartLocator = By.xpath("//*[@class='cart-empty woocommerce-info']");
        var expectedEmptyCart = "Корзина пуста.";

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        // Цикл с поиском первого товара из списка в наличии и его проверки с добавлением в корзину
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ")) {
                driver.findElement(buttonLocator).click();
                driver.findElement(buttonInCartLocator).click();
                i = -1;
            }
        }
        // Удаление товара из корзины
        driver.findElement(removeButtonLocator).click();

        // Проверка пустой корзины
        var actualEmptyCart = driver.findElement(emptyCartLocator).getText();
        Assert.assertEquals("Корзина не стала пустой", expectedEmptyCart, actualEmptyCart);
    }


    // Возвращение товара в корзину
    @Test
    public void returnGood_InCart() {
        By buttonInCartLocator = By.xpath("//*[@class='added_to_cart wc-forward']");
        By removeButtonLocator = By.xpath("//td[@class='product-remove']//a");
        By returnLocator = By.xpath("//*[@role='alert']//a");
        By goodNameLocator = By.xpath("//td[@class='product-name']//a");

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        // Цикл с поиском первого товара из списка в наличии и его проверки с добавлением в корзину
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ")) {
                driver.findElement(buttonLocator).click();
                driver.findElement(buttonInCartLocator).click();
                i = -1;
            }
        }
        // Удаление товара из корзины
        var expectedName = driver.findElement(goodNameLocator).getText();
        driver.findElement(removeButtonLocator).click();
        driver.findElement(returnLocator).click();
        var actualName = driver.findElement(goodNameLocator).getText();

        Assert.assertEquals("Выбранный товар не возвращен в корзину", expectedName, actualName);
    }


    // Проверка сообщения об удалении товара из корзины
    @Test
    public void returnGood_InCart_CorrectMessage() {
        By buttonInCartLocator = By.xpath("//*[@class='added_to_cart wc-forward']");
        By removeButtonLocator = By.xpath("//td[@class='product-remove']//a");
        By messageLocator = By.xpath("//*[@role='alert']");
        By goodNameLocator = By.xpath("//td[@class='product-name']//a");

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        // Цикл с поиском первого товара из списка в наличии и его проверки с добавлением в корзину
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ")) {
                driver.findElement(buttonLocator).click();
                driver.findElement(buttonInCartLocator).click();
                i = -1;
            }
        }
        // Удаление товара из корзины
        driver.findElement(removeButtonLocator).click();

        var expectedMessage = "“" + driver.findElement(goodNameLocator).getText() + "” удален. Вернуть?";
        var actualMessage = driver.findElement(messageLocator).getText();
        Assert.assertEquals("Уведомление об удалении некорректно", expectedMessage, actualMessage);
    }

    // Возвращение из пустой корзины в магазин
    @Test
    public void returnInCatalog_FromEmptyCart() {
        By buttonInCartLocator = By.xpath("//*[@class='added_to_cart wc-forward']");
        By removeButtonLocator = By.xpath("//td[@class='product-remove']//a");
        By returnToShopButton = By.xpath("//*[@class='button wc-backward']");
        var expectedUrl = "http://intershop5.skillbox.ru/shop/";

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        // Цикл с поиском первого товара из списка в наличии и его проверки с добавлением в корзину
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ")) {
                driver.findElement(buttonLocator).click();
                driver.findElement(buttonInCartLocator).click();
                i = -1;
            }
        }
        // Удаление товара из корзины
        driver.findElement(removeButtonLocator).click();

        // Переход назад в магазин
        driver.findElement(returnToShopButton).click();

        var actualUrl = driver.getCurrentUrl();

        Assert.assertEquals("Переход в магазин не осуществлен", expectedUrl, actualUrl);
    }


    // Применение валидного промокода
    @Test
    public void addValidPromoCode_InCart() {
        By buttonInCartLocator = By.xpath("//*[@class='added_to_cart wc-forward']");
        By couponInputLocator = By.xpath("//*[@id='coupon_code']");
        By couponConfirmButton = By.xpath("//*[@name='apply_coupon']");
        By messageLocator = By.xpath("//*[@role='alert']");
        By productPriceLocator = By.xpath("(//*[@class='product-price'])[2]");
        By totalPriceLocator = By.xpath("//*[@class='order-total']//bdi");
        By loaderLocator = By.xpath("//form[contains(@class, 'processing')]");
        var expectedMessage = "Купон успешно добавлен.";
        var coupon = "sert500";

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        // Цикл с поиском первого товара из списка в наличии и его проверки с добавлением в корзину
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ")) {
                driver.findElement(buttonLocator).click();
                driver.findElement(buttonInCartLocator).click();
                i = -1;
            }
        }
        // Применение промокода
        driver.findElement(couponInputLocator).sendKeys(coupon);
        driver.findElement(couponConfirmButton).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(loaderLocator));

        // Проверка уведомления о применении купона
        var actualMessage = driver.findElement(messageLocator).getText();
        Assert.assertEquals("Нет уведомления о применении купона", expectedMessage, actualMessage);

        // Проверка отработки примененного промокода
        int productPrice = Integer.parseInt(driver.findElement(productPriceLocator).getText().replace(",00₽", ""));
        int productPriceDiscount = Integer.parseInt(driver.findElement(totalPriceLocator).getText().replace(",00₽", ""));
        Assert.assertTrue("Промокод не применен", productPrice - productPriceDiscount == 500);
    }


    // Применение невалидного промокода
    @Test
    public void addInvalidPromoCode_InCart() {
        By buttonInCartLocator = By.xpath("//*[@class='added_to_cart wc-forward']");
        By couponInputLocator = By.xpath("//*[@id='coupon_code']");
        By couponConfirmButton = By.xpath("//*[@name='apply_coupon']");
        By messageLocator = By.xpath("//*[@role='alert']");
        By productPriceLocator = By.xpath("(//*[@class='product-price'])[2]");
        By totalPriceLocator = By.xpath("//*[@class='order-total']//bdi");
        var expectedMessage = "Неверный купон.";
        var coupon = "123";

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        // Цикл с поиском первого товара из списка в наличии и его проверки с добавлением в корзину
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ")) {
                driver.findElement(buttonLocator).click();
                driver.findElement(buttonInCartLocator).click();
                i = -1;
            }
        }
        // Применение промокода
        driver.findElement(couponInputLocator).sendKeys(coupon);
        driver.findElement(couponConfirmButton).click();

        // Проверка уведомления о применении купона
        var actualMessage = driver.findElement(messageLocator).getText();
        Assert.assertEquals("Нет уведомления о невалидном купоне", expectedMessage, actualMessage);

        // Проверка неизменности цены
        var expectedPrice = driver.findElement(productPriceLocator).getText();
        var actualPrice = driver.findElement(totalPriceLocator).getText();
        Assert.assertEquals("Промокод применился", expectedPrice, actualPrice);
    }


    // Удаление промокода
    @Test
    public void removePromoCode_InCart() {
        By buttonInCartLocator = By.xpath("//*[@class='added_to_cart wc-forward']");
        By couponInputLocator = By.xpath("//*[@id='coupon_code']");
        By couponConfirmButton = By.xpath("//*[@name='apply_coupon']");
        By productPriceLocator = By.xpath("(//*[@class='product-price'])[2]");
        By totalPriceLocator = By.xpath("//*[@class='order-total']//bdi");
        By loaderLocator = By.xpath("//form[contains(@class, 'processing')]");
        By removePromoCodeButton = By.xpath("//*[@class='woocommerce-remove-coupon']");
        var coupon = "sert500";

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        // Цикл с поиском первого товара из списка в наличии и его проверки с добавлением в корзину
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ")) {
                driver.findElement(buttonLocator).click();
                driver.findElement(buttonInCartLocator).click();
                i = -1;
            }
        }
        // Применение промокода
        driver.findElement(couponInputLocator).sendKeys(coupon);
        driver.findElement(couponConfirmButton).click();

        // Удаление промокода
        driver.findElement(removePromoCodeButton).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(loaderLocator));

        // Проверка возврата первоначальной цены
        var expectedPrice = driver.findElement(productPriceLocator).getText();
        var actualPrice = driver.findElement(totalPriceLocator).getText();
        Assert.assertEquals("Промокод не был удален", expectedPrice, actualPrice);
    }


    // Проверка отображения цены на товар со скидкой в корзине
    @Test
    public void discountPrice_IsDisplay_InCart() {
        By buttonInCartLocator = By.xpath("//*[@class='added_to_cart wc-forward']");
        By productPriceLocator = By.xpath("(//*[@class='product-price'])[2]");

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        // Цикл с поиском первого товара из списка в наличии и его проверки с добавлением в корзину
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            By onSaleLocator = By.xpath("(//*[@class='inner-img'])[" + i + "]//span[@class='onsale']");
            By discountPriceLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//ins");
            var buttonText = driver.findElement(buttonLocator).getText();
            if (buttonText.equals("В КОРЗИНУ") && driver.findElement(onSaleLocator).isDisplayed()) {
                var expectedPrice = driver.findElement(discountPriceLocator).getText();
                driver.findElement(buttonLocator).click();
                driver.findElement(buttonInCartLocator).click();
                var actualPrice = driver.findElement(productPriceLocator).getText();

                // Проверка соответствия цены в каталоге и в корзине
                Assert.assertEquals("Цена в каталоге и в корзине разная", expectedPrice, actualPrice);
                i = -1;
            }
        }
    }
}
