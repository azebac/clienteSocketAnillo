/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import clientegrafico2.mainWindow;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jose
 */

public class servidorCliente implements Runnable {
    static int puerto = 7000; //puerto de comunicacion del servidor
    static ServerSocket server; //el servidor 
    Socket socket=null; 

    @Override
    public void run() {
        try {
            server = new ServerSocket(puerto, 3);
        } catch (IOException ex) { //CATCH DEL SERVIDOR
            Logger.getLogger(servidorCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true)
        {
            try {
               
                socket = server.accept();
                Runnable nuevaConexion = new clienteConectado(socket);
                Thread hilo = new Thread(nuevaConexion);
                hilo.start();
                System.out.println("Nueva conexión entrante: "+socket);
                mainWindow.antecesor = socket.getInetAddress().getHostAddress();
            } catch (IOException ex) { //catch de la conexion con el cliente
                Logger.getLogger(servidorCliente.class.getName()).log(Level.SEVERE, null, ex);
                organizadorClientes.restarConectado();
            }
        }
    }
}
