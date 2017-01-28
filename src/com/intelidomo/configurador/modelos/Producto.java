package com.intelidomo.configurador.modelos;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import net.vjdv.cvlmaker.ListenerView;
import net.vjdv.cvlmaker.modelos.CVL;
import net.vjdv.cvlmaker.modelos.Choice;

/**
 *
 * @author Jassiel
 */
public class Producto implements ListenerView{
  //Variables
//  @XmlTransient
  public CVL cvl;
  @XmlAttribute
  public String nombre;
  @XmlAttribute
  public String paquete = "", clasePrincipal = "";
  @XmlElement(name="Caracteristica")
  public List<String> caracteristicas = new ArrayList<>();
  /**
   * Agrega una nueva característica a la lista
   * @param feature Cadena representando característica
   * @return Devuelve true si la cadena no existía
   */
  public boolean agregarCaracteristica(String feature){
    if(caracteristicas.contains(feature)) return false;
    caracteristicas.add(feature);
//    System.out.println(nombre+": "+feature);
    return true;
  }
  @Override
  public void updatedModel() {
    caracteristicas.clear();
    agregarCaracteristica(cvl.obtenerNombre());
    leerHijos(cvl);
  }
  private void leerHijos(Choice padre_choice){
    for(Choice hijo : padre_choice.obtenerHijos()){
      agregarCaracteristica(hijo.obtenerNombre());
      leerHijos(hijo);
    }
  }
  @Override
  public String toString(){
    return nombre;
  }
}