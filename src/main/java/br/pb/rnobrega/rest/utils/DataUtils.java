package br.pb.rnobrega.rest.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//Classe criada para gerarmos a data atual formatada.
public class DataUtils {

    public static String getDataDiferencaDias(Integer qtdDias){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, qtdDias);
        return getDataFormatada(cal.getTime());
    }
    public static String getDataFormatada(Date data){
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(data);

    }

}