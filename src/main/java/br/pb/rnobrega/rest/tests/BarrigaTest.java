package br.pb.rnobrega.rest.tests;

import br.pb.rnobrega.rest.core.BaseTest;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
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

    //Para evitar repetição de código e ter que colocar essa parte em todas as de movimentação, foi criado um método pra ele.
    private Movimentacao getMovimentacaoValida(){

        Movimentacao mov = new Movimentacao();
        mov.setConta_id(658079);
        mov.setDescricao("Descrição da movimentação");
        mov.setEnvolvido("Envolvido na movimentacao");
        mov.setTipo("REC");
        mov.setData_transacao("01/01/2000");
        mov.setData_pagamento("10/05/2010");
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;

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
                .header("Authorization", "JWT " + TOKEN)
                .body("{\"nome\": \"conta alterada\"}")
        .when()
                .put("/contas/658079")//comando do tipo PUT para alteração para /contas/:id
        .then()
                .statusCode(200)
                .body("nome", is("conta alterada"))
        ;
    }

    @Test
    public void naoDeveIncluirContaComNomeRepetido(){

        given()
                .header("Authorization", "JWT " + TOKEN)
                .body("{\"nome\": \"conta qualquer\"}")
        .when()
                .post("/contas")//Método post para envio
        .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;

    }

    @Test
    public void deveInserirMovimentacaoSucesso(){

        Movimentacao mov = getMovimentacaoValida();

        given()
                .header("Authorization", "JWT " + TOKEN)
                .body(mov)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(201)

        ;

    }

    @Test
    public void deveValidarCamposObrigatoriosNaMovimentacao(){

        given()
                .header("Authorization", "JWT " + TOKEN)
                .body("{}") //objeto vazio para a validação
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", hasSize(8)) //verificando a quantidade de mensagens que retorna.
                .body("msg", hasItems("Data da Movimentação é obrigatório", "Data do pagamento é obrigatório","Descrição é obrigatório","Interessado é obrigatório", "Valor é obrigatório", "Valor deve ser um número","Conta é obrigatório", "Situação é obrigatório"))
        ;
    }

    @Test
    public void naoDeveCadastrarMovimentacaoFutura(){
        Movimentacao mov = getMovimentacaoValida();
        mov.setData_transacao("20/05/2090");

        given()
                .header("Authorization", "JWT " + TOKEN)
                .body(mov)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", hasSize(1)) //verificando a quantidade de mensagens que retorna.
                .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
        ;
    }



}

