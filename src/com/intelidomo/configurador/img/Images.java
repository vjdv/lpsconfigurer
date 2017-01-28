package com.intelidomo.configurador.img;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Jassiel
 */
public class Images {
  private static Images singleton;
  private Images(){}
  public static Images getInstance(){
    if(singleton==null) singleton = new Images();
    return singleton;
  }
  private final Map<String,Image> images_map = new HashMap<>();
  public Image getImage(String name){
    Image img = images_map.get(name);
    if(img==null){
      img = new Image(getClass().getResourceAsStream(name));
      images_map.put(name, img);
    }
    return img;
  }
  public ImageView getIcon(String name){
    return new ImageView(getImage(name));
  }
}