package com.sensorhound.calculator;

import javax.ws.rs.GET; 
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;


@Path("/")
public class CalculatorREST {
    
//  @GET
//  @Produces("text/html")
//  public View getPage(){
//      return new View("index.html");
//  }
//  
    
    private boolean notLong(String a){
        return (a.indexOf('.')>-1 || a.indexOf('e')>-1 || a.indexOf('E')>-1);
    }
    
    @GET
    @Path("add")
    @Produces("text/plain")
    public String calcAdd(@QueryParam("operand1") String a, @QueryParam("operand2") String b){
        try {
            if(notLong(a) || notLong(b)){
                Double ret = Double.valueOf(a) + Double.valueOf(b);
                return ret.toString();
            }else{
                Long ret = Long.valueOf(a) + Long.valueOf(b);
                return ret.toString();
            }
            
        } catch (Exception e) {
            //NumberFormatException
            return "error input";
        }
        
    }
    @GET
    @Path("substract")
    @Produces("text/plain")
    public String calcSubstract(@QueryParam("operand1") String a, @QueryParam("operand2") String b){  
        try {
            if(notLong(a) || notLong(b)){
                Double ret = Double.valueOf(a) - Double.valueOf(b);
                return ret.toString();
            }else{
                Long ret = Long.valueOf(a) - Long.valueOf(b);
                return ret.toString();
            }
            
        } catch (Exception e) {
            //NumberFormatException
            return "error input";
        }
        
    }
    @GET
    @Path("multiply")
    @Produces("text/plain")
    public String calcMultiply(@QueryParam("operand1") String a, @QueryParam("operand2") String b){
        try {
            if(notLong(a) || notLong(b)){
                Double ret = Double.valueOf(a) * Double.valueOf(b);
                return ret.toString();
            }else{
                Long ret = Long.valueOf(a) * Long.valueOf(b);
                return ret.toString();
            }
            
        } catch (Exception e) {
            //NumberFormatException
            return "error input";
        }
        
    }

    @GET
    @Path("divide")
    @Produces("text/plain")
    public String calcDivide(@QueryParam("operand1") String a, @QueryParam("operand2") String b){
        try {
            if(notLong(a) || notLong(b)){
                Double ret = Double.valueOf(a) / Double.valueOf(b);
                return ret.toString();
            }else{
                Long ret = Long.valueOf(a) / Long.valueOf(b);
                return ret.toString();
            }
            
        } catch (Exception e) {
            //NumberFormatException
            return "error input";
        }
        
    }
    
}
