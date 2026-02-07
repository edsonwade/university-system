package code.with.vanilson.studentmanagement.bdd.steps;

import code.with.vanilson.studentmanagement.AbstractIntegrationTest;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration extends AbstractIntegrationTest {
}
