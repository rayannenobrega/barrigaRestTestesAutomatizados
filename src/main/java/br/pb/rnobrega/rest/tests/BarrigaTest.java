package br.pb.rnobrega.rest.tests;

import br.pb.rnobrega.rest.core.BaseTest;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


//É necessário extender o BaseTest que criamos.
public class BarrigaTest extends BaseTest {

    private String TOKEN;

    @Before
    public void loginToken(){
        //Mapeando o Login
        Map<String, String> login = new HashMap<String, String >();
        login.put("email", "rayanne@email");
        login.put("senha", "casa123");

        //Login na API e recebimento de TOKEN
        TOKEN = given()
                .body(login) //Passando o login em formato de MAP para melhor organização
                .when()
                .post("/signin")
                .then()
                .log().all()
                .statusCode(200)
                .extract().path("token")
        ;
    }

    @Test
    public void naoDeveAcessarAPISemToken(){
        given()
        .when()
                .get("/contas")
        .then()
                .statusCode(401)
        ;
    }

    @Test
    public void deveIncluirContaComSucesso(){

        given()
                .header("Authorization", "JWT " + TOKEN) //API's mais recentes ao inves de 'JWT' se usa 'bearer'.
                .body("{\"nome\": \"conta qualquer\"}") //Passa o JSON com as informações que se quer
        .when()
                .post("/contas")//Método post para envio
        .then()
                .statusCode(201)
        ;

    }

    @Test
    public void deveAlterarContaComSucesso(){

        given()
                .header("Authorization", "JWT " + TOKEN) //API's mais recentes ao inves de 'JWT' se usa 'bearer'.
                .body("{\"nome\": \"conta alterada\"}")
        .when()
                .put("/contas/658079")//comando do tipo PUT para alteração para /contas/:id
        .then()
                .statusCode(200)
                .body("nome", is("conta alterada"))
        ;
    }
}

