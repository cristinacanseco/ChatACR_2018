package sockets;


import gui.ServidorGUI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Cristy
 */
public class Cliente implements Runnable{
    
    public Socket cliente=null;
    public boolean run=false;
    public BufferedReader in= null;
    public PrintStream out = null;
    public String ip;
    public int puerto;
    
    public Cliente(String ip){
        this.cliente = null;
        this.run = false;
        this.in = null;
        //this.read = null;
        this.out = null;
        this.ip = ip;
        this.puerto = 4444;
    }  
    
    public Cliente(){
        this.cliente = null;
        this.run = false;
        this.in = null;
        //this.read = null;
        this.out = null;
        this.ip = "192.168.1.64";
        this.puerto = 4444;
    }        
           
    
    public static void main(String arg[]){            
        Cliente c = new Cliente();
        c.iniciaSocket();
    }

    public void iniciaSocket(){
        try{
            cliente = new Socket(this.ip, this.puerto);
            //read = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintStream(cliente.getOutputStream());
            in= new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            System.out.println("!! Bienvenido !!");
            //while(!run){}
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    
    @Override
    public void run() {
        String msj;
        try{  
            while((msj = in.readLine())!= null){
                System.out.println(msj);
                if(msj.equals("LISTA")){
                    continue;
                }
                
                if(msj.equals("ADIOS")){
                    in.close();
                    out.close();
                    cliente.close();
                    break;
                }
            }
            run=true;    
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
   
    }


        
}
    

