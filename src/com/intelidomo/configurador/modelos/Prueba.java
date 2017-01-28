package com.intelidomo.configurador.modelos;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Representa una prueba a realizarse sobre los productos
 * @author Jassiel
 */
@XmlType(propOrder={"id","condicion","descripcion","requisitos","entrada","salida","implementacion"})
public class Prueba implements Condicionable{
  private final SimpleStringProperty id = new SimpleStringProperty();
  private final SimpleStringProperty condicion = new SimpleStringProperty();
  private final SimpleStringProperty descripcion = new SimpleStringProperty();
  private final SimpleStringProperty requisitos = new SimpleStringProperty();
  private final SimpleStringProperty entrada = new SimpleStringProperty();
  private final SimpleStringProperty salida = new SimpleStringProperty();
  private final SimpleStringProperty implementacion = new SimpleStringProperty();
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
  @XmlAttribute
  public String getDescripcion() {
    return descripcion.get();
  }
  public void setDescripcion(String txt) {
    descripcion.set(txt);
  }
  public StringProperty descripcionProperty(){
    return descripcion;
  }
  @XmlAttribute
  public String getRequisitos() {
    return requisitos.get();
  }
  public void setRequisitos(String str) {
    requisitos.set(str);
  }
  public StringProperty requisitosProperty(){
    return requisitos;
  }
  @XmlAttribute
  public String getEntrada() {
    return entrada.get();
  }
  public void setEntrada(String str) {
    entrada.set(str);
  }
  public StringProperty entradaProperty(){
    return entrada;
  }
  @XmlAttribute
  public String getSalida() {
    return salida.get();
  }
  public void setSalida(String res) {
    salida.set(res);
  }
  public StringProperty salidaProperty(){
    return salida;
  }
  @XmlAttribute
  public String getImplementacion() {
    return implementacion.get();
  }
  public void setImplementacion(String imp) {
    implementacion.set(imp);
  }
  public StringProperty implementacionProperty(){
    return implementacion;
  }
  //Para ejecución
  @XmlTransient
  public StringBuilder trace = new StringBuilder();
  @XmlTransient
  public boolean exito = false;
  /**
   * Limpia la salida
   */
  public void clearTrace(){
    trace = new StringBuilder();
  }
}