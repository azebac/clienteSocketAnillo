/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import clientegrafico2.mainWindow;
import com.Cliente;
import com.Message;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 */
public class clienteConectado implements Runnable {
    static Socket socket = null; //el socket que servira de comunicador 
         ObjectInputStream entrada;
        ObjectOutputStream salida;
    static String direccion = System.getProperty("user.dir");
    static Message peticion;  //El mensaje que recibira del servidor y/o cliente
    public clienteConectado(Socket client) 
    {
        this.socket = client;
        
    }

    
    @Override
    public void run() {

        Message peticion;
        
        while (true) {
            try {

                socket.setSoLinger(true, 10);
                
                entrada = new ObjectInputStream(socket.getInputStream());
                salida = new ObjectOutputStream(socket.getOutputStream());
                while (true)
                {
                Object objPeticion = entrada.readObject();
                peticion = (Message) objPeticion;
                System.out.println(peticion.Mensaje);
                if (!peticion.Mensaje.equals("conexion"))
                {
                    //LOGICA DE MULTIMEDIA Y BUSQUEDA DE ARCHIVO Y REDIRECCION
                    String[] comando;
                    comando = peticion.Mensaje.split(":");
                    if (comando[0].equals("redireccion"))
                    {
                        mainWindow.cambiarServidor(comando[1]);
                    }
                    if (comando[0].equals("busqueda"))
                    {
                        //primero pregunto si el mensaje lo mande yo
                        if(comando[1].equals(Inet4Address.getLocalHost().getHostAddress()))
                        {
                           
                            //si lo mande yo y alguien me dijo que lo tiene
                            if(comando.length==4)
                            {
                                tablaDeRecursos.insertarRecurso(comando[3], comando[2]);
                                //aqui va la logica de conexion y solicitud del archivo
                                Message archivo = new Message();
                                //creo un mensaje de solicitud del path relativo de ese archivo
                                archivo.Mensaje = "\\src\\archivos\\"+comando[2];
                                System.out.println("Se solicitará el archivo: "+ archivo.Mensaje);
                                //creo una conexion al que respondio que tenia el archivo
                                Socket socketArchivo = new Socket(comando[3], 7001);
                                //creo los lectores y escritores del socket
                                ObjectOutputStream salidaArchivo = new ObjectOutputStream(socketArchivo.getOutputStream());
                                ObjectInputStream entradaArchivo = new ObjectInputStream(socketArchivo.getInputStream());
                                try {
                                    //le envio la solicitud al servidor multimedia del que respondio
                                    salidaArchivo.writeObject(archivo);
                                    //espero su respuesta
                                    Object respuesta = entradaArchivo.readObject();
                                    //si la respuesta es un mensaje cualquiera quiere decir que se daño la transferencia
                                    if (respuesta instanceof Message) {
                                        Message alerta = (Message) respuesta;
                                        System.out.println(alerta.Mensaje);
                                    //Si la respuesta es una instancia de archivo entonces llamo a mi funcion de recibir archivo
                                    } else if(respuesta instanceof Archivo){
                                        Archivo archivoRecibido = (Archivo)respuesta;
                                        recibeArchivo(archivoRecibido, entradaArchivo);
                                    }
                                    //entrada.close();
                                } catch (IOException e) {
                                   System.out.println("Ocurrio un error al tratar de leer los archivos");
                                   System.out.println(e.toString());
                                   } catch (ClassNotFoundException ex) {
                                        System.out.println("No se encontro la clase a castear");
                                    }
                            }
                            //Si el mensaje lo envie yo mas sin embargo nadie lo tenia muestro un error.
                            else
                            {
                                System.out.println("Archivo no encontrado");
                                mainWindow.mostrarError();
                            }
                        }
                        //Si el mensaje no es para mi
                        else
                        {
                            //Si el mensaje no es para mi pero contiene un recurso, conservo el nombre de ese recurso en mi tabla
                            if (comando.length==4){
                                tablaDeRecursos.insertarRecurso(comando[4], comando[2]);
                            }
                            //Busco en mi carpeta si lo tengo
                            if(buscarEnCarpeta(comando[2]))
                            {
                                //Si tengo el archivo reenvio el mensaje pero adjuntando mi ip
                                peticion.Mensaje = "busqueda:"+comando[1]+":"+comando[2]+":"+Inet4Address.getLocalHost().getHostAddress();
                                mainWindow.enviarMensaje(peticion);
                            }
                            else
                            {
                                //Si no tengo el archivo simplemente reenvio el mensaje sin adjuntar nada
                                mainWindow.enviarMensaje(peticion);
                            }
                        }
                    }
                }
                else //osea, conexion
                {
                   System.out.println("Cliente conectado: "+socket.getInetAddress().getHostAddress());
                }
                //cerramos conexiones    
                }
                
            } catch (IOException e) {
                try {
                    socket.close();
                    break;
                } catch (IOException ex) {
                    Logger.getLogger(clienteConectado.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (ClassNotFoundException ex) {
                System.out.println(ex.toString());
            }
        }
       
    }
    
    public static Boolean buscarEnCarpeta(String nombre)
        {
            //Defino el directorio de archivos
            File dir = new File(direccion+"\\src\\archivos");
            //listo el contenido de ese directorio
            String[] ficheros = dir.list();
            //Si no esta vacio
            if (ficheros!=null)
            {
                //recorro nombre por nombre
                for (int x=0; x<ficheros.length;x++)
                {
                    //si encuentro uno que coincida con la busqueda retorno true
                    if (ficheros[x].equals(nombre))
                    {
                        return true;
                    }
                    
                }
            }
            return false;
        }
    
    private static void recibeArchivo(Archivo archivo, ObjectInputStream entrada) {
        try {
            //Abro el escritor de archivos
            FileOutputStream fos = new FileOutputStream(direccion+"\\src\\archivos\\" + archivo.nombreArchivo);
            //defino un auxiliar por seguridad
            Object mensajeAux=archivo;
            Archivo mensajeRecibido=null; 
            boolean primero=true; 
            do
            {
                //voy leyendo el archivo poco a poco 
               if(!primero)
                   mensajeAux = entrada.readObject();
               //y escribo el archivo 
               if(mensajeAux instanceof Archivo)
               {
                   mensajeRecibido = (Archivo)mensajeAux; 
                   /*System.out.print(new String(
                            mensajeRecibido.contenidoArchivo, 0,
                            mensajeRecibido.bytesValidos));*/
                    fos.write(mensajeRecibido.contenidoArchivo, 0,
                            mensajeRecibido.bytesValidos);
               }
               primero = false;
            }while(!mensajeRecibido.ultimoMsj);
            fos.close();     
            System.out.println("Se termino de pasar el archivo");
        } catch (FileNotFoundException ex) {
            System.out.println("No existe la ruta donde copiarlo");
            System.out.println(ex.toString());
        } catch (IOException ex) {
            System.out.println("Error al copiar el archivo");
        } catch (ClassNotFoundException ex) {
            System.out.println("Error al copiar el archivo");
        }
        
    }
    

}
