package br.pb.rnobrega.rest.tests.refac;

import br.pb.rnobrega.rest.core.BaseTest;
import br.pb.rnobrega.rest.tests.Movimentacao;
import br.pb.rnobrega.rest.utils.DataUtils;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.Matchers.*;

public class MovimentacaoTest extends BaseTest {

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
    public Integer getIdContaPeloNome(String nome){
        return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
    }
    public Integer getIdMovPelaDescricao(String desc){
        return RestAssured.get("/transacoes?descricao="+desc).then().extract().path("id[0]");
    }

    private Movimentacao getMovimentacaoValida(){

        Movimentacao mov = new Movimentacao();
        mov.setConta_id(getIdContaPeloNome("Conta para movimentacoes"));
        mov.setDescricao("Descrição da movimentação");
        mov.setEnvolvido("Envolvido na movimentacao");
        mov.setTipo("REC");
        mov.setData_transacao(DataUtils.getDataDiferencaDias(-1));
        mov.setData_pagamento(DataUtils.getDataDiferencaDias(5));
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;

    }

    @Test
    public void deveInserirMovimentacaoSucesso(){

        Movimentacao mov = getMovimentacaoValida();

        given()
                .body(mov)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(201)
                .body("descricao", is("Descrição da movimentação"))
                .body("envolvido", is("Envolvido na movimentacao"))
                .body("tipo", is("REC"))
                .body("status", is(true))
                .body("conta_id", is(getIdContaPeloNome("Conta para movimentacoes")))
                .body("usuario_id", is(22283))
        ;

    }

    @Test
    public void deveValidarCamposObrigatoriosNaMovimentacao(){

        given()
                .body("{}")
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", hasSize(8))
                .body("msg", hasItems("Data da Movimentação é obrigatório", "Data do pagamento é obrigatório","Descrição é obrigatório","Interessado é obrigatório", "Valor é obrigatório", "Valor deve ser um número","Conta é obrigatório", "Situação é obrigatório"))
        ;
    }

    @Test
    public void naoDeveCadastrarMovimentacaoFutura(){
        Movimentacao mov = getMovimentacaoValida();
        mov.setData_transacao(DataUtils.getDataDiferencaDias(2));

        given()
                .body(mov)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", hasSize(1))
                .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
        ;
    }

    @Test
    public void naoDeveRemoverContaComMovimentacao(){

        given()
                .pathParam("id", getIdContaPeloNome("Conta com movimentacao"))
        .when()
                .delete("/contas/{id}")
        .then()
                .statusCode(500)
                .body("constraint", is("transacoes_conta_id_foreign"))

        ;
    }

    @Test
    public void deveRemoverMovimentacao(){

        //metódo criado acima para funcionamento desse teste.
        Integer MOV_ID = getIdMovPelaDescricao("Movimentacao para exclusao");

        given()
                .pathParam("id", MOV_ID)
        .when()
                .delete("/transacoes/{id}")//ID da movimentação a ser cancelada que pegamos na URL
        .then()
                .statusCode(204)
        ;
    }

    


}
