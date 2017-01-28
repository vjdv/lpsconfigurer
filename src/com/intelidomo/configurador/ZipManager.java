package com.intelidomo.configurador;

import com.intelidomo.configurador.modelos.Proyecto;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import net.vjdv.cvlmaker.TraductorCVL;
import net.vjdv.cvlmaker.modelos.CVL;
import net.vjdv.xmiviewer.TraductorXMI;
import net.vjdv.xmiviewer.modelos.XMI;

/**
 *
 * @author Jassiel
 */
public class ZipManager {
  public Proyecto proyecto;
  public CVL cvl;
  public XMI xmi;
  private FileOutputStream fos;
  private ZipOutputStream zos;
  public boolean setZipFile(File f){
    try{
      fos = new FileOutputStream(f);
      zos = new ZipOutputStream(fos);
      return true;
    }catch(FileNotFoundException ex){
      Logger.getLogger("Configurador").log(Level.SEVERE, "Archivo zip no encontrado",ex);
      return false;
    }
  }
  public boolean setProyecto(Proyecto p){
    try{
      File f = File.createTempFile("arbol", ".cvl");
      TraductorProyecto.modelo2file(p, f);
      addToZipFile(f,"proyecto.xml");
      f.deleteOnExit();
      return true;
    }catch(IOException ex){
      Logger.getLogger("Configurador").log(Level.SEVERE, "No fue posible crear proyecto.xml",ex);
      return false;
    }
  }
  public boolean setCVL(CVL cvl){
    try{
      File f = File.createTempFile("arbol", ".cvl");
      TraductorCVL.modelo2file(cvl, f);
      addToZipFile(f, "arbol.cvl");
      f.deleteOnExit();
      return true;
    }catch(IOException ex){
      Logger.getLogger("Configurador").log(Level.SEVERE, "No fue posible crear arbol.cvl",ex);
      return false;
    }
  }
  public boolean setXMI(XMI xmi){
    try{
      File f = File.createTempFile("uml", ".xmi");
      TraductorXMI.modelo2file(xmi, f);
      addToZipFile(f, "uml.xmi");
      f.deleteOnExit();
      return true;
    }catch(IOException ex){
      Logger.getLogger("Configurador").log(Level.SEVERE, "No fue posible crear uml.xmi",ex);
      return false;
    }
  }
  public void addToZipFile(File file, String filename) throws FileNotFoundException, IOException {
    System.out.println("Writing " + file.getName() + " to zip file");
    try (FileInputStream fis = new FileInputStream(file)) {
      ZipEntry zipEntry = new ZipEntry(filename);
      zos.putNextEntry(zipEntry);
      byte[] bytes = new byte[1024];
      int length;
      while ((length = fis.read(bytes)) >= 0) {
        zos.write(bytes, 0, length);
      }
      zos.closeEntry();
    }
  }
  public void cerrarZip(){
    try{
      zos.close();
      fos.close();
    }catch(IOException ex){
      Logger.getLogger("Configurador").log(Level.SEVERE, "Error al cerrar zip",ex);
    }
  }
  public static ZipManager open(File zipFile) throws FileNotFoundException,IOException{
    ZipManager manager = new ZipManager();
    ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
    ZipEntry entry = zis.getNextEntry();
    while(entry!=null){
      String fileName = entry.getName();
      File newFile = File.createTempFile(fileName,null);
      System.out.println("file "+fileName+" unzip to: "+ newFile.getAbsoluteFile());
      new File(newFile.getParent()).mkdirs();
      try (FileOutputStream fos = new FileOutputStream(newFile)) {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = zis.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }
      }
      switch(fileName){
        case "arbol.cvl":
          manager.cvl = TraductorCVL.file2modelo(newFile);
          break;
        case "proyecto.xml":
          manager.proyecto = TraductorProyecto.file2modelo(newFile);
          break;
        case "uml.xmi":
          manager.xmi = TraductorXMI.file2modelo(newFile);
          break;
      }
      newFile.deleteOnExit();
      entry = zis.getNextEntry();
    }
    if(manager.cvl==null || manager.proyecto==null)
      throw new IOException("NO SE ENCONTRARON TODOS LOS ARCHIVOS DEL PROYECTO");
    return manager;
  }
}