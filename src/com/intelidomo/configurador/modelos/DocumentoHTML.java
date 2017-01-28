package com.intelidomo.configurador.modelos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstrae un documento HTML para contener la documentación generada
 * @author Jassiel
 */
public class DocumentoHTML {
  private int imagesCount = 0;
  private StringBuilder sb = new StringBuilder();
  /**
   * Escribe contenido en el documento
   * @param content contenido a agregar
   */
  public void write(String content){
    write(content,false,false);
  }
  /**
   * Escribe contenido en el documento y concatena un salto de línea al final
   * @param content contenido a agregar
   */
  public void writeLine(String content){
    write(content+"<br>",false,false);
  }
  /**
   * Escribe contenido en el documento
   * @param content contenido a agregar
   * @param replaceLines true para cambiar saltos de línea por etiquetas br
   * @param replaceTabs true para cambiar tabs por dos espacios tipo nbsp
   */
  public void write(String content, boolean replaceLines, boolean replaceTabs){
    content = replaceLines ? nl2br(content) : content;
    content = replaceTabs ? content.replaceAll("\t","&nbsp;&nbsp;") : content;
    sb.append(content);
  }
  /**
   * Agrega el código por defecto desde la etiqueta html a body
   * @param title Título del documento
   */
  public void writeHead(String title){
    sb.append("<!doctype html>\n<html>\n<head>\n<meta charset=\"utf-8\">\n<title>");
    sb.append(title);
    sb.append("</title>\n</head>\n<style>\n");
    sb.append("body{background:#DDD;font:16px Cambria,\"Hoefler Text\",\"Liberation Serif\",Times,\"Times New Roman\",serif;}\n");
    sb.append("h1{text-align:center;}\n");
    sb.append("h1,h2{font-family:\"Lucida Grande\",\"Lucida Sans Unicode\",\"Lucida Sans\",\"DejaVu Sans\",Verdana,sans-serif;}\n");
    sb.append("p{text-align:justify;}\n");
    sb.append("img{max-width:100%;}\n");
    sb.append("a,a:visited{color:#066;text-decoration:none;}\n");
    sb.append("a:hover{color:#077;text-decoration:underline;}\n");
    sb.append(".break{page-break-before:always;}\n");
    sb.append(".contenedor{width:100%;max-width:900px;margin:0 auto;background:#EEE;border-radius:7px;padding:10px;}\n");
    sb.append(".mono{font-family:Consolas, \"Andale Mono\", \"Lucida Console\", \"Lucida Sans Typewriter\", Monaco, \"Courier New\", monospace}\n");
    sb.append(".chico{font-size:12px;}\n");
    sb.append(".ns,.bu{text-decoration:underline;font-weight:bold;}\n");
    sb.append(".tabla{width:100%;border-collapse:collapse;}\n");
    sb.append(".tabla td, .tabla th{border:1px solid #777;padding:2px 5px 2px 5px;}\n");
    sb.append(".tabla th {padding: 2px 0 2px 0;background-color:#D6D6D6;color:#222;}\n");
    sb.append("</style>\n<body>\n<div class=\"contenedor\">\n");
  }
  /**
   * Agrega el final del documento
   */
  public void writeFoot(){
    sb.append("</div><!--fin contenedor-->\n</body>\n</html>");
  }
  /**
   * Agrega una anotación de salto de página
   */
  public void writePagebreak(){
    sb.append("<div class=\"break\"/>\n");
  }
  /**
   * Agrega la portada del documento
   * @param titulo Título de la LPS
   * @param esAplicacion true si se genera un producto o false si es documentación del dominio
   */
  public void writeFront(String titulo, boolean esAplicacion){
    sb.append("<div id=\"portada\">\n");
    sb.append("<h1>").append(titulo).append("</h1>\n");
    String subtitulo = esAplicacion ? "Ingeniería de aplicación" : "Ingeniería de dominio";
    writeTitle(subtitulo);
    String x = esAplicacion ?
            "La ingeniería de aplicación es la segunda etapa definida en el marco de trabajo empleado para la implementación de líneas de productos de software. "
            +"En esta etapa se explota las posibilidades de la plataforma y la variabilidad definidas en la ingeniería de dominio, se reutilizan los insumos de dominio tanto como sea posible, "
            +"se documentan los artefactos de la aplicación (como requerimientos, arquitectura, componentes y pruebas) y se relacionan con los artefactos elaborados en la ingeniería de dominio, "
            +"se enlaza la variabilidad con los requerimientos de una aplicación para extender la arquitectura, componentes y casos de uso, "
            +"por último se estima el impacto de las diferencias de los requerimientos de dominio y aplicación en las arquitecturas, componentes y pruebas. "
            +"El principal objetivo de esta etapa es el desarrollo de un producto específico planeado en la línea de productos de software."
            :"La ingeniería de dominio es la primera etapa definida en el marco de trabajo empleado para la realización de líneas de productos de software. "
            +"En esta etapa se define lo común y lo variable de una línea de productos de software así como las aplicaciones de software planeadas y el alcance de ellas. "
            +"Se definen y construyen artefactos reutilizables que cumplan con la variabilidad deseada. Consta de cinco subprocesos en cada uno de los cuales se detalla y refina la variabilidad definida en el subproceso anterior, "
            +"también se retroalimenta la factibilidad de realizar la variabilidad planeada. El principal objetivo de esta etapa es el desarrollo de la plataforma de software.";
    writeParagraph(x);
    DateFormat df = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy 'a las' HH:mm:ss",new Locale("es","MX"));
    Date date = new Date();
    String p = "El presente documento ha sido generado a partir de un conjunto de insumos que pertenecen a las fases de análisis de requerimientos, diseño, implementación y pruebas de una línea de productos de software. ";
    p += "Ha sido generado automáticamente por un configurador de LPSs que permite capturar toda la información relevante desde una interfaz intuitiva y amigable. ";
    p += "El documento ha sido generado el "+df.format(date)+". Para más información visite la <a href=\"http://jassiel.vjdv.net/apps/configuradorlps/\">página de la aplicación</a>.";
    writeParagraph(p);
    sb.append("</div><!--fin portada-->\n");
    writePagebreak();
  }
  /**
   * Agrega un título en el documento con la etiqueta h2
   * @param content Contenido del título
   */
  public void writeTitle(String content){
    sb.append("<h2>").append(nl2br(content)).append("</h2>\n");
  }
  /**
   * Agrega un párrafo nuevo
   * @param content Contenido del párrafo a agregar
   */
  public void writeParagraph(String content){
    sb.append("<p>").append(nl2br(content)).append("</p>\n");
  }
  /**
   * Agrega una imagen según su url
   * @param url url de la imagen relativa al documento
   * @return número de la imagen o figura en el documento
   */
  public int writeImage(String url){
    imagesCount++;
    sb.append("<div align=\"center\"><a href=\"").append(url).append("\"><img src=\"").append(url).append("\" alt=\"Figura ").append(imagesCount).append("\"><br>Figura ").append(imagesCount).append("</a></div>");
    return imagesCount;
  }
  /**
   * Reemplaza saltos por etiquetas br
   * @param txt Texto con saltos \n
   * @return Texto con saltos br
   */
  private String nl2br(String txt){
    return txt.replaceAll("\n", "<br>");
  }
  /**
   * Crea un archivo con el contenido agregado
   * @param f Ruta del archivo
   * @return Devuelve true si el archivo se creó y escribió con éxito
   */
  public boolean toFile(File f){
    try (FileWriter fw = new FileWriter(f)) {
      fw.write(sb.toString());
      return true;
    }catch(IOException ex){
      Logger.getLogger("Configurador").log(Level.WARNING,"No se pudo escribir archivo",ex);
      return false;
    }
  }
  /**
   * Permite crear un nuevo documento desde cero
   */
  public void clear(){
    sb = new StringBuilder();
  }
}