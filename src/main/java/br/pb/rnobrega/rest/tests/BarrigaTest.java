package br.pb.rnobrega.rest.tests;

import br.pb.rnobrega.rest.core.BaseTest;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


//É necessário extender o BaseTest que criamos.
public class BarrigaTest extends BaseTest {

    @Test
    public void naoDeveAcessarAPISemToken(){
        given()
        .when()
                .get("/contas")
        .then()
                .statusCode(401)
        ;
    }
}
