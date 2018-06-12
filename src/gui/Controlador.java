/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.applet.AudioClip;

/**
 *
 * @author Cristy
 */
public class Controlador extends Thread{
    
    String nombre = null;
    Socket cliente = null;
    Controlador[] threads;
    BufferedReader in = null;
    PrintStream out = null;
    int numClientes; 
    boolean run;
    boolean hayNombre;
    String comando;
    AudioClip sonido = java.applet.Applet.newAudioClip(getClass().getResource("../extras/tono.wav"));
    
    ChatCliente chatCliente;
    ServidorGUI serviGUI;
    
    public Controlador(Socket s, Controlador[] ts, ServidorGUI sg){
        this.numClientes = ts.length;
        this.cliente=s;
        this.threads=ts;
        this.chatCliente = new ChatCliente();
        this.chatCliente.setControlador(this);
        this.run = false;
        this.comando = "";
        this.hayNombre = false;
        this.serviGUI = sg;
    }
    
    public Controlador(){}
    
    public void setNombre(String nombre){
        this.nombre = nombre;
        this.chatCliente.setConectados(new ArrayList<String>());
        this.serviGUI.setConectados(new ArrayList<String>());
        this.hayNombre = true;
    }
    
    public void setBul(boolean run){
        this.run = run;
    }
    
    @Override
    public void run(){
        int c = this.numClientes;
        Controlador[] threads = this.threads;
        try{
            out = new PrintStream(cliente.getOutputStream());
            in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));      
            
            IniciarCliente iC = new IniciarCliente();
            iC.setVisible(true);
            iC.setControlador(this);
            iC.setVentanaCliente(chatCliente);
            
            while(!hayNombre){
                try{
                    Thread.sleep(100);
                }catch(InterruptedException ie){
                    System.out.println(ie.getMessage());
                }
            }
            
            chatCliente.setTitle("Chat: " + this.nombre);
            chatCliente.nombreUsuario.setText(this.nombre);

            serviGUI.cambiarPuertoEIP(""+cliente.getInetAddress(), cliente.getLocalPort());
            
            ArrayList<String> conectados = new ArrayList<>();
            ArrayList<String> conectadosTotal = new ArrayList<>();
            
            for(int i=0; i < c;i++){
                if(threads[i] != null && threads[i]!= this){
                    conectados.add(threads[i].nombre);                 
                    conectadosTotal.add(threads[i].nombre);                 
                    threads[i].chatCliente.addUsuario(this.nombre);
                    threads[i].chatCliente.escribirEnArea("¡¡ "+this.nombre+" inició sesión !!\n");                             
                }
                if(threads[i] == this){
                    threads[i].chatCliente.escribirEnArea("¡¡ Bienvenido, "+this.nombre+" !!\n");   
                    conectadosTotal.add(nombre);
                }  
            }
            
            serviGUI.addUsuario(this.nombre);
            serviGUI.escribirEnArea("¡¡ "+this.nombre+" inició sesión !!\n");  
            serviGUI.setConectados(conectadosTotal);
            
            chatCliente.setConectados(conectados); 
            
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }   
    
    public void enviarComando(String comando) {
        this.comando = comando;
        this.run = true;
        int c = this.numClientes;
        String leer = comando;
        
        if(leer.contains("Privado#")){
            leer = leer.replace("Privado#","");
            String nombreContacto = "";
            int posContacto = 0;
            
            while(leer.charAt(posContacto)!='#'){
                nombreContacto += leer.charAt(posContacto);
                posContacto++;
            }
            
            posContacto++;
            leer = leer.replace(nombreContacto+"#","");
            Controlador contacto = null;
            
            for(int i=0; i < this.numClientes;i++){
                if(threads[i] != null && threads[i]!= this && threads[i].nombre.equals(nombreContacto)){
                    out.println(i +  ". " + threads[i].nombre );
                    contacto = threads[i];
                }
            }           
            
            this.chatCliente.escribirEnArea(">> "+this.nombre+": "+leer+" [ MD a "+nombreContacto+" ]");
            this.serviGUI.escribirEnArea(">> "+this.nombre+": "+leer+" [ MD a "+nombreContacto+" ]");
            sonido.play(); 

            if(contacto!=null){
                contacto.chatCliente.escribirEnArea(">> "+this.nombre+": "+leer+" [ MD ]");
                this.serviGUI.escribirEnArea(">> "+this.nombre+": "+leer+" [ MD ]");
                sonido.play();          
            }
        
        }else if(leer.contains("Publico#")){
            
            leer = leer.replace("Publico#","");
            serviGUI.escribirEnArea(">> " + this.nombre + ": " +leer);
            
            for(int i=0;i<this.numClientes;i++){
                 if(threads[i]!=null){
                     threads[i].chatCliente.escribirEnArea(">> " + this.nombre + ": " +leer);  
                     sonido.play();
                 }
            }
             
        }else if(leer.equals("SALIDA")){
            serviGUI.quitarConectado(this.nombre);
            serviGUI.escribirEnArea( "¡¡ " +this.nombre+ " terminó sesión !!\n");
            
            for(int i=0;i<this.numClientes;i++){
                 if(threads[i]!=null && threads[i]!=this){
                     threads[i].chatCliente.escribirEnArea("¡¡ " +this.nombre+ " terminó sesión !!\n");  
                     threads[i].chatCliente.quitarConectado(this.nombre);                        
                 }
            }
            this.chatCliente.dispose();
        }
    }
}
