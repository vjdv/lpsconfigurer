package com.intelidomo.configurador.modelos;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jassiel
 */
public class CondicionParser {
  private final List<String> caracteristicas = new ArrayList<>();
  public CondicionParser(List<String> list){
    list.forEach((s)->{caracteristicas.add(s.toLowerCase());});
  }
  public CondicionParser(Producto p){
    this(p.caracteristicas);
  }
  public boolean evaluar(String condicion){
    return new Expresion(condicion.toLowerCase()).evaluar();
  }
  public class Expresion{
    private final String expresion;
    public Expresion(String exp){
      expresion = exp.trim();
    }
    public boolean evaluar(){
      if(expresion.contains("(") || expresion.contains(")")){
        
      }else if(expresion.contains("&")){
        String exps[] = expresion.split("&");
        boolean b = true;
        for(String exp : exps){
          Expresion e = new Expresion(exp);
          b = b && e.evaluar();
        }
        return b;
      }else if(expresion.contains("|")){
        String exps[] = expresion.split("\\|");
        boolean b = false;
        for(String exp : exps){
          Expresion e = new Expresion(exp);
          b = b || e.evaluar();
        }
        return b;
      }else{
        return caracteristicas.contains(expresion) || expresion.equals("base");
      }
      return false;
    }
  }
//  private static boolean evaluate(String left, String op, String right) {
//    switch (op) {
//      case "==":
//        return left.equals(right);
//      case "!=":
//        return !left.equals(right);
//      default:
//        System.err.println("ERROR: Operator type not recognized.");
//        return false;
//    }
//  }
}