package com.intelidomo.configurador;

import com.intelidomo.configurador.modelos.ClasspathHacker;
import com.intelidomo.configurador.modelos.CondicionParser;
import com.intelidomo.configurador.modelos.Condicionable;
import com.intelidomo.configurador.modelos.Diagrama;
import com.intelidomo.configurador.modelos.DocumentoHTML;
import com.intelidomo.configurador.modelos.Producto;
import com.intelidomo.configurador.modelos.Proyecto;
import com.intelidomo.configurador.modelos.Prueba;
import com.intelidomo.configurador.modelos.Requerimiento;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import javax.swing.JOptionPane;
import net.vjdv.cvlmaker.ListenerView;
import net.vjdv.cvlmaker.TraductorCVL;
import net.vjdv.cvlmaker.VistaCVL;
import net.vjdv.cvlmaker.VistaResolutionX;
import net.vjdv.cvlmaker.modelos.CVL;
import net.vjdv.cvlmaker.modelos.Choice;
import net.vjdv.xmiviewer.TraductorXMI;
import net.vjdv.xmiviewer.graph.VistaXMI;
import net.vjdv.xmiviewer.modelos.Diagram;
import net.vjdv.xmiviewer.modelos.XMI;

/**
 *
 * @author Jassiel
 */
public class PrincipalController implements Initializable,ListenerView {
  //VARIABLES
  private final DirectoryChooser dirChooser = new DirectoryChooser();
  private final FileChooser cvlChooser = new FileChooser();
  private final FileChooser lpsChooser = new FileChooser();
  private final FileChooser xmiChooser = new FileChooser();
  private final VistaResolutionX product_view = new VistaResolutionX();
  private final VistaCVL cvl_view = new VistaCVL();
  private File zip_file;
  private Proyecto proyecto;
  private CVL cvl;
  private XMI xmi;
  @FXML
  private TabPane tabs;
  @FXML
  private TreeView<String> tree;
  @FXML
  private ScrollPane cvl_scroll, product_cvl_scroll;
  
  
  @FXML
  private void nuevoProyecto(ActionEvent event) {
    if(proyecto!=null){
      int x = JOptionPane.showConfirmDialog(null, "¿Seguro de crear un nuevo proyecto?\nLos cambios no guardados se perderán.");
      if(x!=JOptionPane.YES_OPTION) return;
    }
    String nombre = JOptionPane.showInputDialog("Nombre del proyecto:");
    if(nombre!=null){
      Proyecto p = new Proyecto();
      p.nombre = nombre;
      proyecto = p;
      cvl = new CVL();
      cvl.name = p.nombre;
      xmi = null;
      prepararInterfaz();
      zip_file = null;
    }
  }
  @FXML
  public void abrir(ActionEvent evt){
    File file = lpsChooser.showOpenDialog(Main.stage);
    if(file!=null){
      try{
        ZipManager manager = ZipManager.open(file);
        proyecto = manager.proyecto;
        cvl = manager.cvl;
        xmi = manager.xmi;
        zip_file = file;
        prepararInterfaz();
      }catch(Exception ex){
        JOptionPane.showMessageDialog(null, "Archivo no válido");
        Logger.getLogger("Configurador").log(Level.WARNING,"Error al abrir proyecto",ex);
      }
    }
  }
  /**
   * Llena la información de los componentes de la interfaz cada que se abre o crea un nuevo proyecto
   */
  private void prepararInterfaz(){
    //Información
    nombre_txt.setText(proyecto.nombre);
    nombre2_txt.setText(proyecto.info.nombre_largo);
    introduccion_txt.setText(proyecto.info.introduccion);
    //Características
    cvl_view.clear();
    cvl_view.setCVL(cvl);
    updatedModel();
    //Productos
    agregarProductoImp(proyecto.productos);
    productos_list.getItems().clear();
    productos_list.getItems().addAll(proyecto.productos);
    //Requerimientos
    req_tb.getItems().clear();
    req_tb.getItems().addAll(proyecto.requerimientos);
    req_tb.sort();
    //Implementación
    deltaj_txt.setText(proyecto.info.deltajPath);
    classpath_txt.setText(proyecto.info.classpathExtra);
    agregarClasspath(proyecto.info.deltajPath+File.separator+"bin");
    agregarClasspath(proyecto.info.deltajPath+File.separator+"lib");
    for(String path : proyecto.info.classpathExtra.split(";")) agregarClasspath(path);
    //Diseño
    updatedXMI();
    //Pruebas
    test_tb.getItems().clear();
    test_tb.getItems().addAll(proyecto.pruebas);
    //Exportación
    carpeta_lb.setText(proyecto.info.carpetaExportacion);
    exportDir = new File(proyecto.info.carpetaExportacion);
    productos_cbox.getItems().clear();
    productos_cbox.getItems().addAll(proyecto.productos);
    tabs.setDisable(false);
  }
  @FXML
  private void guardar(ActionEvent event){
    if(zip_file==null) guardarComo(event);
    else guardarArchivo();
  }
  @FXML
  private void guardarComo(ActionEvent event){
    File file = lpsChooser.showSaveDialog(Main.stage);
    if(file!=null){
      zip_file = file;
      guardarArchivo();
    }
  }
  private void guardarArchivo(){
    ZipManager manager = new ZipManager();
    manager.setZipFile(zip_file);
    manager.setCVL(cvl);
    manager.setProyecto(proyecto);
    if(xmi!=null) manager.setXMI(xmi);
    manager.cerrarZip();
  }
  @FXML
  private void importarCVL(ActionEvent event){
    if(cvl!=null){
      int x = JOptionPane.showConfirmDialog(null, "El CVL importado reemplazará al actual.\n¿Continuar?");
      if(x!=JOptionPane.YES_OPTION) return;
    }
    File file = cvlChooser.showOpenDialog(Main.stage);
    if(file==null) return;
    cvl = TraductorCVL.file2modelo(file);
    if(cvl==null){
      JOptionPane.showMessageDialog(null, "Archivo CVL no válido");
      return;
    }
    prepararInterfaz();
  }
  @FXML
  private void importarXMI(ActionEvent event){
    if(xmi!=null){
      int x = JOptionPane.showConfirmDialog(null, "El XMI importado reemplazará al actual.\n¿Continuar?");
      if(x!=JOptionPane.YES_OPTION) return;
    }
    File file = xmiChooser.showOpenDialog(Main.stage);
    if(file==null) return;
    XMI xmi_x = TraductorXMI.file2modelo(file);
    if(xmi_x==null){
      JOptionPane.showMessageDialog(null, "Archivo XMI no válido");
      return;
    }
    xmi = xmi_x;
    updatedXMI();
  }
  @Override
  public void updatedModel() {
    proyecto.caracteristicas.clear();
    proyecto.caracteristicas.agregarCaracteristica(cvl.obtenerNombre());
    TreeItem<String> item = new TreeItem<>(cvl.obtenerNombre());
    item.setExpanded(true);
    leerHijos(cvl, item);
    tree.setRoot(item);
  }
  private void leerHijos(Choice padre_choice, TreeItem<String> padre_tree){
    for(Choice hijo : padre_choice.obtenerHijos()){
      proyecto.caracteristicas.agregarCaracteristica(hijo.obtenerNombre());
      TreeItem<String> item = new TreeItem<>(hijo.obtenerNombre());
      padre_tree.getChildren().add(item);
      item.setExpanded(true);
      leerHijos(hijo, item);
    }
  }
  // I N F O R M A C I Ó N
  @FXML
  private TextField nombre_txt, nombre2_txt;
  @FXML
  private TextArea introduccion_txt;
  @FXML
  public void guardarInformacion(ActionEvent event){
    proyecto.nombre = nombre_txt.getText();
    proyecto.info.nombre_largo = nombre2_txt.getText();
    proyecto.info.introduccion = introduccion_txt.getText();
    System.out.println("Información actualizada");
  }
  // P R O D U C T O S
  @FXML
  private ListView<Producto> productos_list;
  @FXML
  private void nuevoProducto(ActionEvent event){
    String nuevo_nombre = JOptionPane.showInputDialog("Nombre del nuevo producto");
    if(nuevo_nombre==null) return;
    Producto p = new Producto();
    p.nombre = nuevo_nombre;
    proyecto.productos.add(p);
    productos_list.getItems().add(p);
    productos_cbox.getItems().add(p);
    CVL nuevo_cvl = new CVL();
    nuevo_cvl.name = cvl.name;
    p.cvl = nuevo_cvl;
  }
  @FXML
  private void eliminarProducto(ActionEvent event){
    Producto p = productos_list.getSelectionModel().getSelectedItem();
    productos_list.getItems().remove(p);
    productos_cbox.getItems().remove(p);
    proyecto.productos.remove(p);
  }
  // R E Q U E R I M I E N T O S
  private Requerimiento reqCurrentEdit;
  @FXML
  private TableView<Requerimiento> req_tb;
  @FXML
  private TableColumn<Requerimiento,String> reqId_col, reqCond_col, reqDesc_col;
  @FXML
  private VBox reqForm_box;
  @FXML
  private TextField reqId_txt, reqCond_txt;
  @FXML
  private TextArea reqDesc_txt;
  @FXML
  private void nuevoRequerimiento(ActionEvent event){
    reqCurrentEdit = null;
    reqForm_box.setVisible(true);
    AnchorPane.setRightAnchor(req_tb, 250d);
    reqId_txt.setText("");
    reqCond_txt.setText("");
    reqDesc_txt.setText("");
    reqId_txt.requestFocus();
  }
  @FXML
  private void cancelarRequerimiento(ActionEvent event){
    reqForm_box.setVisible(false);
    AnchorPane.setRightAnchor(req_tb, 0d);
  }
  @FXML
  private void editarRequerimiento(ActionEvent event){
    Requerimiento r = req_tb.getSelectionModel().getSelectedItem();
    if(r==null) return;
    reqCurrentEdit = r;
    reqId_txt.setText(r.getId());
    reqCond_txt.setText(r.getCondicion());
    reqDesc_txt.setText(r.getTexto());
    reqForm_box.setVisible(true);
    AnchorPane.setRightAnchor(req_tb, 250d);
  }
  @FXML
  private void eliminarRequerimiento(ActionEvent event){
    Requerimiento r = req_tb.getSelectionModel().getSelectedItem();
    if(r==null) return;
    if(JOptionPane.showConfirmDialog(null, "¿Seguro de eliminar a "+r.getId())==JOptionPane.YES_OPTION){
      proyecto.requerimientos.remove(r);
      req_tb.getItems().remove(r);
      cancelarRequerimiento(event);
    }
  }
  @FXML
  private void agregarRequerimiento(ActionEvent event){
    Requerimiento r = reqCurrentEdit==null ? new Requerimiento() : reqCurrentEdit;
    r.setId(reqId_txt.getText());
    r.setCondicion(reqCond_txt.getText());
    r.setTexto(reqDesc_txt.getText());
    if(reqCurrentEdit==null){
      req_tb.getItems().add(r);
      proyecto.requerimientos.add(r);
    }
    req_tb.sort();
    reqCurrentEdit = null;
    cancelarRequerimiento(event);
  }
  // D I S E Ñ O
  private final VistaXMI diagram_view = new VistaXMI();
  @FXML
  private VBox diagramas_lightbox;
  @FXML
  private ScrollPane diagramas_scroll;
  @FXML
  private ListView<Diagram> diagramasDisp_list;
  @FXML
  private TableView<Diagrama> diagramasUse_tb;
  @FXML
  private TableColumn<Diagrama,String> duseNombre_col, duseCond_col, duseComb_col;
  @FXML
  private TableView<Diagrama> diagramasGen_tb;
  @FXML
  private TableColumn<Diagrama,String> dgenNombre_col, dgenGen_col;
  
