package br.pb.rnobrega.rest.utils;

import io.restassured.RestAssured;

public class BarrigaUtils {
    //Como o reset reseta todo o banco, incluindo os ID's se tornam diferentes, é necessário esse método que trás pra gente o ID da conta a partir do nome.
    public  static Integer getIdContaPeloNome(String nome){
        return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
    }
    public static Integer getIdMovPelaDescricao(String desc){
        return RestAssured.get("/transacoes?descricao="+desc).then().extract().path("id[0]");
    }
}
