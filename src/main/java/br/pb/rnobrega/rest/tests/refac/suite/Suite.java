package br.pb.rnobrega.rest.tests.refac.suite;

import br.pb.rnobrega.rest.core.BaseTest;
import br.pb.rnobrega.rest.tests.refac.AuthTest;
import br.pb.rnobrega.rest.tests.refac.ContasTest;
import br.pb.rnobrega.rest.tests.refac.MovimentacaoTest;
import br.pb.rnobrega.rest.tests.refac.SaldoTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

//interpreta a classe como uma suite de testes
@RunWith(org.junit.runners.Suite.class)
//A anotação abaixo mostra quais as classes que deverão ser executadas
@SuiteClasses({
        ContasTest.class,
        MovimentacaoTest.class,
        SaldoTest.class,
        AuthTest.class
})
public class Suite extends BaseTest {
    @BeforeClass
    public static void loginToken(){
        //Mapeando o Login
        Map<String, String> login = new HashMap<String, String >();
        login.put("email", "rayanne@email");
        login.put("senha", "casa123");

        //Login na API e recebimento de TOKEN
        String TOKEN = given()
                .body(login) //Passando o login em formato de MAP para melhor organização
                .when()
                .post("/signin")
                .then()
                .log().all()
                .statusCode(200)
                .extract().path("token")
                ;

        requestSpecification.header("Authorization", "JWT " + TOKEN); //API's mais recentes ao inves de 'JWT' se usa 'bearer'.

        //Esse último comando reseta todo o banco a cada execução.
        RestAssured.get("/reset").then().statusCode(200);
    }

    //Como o reset reseta todo o banco, incluindo os ID's se tornam diferentes, é necessário esse método que trás pra gente o ID da conta a partir do nome.


}
