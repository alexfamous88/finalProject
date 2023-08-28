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

public class CatalogTests {

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


    // Проверка функционала добавления товара в корзину из списка товаров в Каталоге
    @Test
    public void addGood_ToCart_FromCatalog() {

        By buttonInCartLocator = (By.xpath("//*[@class='added_to_cart wc-forward']"));
        By actualNameLocator = By.xpath("//td[@class='product-name']/a");

        var expectedLink = "http://intershop5.skillbox.ru/cart/";

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        // Цикл с поиском первого товара из списка в наличии и его проверки с добавлением в корзину
        for (int i = 1; i > 0; i++) {

            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            By nameInCatalogLocator = By.xpath("(//*[@class='collection_title'])[" + i + "]");

            var buttonText = driver.findElement(buttonLocator).getText();
            var expectedName = driver.findElement(nameInCatalogLocator).getText();

            if (buttonText.equals("В КОРЗИНУ")) {

                driver.findElement(buttonLocator).click();

                driver.findElement(buttonInCartLocator).click();

                wait.until(ExpectedConditions.elementToBeClickable(actualNameLocator));

                var actualLink = driver.getCurrentUrl();
                var actualName = driver.findElement(actualNameLocator).getText();

                Assert.assertEquals("Переход в корзину не осуществлен", expectedLink, actualLink);
                Assert.assertEquals("Наименование товара не соответствует ожидаемому", expectedName, actualName);

                i = -1;
            }
        }
    }


    // Проверка добавления товара в корзину из карточки
    @Test
    public void addGood_ToCart_FromCardOfGood() {

        By buttonAddToCartLocator = By.xpath("//*[@name='add-to-cart']");
        By messageAboutAdditionLocator = By.xpath("//*[@role='alert']");
        By buttonAdditionLocator = By.xpath("//*[@role='alert']//a");
        By nameInCardLocator = By.xpath("//h1[@class='product_title entry-title']");
        By actualNameLocator = By.xpath("//td[@class='product-name']/a");

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        // Цикл с поиском первого товара из списка в наличии и его проверки с добавлением в корзину
        for (int i = 1; i > 0; i++) {

            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            By cardPicLocator = By.xpath("(//*[@class='inner-img'])[" + i + "]//a");

            var buttonText = driver.findElement(buttonLocator).getText();

            if (buttonText.equals("В КОРЗИНУ")) {

                driver.findElement(cardPicLocator).click();

                // Проверка добавления товара в корзину
                driver.findElement(buttonAddToCartLocator).click();

                wait.until(ExpectedConditions.presenceOfElementLocated(messageAboutAdditionLocator));

                var expectedName = driver.findElement(nameInCardLocator).getText();

                var actualMessage = driver.findElement(messageAboutAdditionLocator).getText();
                var expectedMessage = "Подробнее\n" +
                        "Вы отложили “" + expectedName + "” в свою корзину.";

                // Проверка соответствия сообщения о добавлении в корзину
                Assert.assertEquals("Некорректное уведомление о добавлении товара в корзину",
                        expectedMessage, actualMessage);

                // Проверка перехода после добавления товара в корзину
                driver.findElement(buttonAdditionLocator).click();

                var actualLink = driver.getCurrentUrl();
                var expectedLink = "http://intershop5.skillbox.ru/cart/";
                Assert.assertEquals("Переход в корзину не осуществлен", expectedLink, actualLink);

                // Проверка соответствия наименования товара в корзине
                var actualName = driver.findElement(actualNameLocator).getText();
                Assert.assertEquals("Наименование товара в корзине не соответствует ожидаемому",
                        expectedName, actualName);

                i = -1;
            }
        }
    }


    // Проверка работоспособности навигации по категориям товаров
    @Test
    public void Catalog_Navigation_ByCategory() {

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        var categoriesCount = driver.findElements(By.xpath("//*[@class='product-categories']//li")).size();

        // Цикл для поочередного перехода к каждой категории товаров и проверки
        for (int i = 1; i <= categoriesCount; i++) {

            By categoryLocator = By.xpath("(//*[@id='woocommerce_product_categories-2']//a)[" + i + "]");
            By categoryTitleLocator = By.xpath("//*[@class='entry-title ak-container']");

            var expectedCategory = driver.findElement(categoryLocator).getText().toLowerCase();

            driver.findElement(categoryLocator).click();

            var actualCategory = driver.findElement(categoryTitleLocator).getText().toLowerCase();

            Assert.assertEquals("По категории \"" + expectedCategory + "\" осуществляется переход к другой категории",
                    expectedCategory, actualCategory);
        }
    }


