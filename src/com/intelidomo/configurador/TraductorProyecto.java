package com.intelidomo.configurador;

import com.intelidomo.configurador.modelos.Proyecto;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Jassiel
 */
public class TraductorProyecto {
  public static boolean modelo2file(Proyecto p, File f){
    try{
      JAXBContext jaxbContext = JAXBContext.newInstance(Proyecto.class);
      Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
      jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      jaxbMarshaller.marshal(p,System.out);
      jaxbMarshaller.marshal(p,f);
      return true;
    }catch(Exception ex){
      Logger.getLogger("Configurador").log(Level.WARNING,"ERROR AL GUARDAR MODELO PROYECTO",ex);
      return false;
    }
  }
  public static Proyecto file2modelo(File file){
    Proyecto p;
    try{
      JAXBContext context = JAXBContext.newInstance(Proyecto.class);
      Unmarshaller u = context.createUnmarshaller();
      p = (Proyecto) u.unmarshal(new FileInputStream(file));
      return p;
    }catch(JAXBException | FileNotFoundException ex){
      Logger.getLogger("Configurador").log(Level.SEVERE, "Error al importar modelo proyecto", ex);
      return null;
    }
  }
  public static boolean view2image(Pane view, File file){
    if(view==null || file==null) return false;
    WritableImage snapshot = view.snapshot(new SnapshotParameters(), null);
    BufferedImage image = javafx.embed.swing.SwingFXUtils.fromFXImage(snapshot, null);
    try {
      Graphics2D gd = (Graphics2D) image.getGraphics();
      gd.translate(view.getWidth(), view.getHeight());
      ImageIO.write(image, "png", file);
      return true;
    } catch (IOException ex) {
      Logger.getLogger("Configurador").log(Level.SEVERE, null, ex);
      return false;
    }
  }
}