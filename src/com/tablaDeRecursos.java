/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import java.util.ArrayList;

/**
 *
 * @author Jose
 */
public class tablaDeRecursos {
    
    //trabajar con un array bidimensional habria sido muy tedioso, asi que uso posiciones equivalentes
    private static ArrayList<String> nombreDeRecurso = new ArrayList<String>();
    private static ArrayList<String> ipDeRecurso = new ArrayList<String>();

    
    public static void insertarRecurso(String ip, String nombre){
          tablaDeRecursos.ipDeRecurso.add(ip);
          tablaDeRecursos.nombreDeRecurso.add(nombre);
    }
    
    public static String buscarRecurso(String nombre) {
        
        for (int i = 0;i<tablaDeRecursos.nombreDeRecurso.size();i++)
        {
            if (tablaDeRecursos.nombreDeRecurso.get(i).equals(nombre))
            {
                return (tablaDeRecursos.ipDeRecurso.get(i));
            }
        }
        return "fallo";
    }
    
    public static void eliminarRecurso(String nombre){
        int pos = tablaDeRecursos.nombreDeRecurso.indexOf(nombre);
        tablaDeRecursos.nombreDeRecurso.remove(pos);
        tablaDeRecursos.ipDeRecurso.remove(pos);
    }
    
}
