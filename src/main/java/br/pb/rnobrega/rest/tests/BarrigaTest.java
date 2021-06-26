package br.pb.rnobrega.rest.tests;

import br.pb.rnobrega.rest.core.BaseTest;
import br.pb.rnobrega.rest.utils.DataUtils;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;



@FixMethodOrder(MethodSorters.NAME_ASCENDING) //Essa anotação garante que os testes serão realizados em ordem alfabética
public class BarrigaTest extends BaseTest {
    //É necessário extender o BaseTest que criamos.



    private static String CONTA_NAME ="Conta " + System.nanoTime(); //Criamos uma variável para o nome pra que ela fique dinâmica. Colocamos o nome padrão "Conta " e somamos a ela ao tempo atual em nanosegundos o que garantirá que sempre será um valor distinto
    private static Integer CONTA_ID;
    private static Integer MOV_ID;

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
    }

    //Para evitar repetição de código e ter que colocar essa parte em todas as de movimentação, foi criado um método pra ele.
    private Movimentacao getMovimentacaoValida(){

        Movimentacao mov = new Movimentacao();
        mov.setConta_id(CONTA_ID);
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
    public void t02_deveIncluirContaComSucesso(){

        CONTA_ID = given()
                .body("{\"nome\": \""+CONTA_NAME+"\"}") //Passa o JSON com as informações que se quer
        .when()
                .post("/contas")//Método post para envio
        .then()
                .statusCode(201)
                .extract().path("id") // extraindo ID para torná-lo dinâmico
        ;

    }

    @Test
    public void t03_deveAlterarContaComSucesso(){

        given()
                .body("{\"nome\": \""+CONTA_NAME+" alterada\"}")
                .pathParam("id", CONTA_ID)
        .when()
                .put("/contas/{id}")//comando do tipo PUT para alteração para /contas/:id que está sendo enviado pelo pathparam, extraido no teste anterior.
        .then()
                .statusCode(200)
                .body("nome", is(CONTA_NAME+ " alterada"))
        ;
    }

    @Test
    public void t04_naoDeveIncluirContaComNomeRepetido(){

        given()
                .body("{\"nome\": \""+CONTA_NAME+" alterada\"}")
        .when()
                .post("/contas")//Método post para envio
        .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;

    }

    @Test
    public void t05_deveInserirMovimentacaoSucesso(){

        Movimentacao mov = getMovimentacaoValida();

        MOV_ID = given()
                .body(mov)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(201)
                .extract().path("id")

        ;

    }

    @Test
    public void t06_deveValidarCamposObrigatoriosNaMovimentacao(){

        given()
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
    public void t07_naoDeveCadastrarMovimentacaoFutura(){
        Movimentacao mov = getMovimentacaoValida();
        mov.setData_transacao(DataUtils.getDataDiferencaDias(2));

        given()
                .body(mov)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", hasSize(1)) //verificando a quantidade de mensagens que retorna.
                .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
        ;
    }

    @Test
    public void t08_naoDeveRemoverContaComMovimentacao(){

        given()
                .pathParam("id", CONTA_ID)
        .when()
                .delete("/contas/{id}")// enviando rota de remoção DELETE com o ID da conta que possui movimentação
        .then()
                .statusCode(500) // O erro que deve dar é relacionado a própria API. Não foi tratado pelo dev, é um problema de integridade.
                .body("constraint", is("transacoes_conta_id_foreign"))

        ;
    }
    @Test
    public void t09_deveCalcularSaldoDasContas(){

        given()
        .when()
                .get("/saldo")
        .then()
                .statusCode(200)
                .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("100.00")) // Com o it nós achamos qual o objeto estamos procurando através do id e pegamos o saldo dele.
                                                                                        // A partir disso nós comparamos com o valor que está em string
        ;
    }

    @Test
    public void t10_deveRemoverMovimentacao(){

        given()
                .pathParam("id", MOV_ID)
        .when()
                .delete("/transacoes/{id}")//ID da movimentação a ser cancelada que pegamos na URL
        .then()
                .statusCode(204)
        ;
    }

    @Test
    public void t11_naoDeveAcessarAPISemToken(){
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

