/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import clientegrafico2.mainWindow;
import javax.swing.JTextField;

/**
 *
 * @author Jose
 */
public class controladorMainWindow {
    public static JTextField descargados;
    public static int contador = 0;
    
    public static void initOutlet(JTextField descargados){
        controladorMainWindow.descargados = descargados;
    }    
    public static void sumarContador(){
        controladorMainWindow.contador++;
        controladorMainWindow.descargados.setText(String.valueOf(controladorMainWindow.contador));
    }
}
