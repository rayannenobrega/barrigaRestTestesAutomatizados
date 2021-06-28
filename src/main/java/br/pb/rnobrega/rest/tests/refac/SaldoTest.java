package br.pb.rnobrega.rest.tests.refac;

import br.pb.rnobrega.rest.core.BaseTest;
import br.pb.rnobrega.rest.utils.BarrigaUtils;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SaldoTest extends BaseTest {

    @Test
    public void deveCalcularSaldoDasContas(){

        Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta para saldo");

        given()
                .when()
                .get("/saldo")
                .then()
                .statusCode(200)
                .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("534.00"))
        ;
    }




}
