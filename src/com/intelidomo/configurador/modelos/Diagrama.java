package com.intelidomo.configurador.modelos;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Jassiel
 */
@XmlType(propOrder={"id","condicion","combinableCon","generadoPor","actualizable"})
public class Diagrama implements Condicionable{
  private final SimpleStringProperty id = new SimpleStringProperty();
  private final SimpleStringProperty nombre = new SimpleStringProperty();
  private final SimpleStringProperty condicion = new SimpleStringProperty();
  private final SimpleStringProperty combinableCon = new SimpleStringProperty();
  private final SimpleStringProperty generadoPor = new SimpleStringProperty();
  private final SimpleStringProperty actualizable = new SimpleStringProperty();
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
  @XmlTransient
  public String getNombre() {
    return nombre.get();
  }
  public void setNombre(String n) {
    this.nombre.set(n);
  }
  public StringProperty nombreProperty(){
    return nombre;
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
  @XmlAttribute
  public String getCombinableCon() {
    return combinableCon.get();
  }
  public void setCombinableCon(String iddb) {
    combinableCon.set(iddb);
  }
  public StringProperty combinableConProperty(){
    return combinableCon;
  }
  @XmlAttribute
  public String getGeneradoPor() {
    return generadoPor.get();
  }
  public void setGeneradoPor(String prod) {
    generadoPor.set(prod);
  }
  public StringProperty generadoPorProperty(){
    return generadoPor;
  }
  @XmlAttribute
  public String getActualizable() {
    return actualizable.get();
  }
  public boolean verSiEsActualizable() {
    return actualizable.get()==null || (actualizable.get()!=null && actualizable.get().equals("yes"));
  }
  public void setActualizable(boolean b) {
    actualizable.set(b ? "yes" : "no");
  }
  public StringProperty actualizableProperty(){
    return actualizable;
  }
}