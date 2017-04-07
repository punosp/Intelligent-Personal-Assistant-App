package everytasc.nineleaps.com.hackaton.DataModel;

/**
 * Created by BURNI on 27/5/16.
 */
public class Option {

    int code;
    String text;
    String helpUrl = "";
    String link="";
    String allData;

    public boolean helpButton=false;

    public Option(String allData){
        this.allData = allData;
        String[] al = allData.split(":");
        code = Integer.parseInt(al[0]);
        text =al[1];

        try{
            link = al[2];
            if(link.equals("h")){
                helpButton=true;
            }
        }catch (Exception e){

        };
    }

    public String getAllData(){
        return allData;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public String getHelpUrl() {
        return helpUrl;
    }
}
