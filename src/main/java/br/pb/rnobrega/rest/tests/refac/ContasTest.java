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

public class ContasTest extends BaseTest {

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

    @Test
    public void deveIncluirContaComSucesso(){

        given()
                .body("{\"nome\": \"Conta inserida\"}") //O valor não precisa mais ser dinâmico
        .when()
                .post("/contas")
        .then()
                .statusCode(201)
        ;


    }

    @Test
    public void deveAlterarContaComSucesso(){

        Integer CONTA_ID = getIdContaPeloNome("Conta para alterar");

        given()
                .body("{\"nome\": \"Conta alterada\"}")
                .pathParam("id", CONTA_ID)
        .when()
                .put("/contas/{id}")
        .then()
                .statusCode(200)
                .body("nome", is("Conta alterada"))
        ;
    }

    @Test
    public void naoDeveIncluirContaComNomeRepetido(){

        given()
                .body("{\"nome\": \"Conta mesmo nome\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;

    }


}
