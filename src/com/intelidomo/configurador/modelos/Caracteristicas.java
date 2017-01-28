package com.intelidomo.configurador.modelos;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Jassiel
 */
public class Caracteristicas {
  @XmlAttribute
  public String cvl = "arbol.cvl";
  @XmlElement(name="Caracteristica")
  public List<String> lista_caracteristicas = new ArrayList<>();
  /**
   * Agrega una nueva característica a la lista
   * @param feature Cadena representando característica
   * @return Devuelve true si la cadena no existía
   */
  public boolean agregarCaracteristica(String feature){
    if(lista_caracteristicas.contains(feature)) return false;
    lista_caracteristicas.add(feature);
    return true;
  }
  /**
   * Limpia la lista de características
   */
  public void clear(){
    lista_caracteristicas.clear();
  }
}