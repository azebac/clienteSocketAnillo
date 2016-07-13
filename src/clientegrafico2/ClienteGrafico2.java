/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientegrafico2;

import com.ServidorMultimedia;
import com.clienteConectado;
import com.servidorCliente;
import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;

/**
 *
 * @author Jose
 */
public class ClienteGrafico2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        mainWindow ventana = new mainWindow();
        ventana.setVisible(true);
        Runnable servidor = new servidorCliente();
        Thread hilo = new Thread(servidor);
        hilo.start();
        Runnable servidorMedia = new ServidorMultimedia();
        Thread multimedia = new Thread(servidorMedia);
        multimedia.start();

        try
        {

            System.out.println(Inet4Address.getLocalHost().getHostAddress());
        }
        catch(Exception e)
        {
            
        }
    }
    
}
