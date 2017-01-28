package com.intelidomo.configurador.modelos;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jassiel
 */
@XmlRootElement(name="lps")
public class Proyecto {
  @XmlAttribute
  public String nombre;
  @XmlElement(name="Informacion")
  public Informacion info = new Informacion();
  @XmlElement(name="Caracteristicas")
  public Caracteristicas caracteristicas = new Caracteristicas();
//  @XmlElement(name="Requerimientos")
//  public Requerimientos requerimientos = new Requerimientos();
  @XmlElementWrapper(name="Requerimientos")
  @XmlElement(name="Requerimiento")
  public List<Requerimiento> requerimientos = new ArrayList<>();
  @XmlElementWrapper(name="Disenio")
  @XmlElement(name="Diagrama")
  public List<Diagrama> diagramas = new ArrayList<>();
  @XmlElementWrapper(name="Pruebas")
  @XmlElement(name="Prueba")
  public List<Prueba> pruebas = new ArrayList<>();
  @XmlElementWrapper(name="Productos")
  @XmlElement(name="Producto")
  public List<Producto> productos = new ArrayList<>();
  //Métodos helper
  public Diagrama getDiagramaByID(String id){
    for(Diagrama d : diagramas) if(d.getId().equals(id)) return d;
    return null;
  }
}