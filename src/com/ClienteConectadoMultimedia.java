/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import clientegrafico2.mainWindow;
import com.Archivo;
import com.Message;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 *
 * @author Jose
 */
public class ClienteConectadoMultimedia  implements Runnable {

    static Socket socket = null; //el socket que servira de comunicador 
    static Message peticion;  //El mensaje que recibira del servidor y/o cliente
    static String direccion = System.getProperty("user.dir");
   
    
    //instancio el socket del cliente
    public ClienteConectadoMultimedia(Socket client) 
    {
        this.socket = client;
    }

     
    @Override
    public void run() {
        ObjectInputStream entrada;
        ObjectOutputStream salida;
        Message peticion;
        
        while (true) {
            try {

                socket.setSoLinger(true, 10);
                entrada = new ObjectInputStream(socket.getInputStream());
                salida = new ObjectOutputStream(socket.getOutputStream());
                boolean recibe = true;
                do {
                    System.out.println("El Server esta conectado con " + socket.getInetAddress());
                    Object objPeticion = entrada.readObject();
                    if (objPeticion instanceof Message) {
                        peticion = (Message) objPeticion;
                        System.out.println(direccion+peticion.Mensaje);
                        File file = new File(direccion+peticion.Mensaje);
                        if (peticion.Mensaje.equals("liberar")) {
                            recibe = false;
                        } else if (!file.exists()) {
                           //Aqui no deberia entrar nunca.
                        } else if (file.isDirectory()) {
                            //Aqui tampoco deberia etrar nunca.
                            peticion.Mensaje = "Es un directorio el que trata de seleccionar";
                            salida.writeObject(objPeticion);
                        } else if (file.exists()) {
                            System.out.println("Enviando un archivo");
                            enviaArchivo(direccion+peticion.Mensaje, salida, file.getName(), file.length());
                            controladorMainWindow.sumarContador();
                        }
                    } else {
                        System.out.println("Objeto no esperado");
                    }
                } while (recibe);
                //cerramos conexiones                
            } catch (IOException e) {
                try {
                    socket.close();
                    System.out.println(e.toString());
                    break;
                } catch (IOException ex) {
                    Logger.getLogger(ClienteConectadoMultimedia.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (ClassNotFoundException ex) {
                System.out.println(ex.toString());
            }
    }
    }
    
        private void enviaArchivo(String nombreArchivo, ObjectOutputStream oos, String nombre, long tam) {
        String key = nombreArchivo;
        List<Archivo> lista = new ArrayList();

        try {
            boolean enviadoUltimo = false;
            // Se abre el fichero.
            FileInputStream fis = new FileInputStream(nombreArchivo);

            // Se instancia y rellena un mensaje de envio de fichero
            Archivo archivo = new Archivo();
            archivo.nombreArchivo = nombre;

            // Se leen los primeros bytes del fichero en un campo del mensaje
            int leidos = fis.read(archivo.contenidoArchivo);

            // Bucle mientras se vayan leyendo datos del fichero
            while (leidos > -1) {

                // Se rellena el número de bytes leidos
                archivo.bytesValidos = leidos;

                // Si no se han leido el máximo de bytes, es porque el fichero
                // se ha acabado y este es el último mensaje
                if (leidos < Archivo.LONGITUD) {
                    archivo.ultimoMsj = true;
                    enviadoUltimo = true;
                } else {
                    archivo.ultimoMsj = false;
                }

                // Se envía por el socket
                oos.writeObject(archivo);

                // Si es el último mensaje, salimos del bucle.
                if (archivo.ultimoMsj) {
                    break;
                }

                // Se crea un nuevo mensaje
                archivo = new Archivo();
                archivo.nombreArchivo = nombreArchivo;

                // y se leen sus bytes.
                leidos = fis.read(archivo.contenidoArchivo);
            }

            if (enviadoUltimo == false) {
                archivo.ultimoMsj = true;
                archivo.bytesValidos = 0;
                oos.writeObject(archivo);
                lista.add(archivo);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
