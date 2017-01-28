package com.intelidomo.configurador.modelos;

import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author Jassiel
 */
public class Informacion {
  @XmlAttribute
  public String nombre_largo = "";
  @XmlAttribute
  public String introduccion = "";
  @XmlAttribute
  public String carpetaExportacion = System.getProperty("user.home");
  @XmlAttribute
  public String deltajPath = "";
  @XmlAttribute
  public String classpathExtra = "";
}