    // Проверка работоспособности навигации по блоку "Товары"
    @Test
    public void Catalog_Navigation_Goods() {

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        var goodsCount = driver.findElements(By.xpath("//*[@class='product_list_widget']//li")).size();

        // Цикл для поочередного перехода к каждому товару и проверки
        for (int i = 1; i <= goodsCount; i++) {

            By goodsLocator = By.xpath("(//*[@id='woocommerce_products-2']//a)[" + i + "]");
            By goodsNameLocator = By.xpath("(//*[@id='woocommerce_products-2']//*[@class='product-title'])[" + i + "]");
            By goodsTitleLocator = By.xpath("//*[@class='product_title entry-title']");
            var expectedGoods = driver.findElement(goodsNameLocator).getText().toLowerCase();

            driver.findElement(goodsLocator).click();

            var actualCategory = driver.findElement(goodsTitleLocator).getText().toLowerCase();

            Assert.assertEquals("По товару \"" + expectedGoods + "\" осуществляется переход к другому товару",
                    expectedGoods, actualCategory);

            driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        }
    }


    // Проверка функционала карточек товаров, отсутствующих на складе
    @Test
    public void Catalog_Goods_OutOfStock() {

        By nameInCardLocator = By.xpath("//*[@class='summary entry-summary']/h1");
        By quantityLocator = By.xpath("//*[@class='summary entry-summary']/*[@class='stock out-of-stock']");

        var expectedQuantity = "Out of stock";

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        // Цикл с поиском первого товара из списка с отсутствием на складе и его проверки
        for (int i = 1; i > 0; i++) {
            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            By nameInCatalogLocator = By.xpath("(//*[@class='collection_title'])[" + i + "]");
            var buttonText = driver.findElement(buttonLocator).getText();

            if (buttonText.equals("Read more")) {

                var expectedName = driver.findElement(nameInCatalogLocator).getText();

                driver.findElement(buttonLocator).click();

                var actualName = driver.findElement(nameInCardLocator).getText();
                var actualQuantity = driver.findElement(quantityLocator).getText();

                Assert.assertEquals("Наименование товара не соответствует ожидаемому", expectedName, actualName);
                Assert.assertEquals("Количество товара не соответствует ожидаемому", expectedQuantity, actualQuantity);

                i = -1;
            }
        }
    }


    // Проверка отображения карточки товара в наличии
    @Test
    public void catalog_CardOfGood_IsEnabled() {

        By nameInCardLocator = By.xpath("//h1[@class='product_title entry-title']");

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        // Цикл с поиском первого товара из списка в наличии и его проверки с добавлением в корзину
        for (int i = 1; i > 0; i++) {

            By buttonLocator = By.xpath("(//*[@class='price-cart'])[" + i + "]//a");
            By nameInCatalogLocator = By.xpath("(//*[@class='collection_title'])[" + i + "]");
            By cardPicLocator = By.xpath("(//*[@class='inner-img'])[" + i + "]//a");

            var buttonText = driver.findElement(buttonLocator).getText();
            var expectedName = driver.findElement(nameInCatalogLocator).getText();

            if (buttonText.equals("В КОРЗИНУ")) {

                driver.findElement(cardPicLocator).click();

                var actualName = driver.findElement(nameInCardLocator).getText();

                Assert.assertEquals("Наименование товара не соответствует ожидаемому", expectedName, actualName);

                i = -1;
            }
        }
    }


    // Проверка работы сортировки товаров в каталоге
    @Test
    public void catalog_SortedList_IsEnabled() {

        By sortedListLocator = By.xpath("//*[@class='orderby']");
        By orderSorting = By.xpath("//*[@value='menu_order']");
        By popularitySorting = By.xpath("//*[@value='popularity']");
        By ratingSorting = By.xpath("//*[@value='rating']");
        By dateSorting = By.xpath("//*[@value='date']");
        By priceSorting = By.xpath("//*[@value='price']");
        By priceDescSorting = By.xpath("//*[@value='price-desc']");

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        driver.findElement(sortedListLocator).click();
        driver.findElement(orderSorting).click();

        driver.findElement(sortedListLocator).click();
        driver.findElement(popularitySorting).click();

        driver.findElement(sortedListLocator).click();
        driver.findElement(ratingSorting).click();

        driver.findElement(sortedListLocator).click();
        driver.findElement(dateSorting).click();

        driver.findElement(sortedListLocator).click();
        driver.findElement(priceSorting).click();

        driver.findElement(sortedListLocator).click();
        driver.findElement(priceDescSorting).click();
    }


    // Проверка работы пагинатора
    @Test
    public void catalog_Paginator_IsEnabled() {

        By page2Locator = By.xpath("(//ul[@class='page-numbers']/*)[2]");
        By pageInfoLocator = By.xpath("//*[@class='woocommerce-breadcrumb accesspress-breadcrumb']/span");

        var expectedPageInfo = "Page 2";

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        ((JavascriptExecutor) driver).executeScript("scroll(0,4000)");

        driver.findElement(page2Locator).click();
        var actualPageInfo = driver.findElement(pageInfoLocator).getText();

        Assert.assertEquals("Переход на страницу не осуществлен", expectedPageInfo, actualPageInfo);
    }
}