/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;

import gui.ChatCliente;
import java.io.*;
import java.net.*;
import gui.Controlador;
import gui.ServidorGUI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Cristy
 */
public class Servidor {
    
    public ServerSocket server;
    public Socket cliente;
    public Controlador threads[];
    public int numClientes, i, puerto;
    public static ServidorGUI sg;
    public Servidor(){
        this.server = null;
        this.cliente = null;
        this.numClientes = 15;
        this.i=0;
        this.puerto = 4444;
        this.threads = new Controlador[numClientes];
        sg = null;
    }
    
    public static void main(String ar[]){
       Servidor s = new Servidor();
       s.iniciarSocket();
       sg = new ServidorGUI();
       sg.setVisible(true);
       JOptionPane.showMessageDialog(sg,"¡¡ Servidor incializado correctamente !!");
       s.correrServidor();
      
    }
   
    public void iniciarSocket(){
        try{
            this.server= new ServerSocket(this.puerto);
            System.out.println("Servidor inicializado");
            //String msj = (char)27 + "[35m¡¡ Hola";
            //System.out.println(msj);
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    
    public  void correrServidor(){
        while(true){
            try{
                this.cliente = this.server.accept();

                for(i=0; i<numClientes;i++){
                    if(threads[i] == null){
                        (threads[i] = new Controlador(cliente,threads, sg)).start();
                        break;
                    }
                }
                
                if(i==numClientes){
                    PrintStream os = new PrintStream(cliente.getOutputStream());
                    os.println("Servidor lleno. Intenta más tarde");
                    os.close();
                    ChatCliente cc = new ChatCliente();
                    JOptionPane.showMessageDialog(cc,"¡¡ Servidor incializado correctamente !!");
                    this.cliente.close();
                }
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        }
    }
    
    public void desconectarServidor(){
        try {
            this.cliente.close();
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
