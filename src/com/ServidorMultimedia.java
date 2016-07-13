/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.Message;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServidorMultimedia implements Runnable {
    static int puerto = 7001; // EL RANGO DEBE IR DEL 7001 AL 7010
    static ServerSocket server; //el servidor
    static Socket socket = null; //el socket que servira de comunicador   
    static File directorio;
    static String path = "C:/videos/";


    @Override
    public void run() {
          try {
            System.out.println("Encendiendo servidor multimedia");
             server = new ServerSocket(puerto);
            while(true)
            {
                System.out.println("El server acepta conexiones");
                socket = server.accept();
                directorio = new File(path);
                System.out.println("Nueva conexión entrante: "+socket);
                Runnable nuevaConexion = new ClienteConectadoMultimedia(socket);
                Thread hilo = new Thread(nuevaConexion);
                hilo.start();
            }
      
        } catch (IOException ee) {
            System.out.print("ERROR, no se pudo iniciar el servidor multimedia");
        }
        
        
    }
    
}