  public void updatedXMI(){
    diagramas_lightbox.setVisible(xmi==null);
    if(xmi==null){
      diagramasDisp_list.getItems().clear();
    }else{
      diagramasDisp_list.getItems().clear();
      diagramasDisp_list.getItems().addAll(xmi.diagram_list);
      List<Diagrama> listaDelete = new ArrayList<>();
      proyecto.diagramas.forEach(d -> {
        Diagram dx = xmi.getDiagramByID(d.getId());
        if(dx!=null){
          diagramasDisp_list.getItems().remove(dx);
          d.setNombre((dx.name!=null && !dx.name.isEmpty() ? dx.name : "Diagrama sin nombre")+" ("+(dx.id!=null ? dx.id : "sin id")+")");
          if(d.getCondicion()!=null) diagramasUse_tb.getItems().add(d);
          else if(d.getGeneradoPor()!=null) diagramasGen_tb.getItems().add(d);
          else listaDelete.add(d);
        }else listaDelete.add(d);
      });
      proyecto.diagramas.removeAll(listaDelete);
    }
  }
  class DiagramaCombinableDialog extends Dialog<Pair<String,Diagram>>{
    private final ComboBox<Diagram> base_cbox;
    private final TextField condicion_txt;
    private final Node okButton;
    public DiagramaCombinableDialog(){
      setTitle("Diálogo");
      setHeaderText("Detalles de la combinación");
      ButtonType okButtonType = new ButtonType("Agregar", ButtonData.OK_DONE);
      getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
      okButton = getDialogPane().lookupButton(okButtonType);
      condicion_txt = new TextField();
      HBox hbox1 = new HBox(new Label("Condición:"),condicion_txt);
      hbox1.setSpacing(5d);
      base_cbox = new ComboBox<>();
      base_cbox.setOnMousePressed((MouseEvent event) -> resolverComboboxBug(event));
      proyecto.diagramas.stream().filter(d->d.getCondicion()!=null && d.getCondicion().equals("base")).forEach(d->{
        Diagram dx = xmi.getDiagramByID(d.getId());
        base_cbox.getItems().add(dx);
      });
      HBox hbox2 = new HBox(new Label("Se combina con:"),base_cbox);
      hbox2.setSpacing(5d);
      VBox vbox = new VBox(hbox1,hbox2);
      vbox.setSpacing(5d);
      getDialogPane().setContent(vbox);
      condicion_txt.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
        isCondicionValid(!newValue.isEmpty());
      });
      base_cbox.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Diagram> observable, Diagram oldValue, Diagram newValue) -> {
        isDiagramValid(newValue!=null);
      });
      isCondicionValid(false);
      isDiagramValid(false);
      setResultConverter(dialogButton->{
        if(dialogButton==okButtonType){
          Diagram d = base_cbox.getSelectionModel().getSelectedItem();
          return new Pair<>(condicion_txt.getText(),d);
        }
        return null;
      });
      Platform.runLater(()-> condicion_txt.requestFocus());
    }
    public void setCondicionInicial(String cond){
      condicion_txt.setText(cond);
      isCondicionValid(cond!=null && !cond.isEmpty());
    }
    public void setDiagramInicial(Diagram d){
      base_cbox.getSelectionModel().select(d);
      isDiagramValid(d!=null);
    }
    boolean b1 = false, b2 = false;
    private void isCondicionValid(boolean b){
      b1 = b;
      okButton.setDisable(!(b1&&b2));
    }
    private void isDiagramValid(boolean b){
      b2 = b;
      okButton.setDisable(!(b1&&b2));
    }
  }
  // I M P L E M E N T A C I Ó N
  VBox productosImp_box = new VBox();
  @FXML
  private TextField deltaj_txt, classpath_txt;
  @FXML
  private ScrollPane productosImp_scroll;
  @FXML
  private void elegirCarpetaImp(ActionEvent event){
    File dir = dirChooser.showDialog(null);
    if(dir==null) return;
    proyecto.info.deltajPath = dir.getAbsolutePath();
    deltaj_txt.setText(dir.getAbsolutePath());
    agregarClasspath(dir.getAbsolutePath()+File.separator+"bin");
    agregarClasspath(dir.getAbsolutePath()+File.separator+"lib");
  }
  @FXML
  private void agregarClasspath(ActionEvent event){
    File dir = dirChooser.showDialog(null);
    if(dir==null) return;
    String current = proyecto.info.classpathExtra;
    String nuevo = current.isEmpty() ? dir.getAbsolutePath() : current+File.pathSeparator+dir.getAbsolutePath();
    proyecto.info.classpathExtra = nuevo;
    classpath_txt.setText(nuevo);
    agregarClasspath(dir.getAbsolutePath());
  }
  private void agregarClasspath(String str){
    if(str.isEmpty()) return;
    try{
      File file_dir = new File(str);
      ClasspathHacker.addFile(file_dir);
      Logger.getLogger("Configurador").log(Level.WARNING, "Directorio agregado a classpath: {0}", str);
      if(file_dir.isDirectory()){
        for(File jar_file : file_dir.listFiles()){
          if(jar_file.isFile() && jar_file.getAbsolutePath().endsWith(".jar")) agregarClasspath(jar_file.getAbsolutePath());
        }
      }
    
    }catch(IOException ex){
      Logger.getLogger("Configurador").log(Level.WARNING, null, ex);
    }
  }
  private void agregarProductoImp(Producto p){
    Label lb = new Label("Información de implementación de producto "+p.nombre);
    lb.getStyleClass().add("titulo2");
    TextField paq_txt = new TextField(p.paquete);
    TextField cls_txt = new TextField(p.clasePrincipal);
    Button boton = new Button("Guardar");
    boton.setOnAction((ActionEvent event) -> {
      p.paquete = paq_txt.getText();
      p.clasePrincipal = cls_txt.getText();
    });
    productosImp_box.getChildren().addAll(new VBox(lb,new HBox(new Label("Paquete:"),paq_txt),new HBox(new Label("Clase con main:"),cls_txt),boton));
  }
  private void agregarProductoImp(List<Producto> list){
    list.forEach((p)-> agregarProductoImp(p));
  }
  // P R U E B A S
  private Prueba testCurrentEdit;
  @FXML
  private TableView<Prueba> test_tb;
  @FXML
  private TableColumn<Prueba,String> testId_col, testCond_col, testDesc_col, testReq_col, testIn_col, testOut_col, testImp_col;
  @FXML
  private VBox testForm_box;
  @FXML
  private TextField testId_txt, testCond_txt, testImp_txt;
  @FXML
  private TextArea testDesc_txt, testReq_txt, testIn_txt, testOut_txt;
  @FXML
  private void nuevaPrueba(ActionEvent event){
    testForm_box.setVisible(true);
    AnchorPane.setRightAnchor(test_tb, 250d);
    testId_txt.setText("");
    testCond_txt.setText("");
    testDesc_txt.setText("");
    testReq_txt.setText("");
    testIn_txt.setText("");
    testOut_txt.setText("");
    testImp_txt.setText("");
    testId_txt.requestFocus();
    testCurrentEdit = null;
  }
  @FXML
  private void cancelarPrueba(ActionEvent event){
    testForm_box.setVisible(false);
    AnchorPane.setRightAnchor(test_tb, 0d);
  }
  @FXML
  private void editarPrueba(ActionEvent event){
    Prueba p = test_tb.getSelectionModel().getSelectedItem();
    if(p==null) return;
    testCurrentEdit = p;
    testId_txt.setText(p.getId());
    testCond_txt.setText(p.getCondicion());
    testDesc_txt.setText(p.getDescripcion());
    testReq_txt.setText(p.getRequisitos());
    testIn_txt.setText(p.getEntrada());
    testOut_txt.setText(p.getSalida());
    testImp_txt.setText(p.getImplementacion());
    testForm_box.setVisible(true);
    AnchorPane.setRightAnchor(test_tb, 250d);
  }
  @FXML
  private void eliminarPrueba(ActionEvent event){
    Prueba p = test_tb.getSelectionModel().getSelectedItem();
    if(p==null) return;
    if(JOptionPane.showConfirmDialog(null, "¿Seguro de eliminar a "+p.getId())==JOptionPane.YES_OPTION){
      proyecto.pruebas.remove(p);
      test_tb.getItems().remove(p);
      cancelarPrueba(event);
    }
  }
  @FXML
  private void agregarPrueba(ActionEvent event){
    Prueba p = testCurrentEdit==null ? new Prueba() : testCurrentEdit;
    p.setId(testId_txt.getText());
    p.setCondicion(testCond_txt.getText());
    p.setDescripcion(testDesc_txt.getText());
    p.setRequisitos(testReq_txt.getText());
    p.setEntrada(testIn_txt.getText());
    p.setSalida(testOut_txt.getText());
    p.setImplementacion(testImp_txt.getText());
    if(testCurrentEdit==null){
      test_tb.getItems().add(p);
      proyecto.pruebas.add(p);
    }
    testCurrentEdit = null;
    cancelarPrueba(event);
    test_tb.sort();
  }
  // E X P O R T A C I Ó N
  private File exportDir;
  private boolean exportarApp = false;
  @FXML
  private CheckBox req_check, dis_check, imp_check, pru_check, html_check, pdf_check;
  @FXML
  private Label carpeta_lb;
  @FXML
  private ComboBox<Producto> productos_cbox;
  @FXML
  public void cambiarCarpeta(ActionEvent event){
    dirChooser.setInitialDirectory(exportDir);
    File newDir = dirChooser.showDialog(Main.stage);
    if(newDir==null) return;
    exportDir = newDir;
    proyecto.info.carpetaExportacion = exportDir.getAbsolutePath();
    carpeta_lb.setText(exportDir.getAbsolutePath());
  }
  @FXML
  public void cambiarProceso(ActionEvent event){
    RadioButton radio = (RadioButton) event.getSource();
    if(!radio.isSelected()) return;
    if(radio.getText().endsWith("dominio")){
      exportarApp = false;
      productos_cbox.setDisable(true);
    }else{
      exportarApp = true;
      productos_cbox.setDisable(false);
      productos_cbox.getSelectionModel().selectFirst();
    }
  }
  @FXML
  public void exportar(ActionEvent event){
    //Controles del diálogo
    ProgressBar progress = new ProgressBar(0);
    Label estado = new Label("Exportando...");
    VBox box = new VBox(estado,progress);
    box.setSpacing(5);
    progress.prefWidthProperty().bind(box.widthProperty().subtract(10));
    TextArea area = new TextArea();
    area.setEditable(false);
    //Configuración del diálogo
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Exportar");
    alert.setHeaderText("Exportando documentación de "+(exportarApp ? "aplicación" : "dominio")+".");
    alert.getDialogPane().setContent(box);
    alert.getDialogPane().setExpandableContent(area);
    alert.getDialogPane().setExpanded(true);
    //Configuración del task y thread
    Exporter task = new Exporter();
    task.setTextArea(area);
    progress.progressProperty().bind(task.progressProperty());
    estado.textProperty().bind(task.messageProperty());
    Thread hilo = new Thread(task);
    hilo.setDaemon(true);
    hilo.start();
    //Muestra diálogo
    alert.showAndWait();
  }
  /**
   * Resuelve un bug de incompatibilidad en Windows 10 e Intel Graphics
   * @param event 
   * @see https://bugs.openjdk.java.net/browse/JDK-8132897
   */
  @FXML
  public void resolverComboboxBug(MouseEvent event){
    ((ComboBox)event.getSource()).requestFocus();
  }
  // I N I C I A L I Z A C I Ó N
  /**
   * Establece el comportamiento inicial de los elementos de la interfaz y otros objetos.
   * @param url Contexto donde se ejecuta la aplicación
   * @param rb Conjunto de recursos del FXML
   */
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    //Características
    lpsChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Proyecto de LPS (lps)","*.lps"));
    lpsChooser.setInitialFileName("*.lps");
    cvlChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CVL (xml o cvl)","*.cvl","*.xml"));
    cvlChooser.setInitialFileName("*.cvl");
    xmiChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos XMI (xmi)","*.xmi"));
    xmiChooser.setInitialFileName("*.xmi");
    cvl_view.addOnUpdateListener(this);
    cvl_scroll.setContent(cvl_view);
    //Productos
    product_cvl_scroll.setContent(product_view);
    productos_list.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Producto> observable, Producto oldValue, Producto newValue) -> {
      product_view.clear();
      product_view.setCVL(cvl, newValue.cvl);
      product_view.removeOnUpdateListener(oldValue);
      product_view.addOnUpdateListener(newValue);
    });
    //Requerimientos
    cancelarRequerimiento(null);
    reqId_col.setCellValueFactory(new PropertyValueFactory<>("id"));
    reqCond_col.setCellValueFactory(new PropertyValueFactory<>("condicion"));
    reqDesc_col.setCellValueFactory(new PropertyValueFactory<>("texto"));
    reqId_col.setSortType(TableColumn.SortType.ASCENDING);
    req_tb.getSortOrder().add(reqId_col);
    req_tb.sort();
    //Diseño
    diagramas_scroll.setContent(diagram_view);
    ChangeListener<Diagram> diagramasListener = (ObservableValue<? extends Diagram> observable, Diagram oldValue, Diagram newValue) -> {
      diagram_view.getChildren().clear();
      diagram_view.setModels(xmi, newValue);
      diagram_view.draw();
    };
    diagramasDisp_list.getSelectionModel().selectedItemProperty().addListener(diagramasListener);
    diagramasDisp_list.setCellFactory(lv->{
      ListCell<Diagram> cell = new ListCell<>();
      ContextMenu contextMenu = new ContextMenu();
      MenuItem m1 = new MenuItem("Utilizar como diagrama base");
      m1.setOnAction(event->{
        Diagram d = cell.getItem();
        Diagrama dx = new Diagrama();
        dx.setId(d.id);
        dx.setNombre(d.name+" ("+d.id+")");
        dx.setCondicion("base");
        proyecto.diagramas.add(dx);
        diagramasDisp_list.getItems().remove(d);
        diagramasUse_tb.getItems().add(dx);
      });
      MenuItem m2 = new MenuItem("Utilizar para composición");
      m2.setOnAction((event)->{
        Diagram d = cell.getItem();
        Diagrama dx = new Diagrama();
        dx.setId(d.id);
        DiagramaCombinableDialog dialog = new DiagramaCombinableDialog();
        Optional<Pair<String, Diagram>> result = dialog.showAndWait();
        result.ifPresent(selectedData -> {
          Diagrama dy = new Diagrama();
          dy.setId(d.id);
          dy.setNombre(d.name);
          dx.setNombre(d.name+" ("+d.id+")");
          dy.setCondicion(selectedData.getKey());
          dy.setCombinableCon(selectedData.getValue().id);
          proyecto.diagramas.add(dy);
          diagramasUse_tb.getItems().add(dy);
          diagramasDisp_list.getItems().remove(d);
        });
      });
      contextMenu.getItems().addAll(m1,m2);
      if(cell.getItem()!=null)cell.setText(cell.getItem().id);
      cell.itemProperty().addListener((ObservableValue<? extends Diagram> obs, Diagram oldValue, Diagram newValue) -> {
        if(newValue!=null)cell.setText(newValue.toString());
      });
      cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
        if (isNowEmpty){
          cell.setContextMenu(null);
          cell.setText(null);
        }else cell.setContextMenu(contextMenu);
      });
      return cell;
    });
    diagramasUse_tb.setRowFactory(tv->{
      TableRow<Diagrama> row = new TableRow<>();
      ContextMenu contextMenu = new ContextMenu();
      MenuItem m1 = new MenuItem("Dejar de utilizar");
      m1.setOnAction(event->{
        Diagrama d = row.getItem();
        diagramasUse_tb.getItems().remove(d);
        proyecto.diagramas.remove(d);
        Diagram dx = xmi.getDiagramByID(d.getId());
        diagramasDisp_list.getItems().add(dx);
      });
      contextMenu.getItems().addAll(m1);
      row.emptyProperty().addListener((obs,wasEmpty,isNowEmpty)->{
        if (isNowEmpty){
          row.setContextMenu(null);
//          row.setText(null);
        }else row.setContextMenu(contextMenu);
      });
      return row;
    });
    diagramasUse_tb.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Diagrama> observable, Diagrama oldValue, Diagrama newValue) -> {
      if(newValue==null) return;
      Diagram d = xmi.getDiagramByID(newValue.getId());
      diagram_view.getChildren().clear();
      diagram_view.setModels(xmi, d);
      diagram_view.draw();
    });
    duseNombre_col.setCellValueFactory(new PropertyValueFactory<>("nombre"));
    duseCond_col.setCellValueFactory(new PropertyValueFactory<>("condicion"));
    duseComb_col.setCellValueFactory((new PropertyValueFactory<>("combinableCon")));
    diagramasGen_tb.setRowFactory(tv->{
      TableRow<Diagrama> row = new TableRow<>();
      ContextMenu contextMenu = new ContextMenu();
      CheckMenuItem m1 = new CheckMenuItem("Siempre actualizar");
      m1.setSelected(true);
      m1.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        if(newValue==null) return;
        Diagrama diagrama = row.getItem();
        diagrama.setActualizable(newValue);
      });
