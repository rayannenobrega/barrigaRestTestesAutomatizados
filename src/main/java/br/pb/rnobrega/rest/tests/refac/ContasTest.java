package br.pb.rnobrega.rest.tests.refac;

import io.restassured.RestAssured;
import org.junit.BeforeClass;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

public class ContasTest {

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
        RestAssured.get("/reset").then().statusCode(200);
    }
}
