package me.tkuipers.cr.lib.data.filebacked;

public class CRStyle {
  private String name;
  private String color;
  private String backgrounColor;

  public CRStyle(){}

  public CRStyle(CRStyle style){
    this.name = style.name;
    this.color = style.color;
    this.backgrounColor = style.backgrounColor;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getBackgrounColor() {
    return backgrounColor;
  }

  public void setBackgrounColor(String backgrounColor) {
    this.backgrounColor = backgrounColor;
  }

  public CRStyle clone(){ return new CRStyle(this); }

  @Override
  public String toString() {
    return "CRStyle{" +
          "name='" + name + '\'' +
          ", color='" + color + '\'' +
          ", backgrounColor='" + backgrounColor + '\'' +
          '}';
  }
}
