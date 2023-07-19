package co.com.certification.automation.tasks;

import co.com.certification.automation.model.Product;
import co.com.certification.automation.userinterface.CabecerasPage;
import io.cucumber.datatable.internal.difflib.Chunk;
import net.serenitybdd.core.pages.ClickStrategy;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.HoverOverTarget;
import net.serenitybdd.screenplay.actions.Scroll;
import net.serenitybdd.screenplay.actions.ScrollTo;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.questions.WebElementQuestion;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.Wait;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;


import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class SelectSeveralProductsRandomlyTask implements Task {

    Random random;
    private static List<Product> products;
    public SelectSeveralProductsRandomlyTask() {
        random= new Random();
        generateProductsListRandom();
    }

    private void generateProductsListRandom() {
        products = new ArrayList<>();
        Set<Integer> generatePositions = new HashSet<>();
        while (products.size() < 5) {
            int position = random.nextInt(12) + 1;
            boolean isPositionAvailable= isPositionAvailable(position);
            if (!(generatePositions.contains(position)) && isPositionAvailable)
            {
                generatePositions.add(position);
                int amount = random.nextInt(5) + 1;
                Product objeto = new Product(position, amount);
                products.add(objeto);
            }
        }
    }

    private static boolean isPositionAvailable(int position) {
        Integer[] availablePositionArray= new Integer[]{
                7
        };
        return  Arrays.stream(availablePositionArray).noneMatch(Predicate.isEqual(position));
    }

    @Override
    @Step("Seleccionar varios productos de cabecera de cama")
    public <T extends Actor> void performAs(T theActor) {
        String locateForProducts="div[id='gallery-layout-container'] > div:nth-child(%d) ";
        String locateForPriceProducts=locateForProducts+"div[class='exito-vtex-components-4-x-PricePDP']";
        String locateForRapidSaleProducts=locateForProducts+"div[class*='button-modal']";
        for (int i=0; i<products.size();i++){
            Product currentProduct=products.get(i);
            int productCurrentPosition=currentProduct.getPosition();
            By productRapidSaleSelector=By.cssSelector(
                    String.format(locateForRapidSaleProducts,productCurrentPosition));
            Target buttonRapidSaleProduct=Target.the(
                    "button rapid sale from product selected for add to shopping cart")
                    .located(productRapidSaleSelector);
            Point coordinates = buttonRapidSaleProduct.resolveFor(theActor).getCoordinates().onPage();
            int positionY=Math.abs(coordinates.y-180);
            BrowseTheWeb.as(theActor).evaluateJavascript(
                    String.format("window.scrollTo(%d, %d)",coordinates.x,positionY)
            );
            //
            Target elementPriceProduct=Target.the(
                            "product price selected for add to shopping cart")
                    .located(By.cssSelector(String.format(locateForPriceProducts,productCurrentPosition))
                    );
            String strPriceOfProduct=elementPriceProduct.resolveFor(theActor)
                    .getText();
            double priceOfProduct=Double.parseDouble(
                    strPriceOfProduct.replace("$ ","").replace(".","")
            );
            products.get(i).setPrice(priceOfProduct);
            //
            waitWithSleep(2);
            buttonRapidSaleProduct.resolveFor(theActor).waitUntilEnabled().click();
            //
            waitWithSleep(1);
            Target productNameElement=Target.the(
                    "the element with name of product").located(By.cssSelector("div[class*='quickPurchaseModalContainermodal']  span[class*='vtex-store-components-3-x-productBrand']")
                    );
            String nameOfProduct=productNameElement.resolveFor(theActor).getText();
            products.get(i).setNameOfProduct(nameOfProduct);
            //
            Target summaryButton=Target.the("button to add to shopping cart").located(
                    By.cssSelector("div[class*='productSummaryBuyButtonProductDetail'] button")
            );
            summaryButton.resolveFor(theActor).waitUntilEnabled().click();
            waitWithSleep(1);
            for (int j=0;j<currentProduct.getAmount()-1;j++){
                Target addOneItem=Target.the("button to add a item from the product")
                        .located(By.cssSelector("button[class*='product-summary-add-to-car-plus']"));
                addOneItem.resolveFor(theActor).waitUntilEnabled().click();
            }
            waitWithSleep(1);
            Target linkToContinueBuying=Target.the("link to continue buying").located(By.cssSelector("p[class='exito-vtex-components-4-x-continue']"));
            linkToContinueBuying.resolveFor(theActor).waitUntilEnabled().click();
            waitWithSleep(2);
        }
        BrowseTheWeb.as(theActor).evaluateJavascript(
                String.format("window.scrollTo(%d, %d)",60,400));
        waitWithSleep(7);

    }

    private static void waitWithSleep(long timeInSeconds) {
        try {
            Thread.sleep(1000*timeInSeconds);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());

        }
    }

    public static SelectSeveralProductsRandomlyTask selectSeveralProductsRandomlyTask(){
        return instrumented(SelectSeveralProductsRandomlyTask.class);
    }

    public static List<Product> getAllProductsInShoppingCart(){
        return products;
    }
}
