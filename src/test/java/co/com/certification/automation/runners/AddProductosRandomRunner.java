package co.com.certification.automation.runners;



import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;


@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources/features/agregar_aleatoriamente_productos.feature",
        glue = {"co.com.certification.automation.stepdefinitions",
                "co.com.certification.automation.webHooks"}
)
public class AddProductosRandomRunner {

}
