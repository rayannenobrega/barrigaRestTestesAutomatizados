package br.pb.rnobrega.rest.tests.refac;

import br.pb.rnobrega.rest.core.BaseTest;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.Test;


import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;


public class AuthTest extends BaseTest {


    @Test
    public void naoDeveAcessarAPISemToken(){
        //Removendo o TOKEN de autorização do método
        FilterableRequestSpecification req = (FilterableRequestSpecification) requestSpecification;
        req.removeHeader("Authorization");

        given()
        .when()
                .get("/contas")
        .then()
                .statusCode(401)
        ;
    }

}
