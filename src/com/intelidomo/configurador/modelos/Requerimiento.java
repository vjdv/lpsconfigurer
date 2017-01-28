package com.intelidomo.configurador.modelos;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Abstracción de un requerimientos de software
 * @author Jassiel
 */
public class Requerimiento implements Condicionable{
  private final SimpleStringProperty id = new SimpleStringProperty();
  private final SimpleStringProperty condicion = new SimpleStringProperty();
  private final SimpleStringProperty texto = new SimpleStringProperty();
  @XmlAttribute
  public String getId() {
    return id.get();
  }
  public void setId(String id) {
    this.id.set(id);
  }
  public StringProperty idProperty(){
    return id;
  }
  @Override
  @XmlAttribute
  public String getCondicion() {
    return condicion.get();
  }
  public void setCondicion(String cond) {
    condicion.set(cond);
  }
  public StringProperty condicionProperty(){
    return condicion;
  }
  @XmlAttribute(name="descripcion")
  public String getTexto() {
    return texto.get();
  }
  public void setTexto(String txt) {
    texto.set(txt);
  }
  public StringProperty textoProperty(){
    return texto;
  }
  @Override
  public String toString(){
    return id.get();
  }
}