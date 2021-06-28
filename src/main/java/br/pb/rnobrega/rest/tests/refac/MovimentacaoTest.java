package br.pb.rnobrega.rest.tests.refac;

import br.pb.rnobrega.rest.core.BaseTest;
import br.pb.rnobrega.rest.tests.Movimentacao;
import br.pb.rnobrega.rest.utils.BarrigaUtils;
import br.pb.rnobrega.rest.utils.DataUtils;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MovimentacaoTest extends BaseTest {

    private Movimentacao getMovimentacaoValida(){

        Movimentacao mov = new Movimentacao();
        mov.setConta_id(BarrigaUtils.getIdContaPeloNome("Conta para movimentacoes"));
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
                .body("conta_id", is(BarrigaUtils.getIdContaPeloNome("Conta para movimentacoes")))
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
                .pathParam("id", BarrigaUtils.getIdContaPeloNome("Conta com movimentacao"))
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
        Integer MOV_ID = BarrigaUtils.getIdMovPelaDescricao("Movimentacao para exclusao");

        given()
                .pathParam("id", MOV_ID)
        .when()
                .delete("/transacoes/{id}")//ID da movimentação a ser cancelada que pegamos na URL
        .then()
                .statusCode(204)
        ;
    }

    


}
