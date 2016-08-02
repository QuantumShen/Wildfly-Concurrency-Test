package com.sensorhound.client;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;


class TestThread extends Thread{
    private WebTarget target;
    private int testCount;
    private int id;
    private Client client;
    private static int idCount = 0;


    TestThread(WebTarget target, Client client, int testCount){
        this.target = target;
        this.testCount = testCount;
        this.id = ++idCount;
        this.client = client;
    }

    public void run(){
    	
    	CalculatorClient.activeThreads++;
        
        for(int i=0; i<testCount; i++){
            long startTime = System.currentTimeMillis();
            long ms = -1;
            
            Future<Response> future = target.request("text/plain").async().get();
            Response response = null;
            String result = "";
            try {
                response = future.get(20, TimeUnit.SECONDS);     //will suspend current thread
                result = response.readEntity(String.class);
                
                long endTime = System.currentTimeMillis();
                ms = endTime - startTime;
                
                if(response != null && response.getStatus() == 200){
                    CalculatorClient.avgTime = CalculatorClient.avgTime * CalculatorClient.successCount/(CalculatorClient.successCount+1) + ms;
                    CalculatorClient.successCount++;
                }
                
                
            } catch (TimeoutException timeout) {
                result = "Timeout";
                break;
            } catch (InterruptedException ie) {
                result = "Interrupted";
                break;
            } catch (ExecutionException ee) {
                result = ee.getCause().toString();
                break;
            } finally {

                if(response == null || response.getStatus() != 200){  
                    CalculatorClient.errorCount++;
                }
                CalculatorClient.errorRatio = ((double)CalculatorClient.errorCount) /(CalculatorClient.errorCount + CalculatorClient.successCount);
                String ret ="";
                String csv = "";
                double qps = ((double)CalculatorClient.successCount)/(System.currentTimeMillis()-CalculatorClient.testStartTime) *1000;
                if(response != null){
                    
                    ret = "Thread " + id 
                            + " , Test " + i 
                            + " , Status: " + response.getStatus();
                	
                	
                	
                    csv =   System.currentTimeMillis()-CalculatorClient.testStartTime
                            + " , " + id 
                            + " , " + i 
                            + " , " + response.getStatus() 
                            + " , " + result 
                            + " , " + ms
                            + " , " + qps
                            + " , " + CalculatorClient.errorRatio*100 + "%"
                            + " , " + CalculatorClient.activeThreads;
                    
                    response.close();
                }else{
                    ret = "Thread " + id 
                            + " , Test " + i 
                            + " , Status: " + "No Response" ;

                	
                    csv =   System.currentTimeMillis()-CalculatorClient.testStartTime
                            + " , " + id 
                            + " , " + i 
                            + " , " + "No Response" 
                            + " , " + result 
                            + " , " + ms
                            + " , " + qps
                            + " , " + CalculatorClient.errorRatio*100 + "%"
                            + " , " + CalculatorClient.activeThreads;
                }
                System.out.println(ret);
                try {
                    CalculatorClient.writer.write(csv+'\n');
                    
                } catch (IOException e) {
                    System.out.println("Writing log.csv failed");
                    break;
                }
                
                
                
            }
        }
        CalculatorClient.activeThreads--;
        this.client.close();
        try {
			CalculatorClient.writer.flush();
		} catch (IOException e) {
            System.out.println("Writing log.csv failed");
            
		}

    }
}


public class CalculatorClient {

    static String[] opSymbols = {"+", "-", "*", "/"};
    static String[] opNames = {"add", "substract", "multiply", "divide"};
    static ArrayList<String> opSymbolsList = new ArrayList<>(Arrays.asList(opSymbols));
    static Random rnd = new Random();
    static long testStartTime;
    static double avgTime; //ms
    static int successCount = 0;
    static int errorCount = 0;
    static double errorRatio = 0;
    static BufferedWriter writer = null;
    static int activeThreads = 0;
    
    
    static String getOpName(String opSymbol, int i){
        if(opSymbol.equals("all")){
            return opNames[i%4];
        }else{
            if(opSymbolsList.contains(opSymbol)){
                return opNames[opSymbolsList.indexOf(opSymbol)];
            }else{
                throw new InputMismatchException("wrong operator, only +,-,*,/ and 'all' are accepted");
            }
        }
        
    }
    
    static String getOp(String op){
        if(op.equals("")){
            String s =  String.valueOf(rnd.nextInt());  
//          System.out.println(s);
            return s;
        }else{
            return op;
        }
        
    }
    
    /* 
     * params:
     *   number of user: an integer
     *   requests per user: an interger
     *   operator: + - * / all
     *   op1: either integer or float point number
     *   op2: either integer or float point number
     */
    public static void main(String[] args) {
        int numUser, requestsPerUser;
        String opSymbol, op1, op2;
        
        
        //Check parameters
        try {
            if(args != null && args.length > 1){
                
                
                numUser = Integer.valueOf(args[0]);
                requestsPerUser = Integer.valueOf(args[1]);
                
                if(args.length > 2){
                    opSymbol = args[2];
                    if(args.length > 4){
                        op1 = args[3];
                        op2 = args[4];
                    }else{
                        op1 = "";
                        op2 = "";
                    }
                }else{
                    opSymbol = "all";
                    op1 = "";
                    op2 = "";
                }
                
            }else{
                numUser = 10;
                requestsPerUser = 10;
                opSymbol = "all";
                op1 = "";
                op2 = "";
            }
        } catch (Exception e) {
            System.out.println("Invalid parameters");
            return;
        }
        
        //open a file
        try {           
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("log"+numUser+"x"+requestsPerUser+".csv"), "utf-8"));
            writer.write("Execute Time_ms, Thread ID, Request ID, Status Code, Response Text, Response Delay_ms, Success QPS, Error Ratio, Active Threads \n");
            
            
        } catch (Exception e) {
            System.out.println("can not open log.csv");
            return;
        }
        

        
        
        
        //Prepare threads
        List<TestThread> threads = new ArrayList<>();
            
        for (int i=0; i< numUser; i++){
            Client client = ClientBuilder.newClient();

            try {
                WebTarget target = client.target("http://52.42.56.228:8080")
                        .path(getOpName(opSymbol, i)).queryParam("operand1", getOp(op1)).queryParam("operand2", getOp(op2));
                threads.add(new TestThread(target, client, requestsPerUser));

            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
                return;
            }
        }
        
        testStartTime = System.currentTimeMillis();
        //Start threads
        for (TestThread t : threads){
            t.start();
        }

    }

}
