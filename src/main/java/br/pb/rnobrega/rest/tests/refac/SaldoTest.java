package br.pb.rnobrega.rest.tests.refac;

import br.pb.rnobrega.rest.core.BaseTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.Matchers.is;

public class SaldoTest extends BaseTest {

    @BeforeClass
    public static void loginToken(){
        //Mapeando o Login
        Map<String, String> login = new HashMap<String, String >();
        login.put("email", "rayanne@email");
        login.put("senha", "casa123");

        //Login na API e recebimento de TOKEN
        String TOKEN = given()
                .body(login)
        .when()
                .post("/signin")
        .then()
                .log().all()
                .statusCode(200)
                .extract().path("token")
        ;

        requestSpecification.header("Authorization", "JWT " + TOKEN);


        RestAssured.get("/reset").then().statusCode(200);
    }


    public Integer getIdContaPeloNome(String nome){
        return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
    }

    @Test
    public void deveCalcularSaldoDasContas(){

        Integer CONTA_ID = getIdContaPeloNome("Conta para saldo");

        given()
                .when()
                .get("/saldo")
                .then()
                .statusCode(200)
                .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("534.00"))
        ;
    }




}