//      MenuItem m2 = new MenuItem("Acomodar manualmente");
      MenuItem m3 = new MenuItem("Eliminar del XMI");
      m3.setOnAction(event->{
        Diagrama diagrama = row.getItem();
        Diagram diagram = xmi.getDiagramByID(diagrama.getId());
        diagramasGen_tb.getItems().remove(diagrama);
        proyecto.diagramas.remove(diagrama);
        xmi.diagram_list.remove(diagram);
      });
      contextMenu.getItems().addAll(m1,m3);
      row.emptyProperty().addListener((obs,wasEmpty,isNowEmpty)->{
        if (isNowEmpty){
          row.setContextMenu(null);
//          row.setText(null);
        }else row.setContextMenu(contextMenu);
      });
      return row;
    });
    diagramasGen_tb.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Diagrama> observable, Diagrama oldValue, Diagrama newValue) -> {
      if(newValue==null) return;
      Diagram d = xmi.getDiagramByID(newValue.getId());
      diagram_view.getChildren().clear();
      diagram_view.setModels(xmi, d);
      diagram_view.draw();
    });
    dgenNombre_col.setCellValueFactory(new PropertyValueFactory<>("nombre"));
    dgenGen_col.setCellValueFactory(new PropertyValueFactory<>("generadoPor"));
    //Implementación
    productosImp_box.setSpacing(20);
    productosImp_box.setPadding(new Insets(10));
    productosImp_scroll.setContent(productosImp_box);
    productosImp_box.prefWidthProperty().bind(productosImp_scroll.widthProperty().subtract(20));
    //Pruebas
    cancelarPrueba(null);
    testId_col.setCellValueFactory(new PropertyValueFactory<>("id"));
    testCond_col.setCellValueFactory(new PropertyValueFactory<>("condicion"));
    testDesc_col.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
    testReq_col.setCellValueFactory(new PropertyValueFactory<>("requisitos"));
    testIn_col.setCellValueFactory(new PropertyValueFactory<>("entrada"));
    testOut_col.setCellValueFactory(new PropertyValueFactory<>("salida"));
    testImp_col.setCellValueFactory(new PropertyValueFactory<>("implementacion"));
    testId_col.setSortType(TableColumn.SortType.ASCENDING);
    test_tb.getSortOrder().add(testId_col);
    //General
    tabs.setDisable(true);
  }
  /**
   * Tarea que se encarga de la exportación
   */
  public class Exporter extends Task<Boolean>{
    private final LogHandler logHandler = new LogHandler();
    private final long TIME = 50;
    private DocumentoHTML doc;
    private TextArea textarea;
    private String filename;
    Predicate<Condicionable> dom_pre, app_pre;
    Predicate<Requerimiento> objPre, featuresPre, reqsPre;
    public Exporter(){
      dom_pre = (Condicionable c) -> c.getCondicion().equals("base");
      app_pre = (Condicionable c) -> !c.getCondicion().equals("base");
      objPre = (Requerimiento r) -> r.getId().startsWith("G") || r.getId().startsWith("O");
      featuresPre = (Requerimiento r) -> r.getId().startsWith("F") || r.getId().startsWith("C");
      reqsPre = (Requerimiento r) -> r.getId().startsWith("R");
      Logger.getLogger("ConfiguradorTest").setLevel(Level.ALL);
    }
    @Override
    protected Boolean call() {
      try {
        appendMessage("Preparando para exportación");
        updateMessage("Iniciando exportación");
        updateProgress(0, 1);
        esperar(1500);
        updateProgress(2, 100);
        updateMessage("Exportando...");
        appendMessage("Creando documento");
        doc = new DocumentoHTML();
        esperar(TIME);
        updateProgress(4, 100);
        appendMessage("Escribiendo cabeceras");
        doc.writeHead(proyecto.nombre);
        esperar(TIME);
        updateProgress(6, 100);
        appendMessage("Escribiendo portada");
        doc.writeFront(proyecto.info.nombre_largo, exportarApp);
        esperar(TIME);
        updateProgress(8, 100);
        appendMessage("Escribiendo introducción");
        doc.writeTitle("Introducción");
        doc.writeParagraph(proyecto.info.introduccion);
        esperar(TIME);
        updateProgress(10, 100);
        if (exportarApp) {
          Producto p = productos_cbox.getSelectionModel().getSelectedItem();
          if(p==null){
            updateProgress(0, 1);
            updateMessage("Sin productos");
            appendMessage("No se ha encontrado o elegido un producto válido");
            return false;
          }
          appendMessage("Producto a exportar: " + p.nombre);
          appendMessage("El producto tiene " + p.caracteristicas.size() + " características");
          exportarAplicacion(p);
        } else exportarDominio();
        esperar(TIME);
        updateProgress(94, 100);
        updateMessage("Finalizando...");
        appendMessage("Exportando cierre");
        doc.writeFoot();
        esperar(TIME);
        updateProgress(96, 100);
        appendMessage("Creando archivo");
        appendMessage("Ruta: " + filename);
        File f = getFile(filename);
        esperar(TIME);
        updateProgress(98, 100);
        appendMessage("Escribiendo en archivo");
        boolean exito = doc.toFile(f);
        appendMessage(exito ? "Exportado correctamente" : "Error al exportar a la ruta especificada");
        esperar(TIME);
        updateProgress(1, 1);
        updateMessage("¡Completado!");
        appendMessage("Proceso terminado");
        return true;
      } catch (Exception ex) {
        updateProgress(0, 1);
        Logger.getLogger("Configurador").log(Level.SEVERE,"Error de exportación",ex);
        updateMessage("Error de exportación");
        appendMessage(ex.toString());
        appendMessage(ex.getMessage());
//        throw ex;
        return false;
      }
    }
    /**
     * Exporta documentación de ingeniería de dominio
     */
    private void exportarDominio(){
      appendMessage("Se exportará la documentación de dominio");
      filename = "lps_"+proyecto.nombre+"_dominio.html";
      //CARACTERISTICAS
      esperar(TIME);
      updateProgress(12, 100);
      updateMessage("Exportando características");
      appendMessage("Preparando CVL");
      appendMessage("Existen " + proyecto.caracteristicas.lista_caracteristicas.size() + " características en total");
      File cvlfile = getFile("lps_cvl.png");
      appendMessage("Exportando archivo CVL: " + cvlfile.getAbsolutePath());
      Platform.runLater(()->{
        tabs.getSelectionModel().select(1);
        TraductorCVL.vista2image(cvl_view, cvlfile);
      });
      esperar(TIME + 200);
      updateProgress(15, 100);
      appendMessage("Escribiendo imagen");
      doc.writeTitle("Características");
      doc.writeParagraph("En la siguiente figura se muestra el árbol de características CVL correspondiente al dominio:");
      doc.writeImage("lps_cvl.png");
      //REQUERIMIENTOS
      esperar(TIME);
      updateProgress(20, 100);
      if (req_check.isSelected()) {
        updateMessage("Exportando requerimientos...");
        appendMessage("Preparando para requerimientos");
        doc.writeTitle("Fase de Requerimientos");
        doc.writeParagraph("A continuación se describen los requerimientos presentes en la <b>plataforma</b>.");
        //Exportando requerimientos del dominio
        appendMessage("Exportando requerimientos de dominio");
        final StringBuilder sb = new StringBuilder("<span class=\"ns\">Objetivos</span>");
        proyecto.requerimientos.stream().filter(dom_pre).filter(objPre).forEach((r)->{escribirRequerimiento(r, sb);});
        sb.append("\n<span class=\"ns\">Rasgos deseados</span>");
        proyecto.requerimientos.stream().filter(dom_pre).filter(featuresPre).forEach((r)->{escribirRequerimiento(r, sb);});
        sb.append("\n<span class=\"ns\">Requerimientos</span>");
        proyecto.requerimientos.stream().filter(dom_pre).filter(reqsPre).forEach((r)->{escribirRequerimiento(r, sb);});
        doc.writeParagraph(sb.toString());
        //Exportando requerimientos de características
        appendMessage("Agrupando requerimientos por condición de características");
        HashMap<String,List<Requerimiento>> map = new HashMap<>();
        proyecto.requerimientos.stream().filter(app_pre).forEach((r)->{
          if(!map.containsKey(r.getCondicion())) map.put(r.getCondicion(), new ArrayList<>());
          map.get(r.getCondicion()).add(r);
        });
        map.entrySet().stream().forEach((entry)->{
          doc.writeParagraph("A continuación se describen los requerimientos aplicados al cumplirse la condición <b>"+entry.getKey()+"</b>.");
          final StringBuilder sbx = new StringBuilder("<span class=\"ns\">Objetivos</span>");
          entry.getValue().stream().filter(objPre).forEach((r)->{escribirRequerimiento(r, sbx);});
          sbx.append("\n<span class=\"ns\">Rasgos deseados</span>");
          entry.getValue().stream().filter(featuresPre).forEach((r)->{escribirRequerimiento(r, sbx);});
          sbx.append("\n<span class=\"ns\">Requerimientos</span>");
          entry.getValue().stream().filter(reqsPre).forEach((r)->{escribirRequerimiento(r, sbx);});
          doc.writeParagraph(sbx.toString());
        });
      } else appendMessage("Fase de requerimientos saltada");
      //DISEÑO
      esperar(TIME);
      updateProgress(40,100);
      if(dis_check.isSelected()){
        updateMessage("Exportando diseño...");
        appendMessage("Leyendo diagramas utilizados");
        doc.writeTitle("Fase de diseño");
        doc.writeParagraph("Diagramas de clases del dominio");
        proyecto.diagramas.stream().filter((d)-> d.getCondicion()!=null && d.getCondicion().equals("base")).forEach(d->{
          Diagram dx = xmi.getDiagramByID(d.getId());
          String img_name = d.getId()+"_ddc.png";
          File dfile = getFile(img_name);
          appendMessage("Exportando diagrama de clases: " + dfile.getAbsolutePath());
          doc.writeParagraph("Los diagramas que a continuación se muestran son pertenecientes a la arquitectura de referencia. Al implementar un producto estos diagramas se modifican para mostrar el diseño final de la arquitectura de la aplicación o producto.");
          doc.write("<p id=\""+dx.id+"\">");
          doc.write("\t<b>ID de diagrama: </b>"+dx.id+"\n",true,true);
          doc.write("\t<b>Nombre: </b>"+dx.name+"\n",true,true);
          doc.write("</p>");
          Platform.runLater(()->{
            tabs.getSelectionModel().select(4);
            diagram_view.setAndDraw(xmi, dx);
            TraductorProyecto.view2image(diagram_view, dfile);
          });
          doc.writeImage(img_name);
          esperar(TIME);
        });
        doc.writeParagraph("Los diagramas que a continuación se muestran son partes de diseño específicos para una o varias características. "
                + "Los diagramas se unen en algún punto (una clase, relación o paquete) con un diagrama base, "
                + "esto se verá reflejado al exportar la documentación de un producto específico. A continuación los diagramas encontrados:");
        proyecto.diagramas.stream().filter((d)-> d.getCombinableCon()!=null).forEach(d->{
          Diagram dx = xmi.getDiagramByID(d.getId());
          String img_name = d.getId()+"_ddc.png";
          File dfile = getFile(img_name);
          appendMessage("Exportando diagrama de clases: " + dfile.getAbsolutePath());
          doc.write("<p>");
          doc.write("\t<b>ID de diagrama: </b>"+dx.id+"\n",true,true);
          doc.write("\t<b>Nombre: </b>"+dx.name+"\n",true,true);
          doc.write("\t<b>Condición de características: </b>"+d.getCondicion()+"\n",true,true);
          doc.write("\t<b>Se combina con el diagrama con ID: </b><a href=\"#"+d.getCombinableCon()+"\">"+d.getCombinableCon()+"</a>\n",true,true);
          doc.write("</p>");
          Platform.runLater(()->{
            tabs.getSelectionModel().select(4);
            diagram_view.setAndDraw(xmi, dx);
            TraductorProyecto.view2image(diagram_view, dfile);
          });
          doc.writeImage(img_name);
          esperar(TIME);
        });
        Platform.runLater(()->diagram_view.clear());
      } else appendMessage("Fase de diseño saltada");
      //PRUEBAS
      esperar(TIME);
      updateProgress(85, 100);
      if(pru_check.isSelected()){
        updateMessage("Exportando pruebas...");
        appendMessage("Preparando para pruebas");
        doc.writeTitle("Fase de Pruebas");
        doc.writeParagraph("Pruebas pertenecientes al dominio");
        doc.write("<table class=\"tabla\" align=\"center\">");
        doc.write("<tr><th>ID</th><th>Descripción u Objetivo</th><th>Pre-requisitos</th><th>Datos de entrada</th><th>Datos de salida</th></tr>");
        proyecto.pruebas.stream().filter(dom_pre).forEach((p)-> escribirPrueba(p));
        doc.write("</table>\n");
        appendMessage("Agrupando pruebas por condición de características");
        HashMap<String,List<Prueba>> map = new HashMap<>();
        proyecto.pruebas.stream().filter(app_pre).forEach((p)->{
          if(!map.containsKey(p.getCondicion())) map.put(p.getCondicion(), new ArrayList<>());
          map.get(p.getCondicion()).add(p);
        });
        map.entrySet().forEach((entry)->{
          doc.writeParagraph("A continuación se describen las pruebas aplicadas al cumplirse la condición <b>"+entry.getKey()+"</b>.");
          doc.write("<table class=\"tabla\" align=\"center\">");
          doc.write("<tr><th>ID</th><th>Descripción u Objetivo</th><th>Pre-requisitos</th><th>Datos de entrada</th><th>Datos de salida</th></tr>");
          entry.getValue().forEach((p)-> escribirPrueba(p));
          doc.write("</table>\n");
        });
      }else appendMessage("Fase de pruebas saltada");
    }
    /**
     * Exporta documentación de una aplicación: un producto de la LPS
     * @param producto Producto a exportar
     */
    public void exportarAplicacion(Producto producto){
      appendMessage("Se prepara la documentación del producto");
      filename = "lps_"+proyecto.nombre+"_"+producto.nombre+".html";
      //CARACTERISTICAS
      esperar(TIME);
      updateProgress(12, 100);
      updateMessage("Exportando características");
      appendMessage("Preparando CVL");
      appendMessage("Existen " + producto.caracteristicas.size() + " características en total en el producto");
      File cvlfile = getFile(producto.nombre+"_cvl.png");
      appendMessage("Exportando archivo CVL: " + cvlfile.getAbsolutePath());
      Platform.runLater(()->{
        tabs.getSelectionModel().select(2);
        productos_list.getSelectionModel().select(producto);
        TraductorCVL.vista2image(product_view, cvlfile);
      });
      esperar(TIME + 200);
      updateProgress(15, 100);
      appendMessage("Escribiendo imagen");
      doc.writeTitle("Características");
      doc.writeParagraph("En la siguiente figura se muestra el árbol de características CVL correspondiente al producto:");
      doc.writeImage(producto.nombre+"_cvl.png");
      //REQUERIMIENTOS
      esperar(TIME);
      updateProgress(20, 100);
      if (req_check.isSelected()) {
        updateMessage("Exportando requerimientos...");
        appendMessage("Preparando para requerimientos");
        doc.writeTitle("Fase de Requerimientos");
        doc.writeParagraph("A continuación se describen la definición textual de los requerimientos para el producto.");
        Predicado<Requerimiento> predicado = new Predicado<>(producto);
        Comparador<Requerimiento> sorter = new Comparador<>();
        final StringBuilder sb = new StringBuilder("<span class=\"ns\">Objetivos</span>");
        proyecto.requerimientos.stream().filter(objPre).filter(predicado).sorted(sorter).forEach((r)->{escribirRequerimiento(r, sb);});
        sb.append("\n<span class=\"ns\">Rasgos deseados</span>");
        proyecto.requerimientos.stream().filter(featuresPre).filter(predicado).sorted(sorter).forEach((r)->{escribirRequerimiento(r, sb);});
        sb.append("\n<span class=\"ns\">Requerimientos</span>");
        proyecto.requerimientos.stream().filter(reqsPre).filter(predicado).sorted(sorter).forEach((r)->{escribirRequerimiento(r, sb);});
        doc.writeParagraph(sb.toString());
      } else appendMessage("Fase de requerimientos brincada");
      //DISEÑO
      esperar(TIME);
      updateProgress(40,100);
      if(dis_check.isSelected()){
        updateMessage("Exportando diseño...");
        appendMessage("Leyendo diagramas utilizados");
        doc.writeTitle("Fase de diseño");
        doc.writeParagraph("Diagramas de clases de la aplicación");
        //lee diagramas base
        List<Diagrama> futuros = new ArrayList<>();
        proyecto.diagramas.stream().filter((d)-> d.getCondicion()!=null && d.getCondicion().equals("base")).forEach(d->{
          String id_nuevo = producto.nombre+"_"+d.getId();
          appendMessage("Nuevo diagrama a generar: " + id_nuevo);
          //Se verifica si ya hay información diagrama anterior
          boolean generar = true;
          Diagrama diagrama_recovered = proyecto.getDiagramaByID(id_nuevo);
          if(diagrama_recovered!=null && !diagrama_recovered.verSiEsActualizable()){
            appendMessage("Generación saltada de " + id_nuevo+", ya existe uno no actualizable.");
            generar = false;
          }
          if(diagrama_recovered==null){
            diagrama_recovered = new Diagrama();
            diagrama_recovered.setActualizable(true);
            diagrama_recovered.setId(id_nuevo);
            diagrama_recovered.setGeneradoPor(producto.nombre);
            futuros.add(diagrama_recovered);
            diagramasGen_tb.getItems().add(diagrama_recovered);
          }
          if(generar){
            //Actualiza nombre de diagrama
            String nuevo_nombre = "Generado_"+System.currentTimeMillis();
            diagrama_recovered.setNombre(nuevo_nombre);
            //Elimina el diagram anterior si existe
            Diagram posible_nuevo = xmi.getDiagramByID(id_nuevo);
            if(posible_nuevo!=null) xmi.diagram_list.remove(posible_nuevo);
            //Crea nuevo diagram clonando base y lo agrega
            Diagram nuevo_final = xmi.getDiagramByID(d.getId()).clonar();
            nuevo_final.id = id_nuevo;
            nuevo_final.name = nuevo_nombre;
            xmi.diagram_list.add(nuevo_final);
            //Comienza combinación
            Predicado<Diagrama> predicado = new Predicado<>(producto);
            proyecto.diagramas.stream().filter(dx->dx.getCombinableCon()!=null && dx.getCombinableCon().equals(d.getId())).filter(dx->predicado.test(dx)).forEach(dx->{
              appendMessage("Combinando con: " + dx.getId());
              Diagram dy = xmi.getDiagramByID(dx.getId());
              nuevo_final.combineWith(dy,true);
              esperar(TIME/2);
            });
            //Crea imagen y exporta
            String img_name = id_nuevo+"_ddc.png";
            File dfile = getFile(img_name);
            appendMessage("Exportando diagrama de clases: " + dfile.getAbsolutePath());
            Platform.runLater(()->{
              tabs.getSelectionModel().select(4);
              diagram_view.setAndDraw(xmi, nuevo_final);
              TraductorProyecto.view2image(diagram_view, dfile);
            });
            doc.writeImage(img_name);
          }else{
            String img_name = id_nuevo+"_ddc.png";
            doc.writeImage(img_name);
          }
          esperar(TIME);
        });
        proyecto.diagramas.addAll(futuros);
        Platform.runLater(()->diagram_view.clear());
      } else appendMessage("Fase de diseño saltada");
      //PRUEBAS
      esperar(TIME);
      updateProgress(85, 100);
      if(pru_check.isSelected()){
        updateMessage("Exportando pruebas...");
        appendMessage("Preparando para pruebas");
        Predicado<Prueba> predicado = new Predicado<>(producto);
        Comparador<Prueba> sorter = new Comparador<>();
        doc.writeTitle("Fase de Pruebas");
        doc.writeParagraph("Pruebas pertenecientes a la aplicación");
        doc.write("<table class=\"tabla\" align=\"center\">");
        doc.write("<tr><th>ID</th><th>Descripción u Objetivo</th><th>Pre-requisitos</th><th>Datos de entrada</th><th>Datos de salida</th></tr>");
        proyecto.pruebas.stream().filter(predicado).sorted(sorter).forEach((p)-> escribirPrueba(p));
        doc.write("</table>\n");
        doc.writeTitle("Ejecución de Pruebas");
        doc.writeParagraph("Pruebas pertenecientes a la aplicación");
        doc.write("<table class=\"tabla\" align=\"center\">");
        doc.write("<tr><th>ID</th><th>Descripción u Objetivo</th><th>Información de ejecución</th><th>Éxito</th></tr>");
        proyecto.pruebas.stream().filter(predicado).sorted(sorter).forEach((p)-> ejecutarPrueba(p,producto));
        doc.write("</table>\n");
      }else appendMessage("Fase de pruebas saltada");
    }
    public void setTextArea(TextArea ta){
      textarea = ta;
    }
    private void esperar(long time){
      try {
        Thread.sleep(time);
      } catch (InterruptedException ex) {
        Logger.getLogger("Configurador").log(Level.SEVERE, null, ex);
      }
    }
    private void appendMessage(String txt){
      if(textarea!=null){
        textarea.appendText(txt);
        textarea.appendText("\n");
      }
    }
    private File getFile(String filename){
      String name = exportDir.getAbsoluteFile()+File.separator+filename;
      return new File(name);
    }
    private void escribirRequerimiento(Requerimiento r, StringBuilder sb){
      sb.append("\n<span class=\"mono\">").append(r.getId()).append(": </span>").append(r.getTexto());
    }
    private void escribirPrueba(Prueba p){
      doc.write("<tr>");
      doc.write("<td class=\"mono\">"+p.getId()+"</td>");
      doc.write("<td>"+p.getDescripcion()+"</td>");
      doc.write("<td>"+p.getRequisitos()+"</td>");
      doc.write("<td>"+p.getEntrada()+"</td>");
      doc.write("<td>"+p.getSalida()+"</td>");
      doc.write("</tr>");
    }
    private void ejecutarPrueba(Prueba p, Producto producto){
      if(p.getImplementacion().isEmpty()) return;
      appendMessage("Ejecutando prueba: "+p.getImplementacion());
      logHandler.setPrueba(p);
      boolean encontrado = false;
      for(Handler h : Logger.getLogger("ConfiguradorTest").getHandlers()) encontrado = h.toString().equals(logHandler.toString()) ? true : encontrado || false;
      if(!encontrado) Logger.getLogger("ConfiguradorTest").addHandler(logHandler);
      try{
        Class<?> clase = Class.forName(p.getImplementacion());
        Method main = clase.getMethod("main", String[].class);
        String[] params = {producto.paquete,producto.clasePrincipal,"unknown"};
        main.invoke(null, (Object) params);
        if(params[2].equals("success")) p.exito = true;
        if("si".equals("no")) throw new Exception("Nunca seré lanzada, sólo quito un warning.");
      }catch (ClassNotFoundException ex) {
        Logger.getLogger("ConfiguradorTest").log(Level.SEVERE, "No se encontr\u00f3 la clase {0}", p.getImplementacion());
      }catch(NoSuchMethodException ex){
        Logger.getLogger("ConfiguradorTest").log(Level.SEVERE,"La clase no tiene método main, no es posible ejecutar la prueba.");
      }catch(SecurityException ex){
        Logger.getLogger("ConfiguradorTest").log(Level.SEVERE, null,ex);
      }catch(Exception ex){
        Logger.getLogger("ConfiguradorTest").log(Level.SEVERE,"Error inesperado al ejecutar prueba",ex);
      }
      doc.write("<tr>");
      doc.write("<td class=\"mono\">"+p.getId()+"</td>");
      doc.write("<td>"+p.getDescripcion()+"</td>");
      doc.write("<td class=\"mono chico\">"+p.trace.toString()+"</td>",true,true);
      doc.write("<td>"+(p.exito ? "Sí" : "No")+"</td>");
      doc.write("</tr>");
      p.clearTrace();
      esperar(TIME);
    }
    public class Predicado<OBJECT> implements Predicate<OBJECT>{
      private final CondicionParser parser;
      private final Producto producto;
      public Predicado(Producto p){
        producto = p;
        parser = new CondicionParser(producto);
      }
      @Override
      public boolean test(OBJECT obj) {
        if(obj instanceof Condicionable){
          Condicionable c = (Condicionable) obj;
          return parser.evaluar(c.getCondicion());
        }
        return false;
      }
    }//FIN Predicado
    public class Comparador<OBJECT> implements Comparator<OBJECT>{
      @Override
      public int compare(OBJECT o1, OBJECT o2) {
        if(o1 instanceof Requerimiento && o2 instanceof Requerimiento){
//          Requerimiento r1 = (Requerimiento) o1, r2 = (Requerimiento) o2;
//          String s1 = r1.getId().substring(1), s2 = r2.getId().substring(1);
          Float f1 = Float.parseFloat(((Requerimiento)o1).getId().substring(1));
          Float f2 = Float.parseFloat(((Requerimiento)o2).getId().substring(1));
          return f1.compareTo(f2);
        }
        if(o1 instanceof Prueba && o2 instanceof Prueba){
          Float f1 = Float.parseFloat(((Prueba)o1).getId());
          Float f2 = Float.parseFloat(((Prueba)o2).getId());
          return f1.compareTo(f2);
        }
        return o1.toString().compareTo(o2.toString());
      }
    }//FIN Comparador
    public class LogHandler extends Handler{
      private Prueba currentlyTesting;
      private final SimpleFormatter formatter;
      LogHandler(){
        formatter = new SimpleFormatter();
        setFormatter(formatter);
      }
      public void setPrueba(Prueba p){
        currentlyTesting = p;
      }
      @Override
      public void publish(LogRecord record) {
        currentlyTesting.trace.append(formatter.format(record));
      }
      @Override public void flush() {/*DO NOTHING*/}
      @Override public void close() throws SecurityException {/*DO NOTHING*/}
      @Override public String toString(){return "LogHandler";}
    }
  }//FIN Exporter
}//FIN PrincipalController