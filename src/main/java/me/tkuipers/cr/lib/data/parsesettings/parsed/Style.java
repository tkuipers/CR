package me.tkuipers.cr.lib.data.parsesettings.parsed;

public class Style {
  private String name;
  private String color;
  private String backgrounColor;
  private boolean hasSpecialCapability;

  public Style(){}

  public Style(Style other){
    this.name = other.name;
    this.color = other.color;
    this.backgrounColor = other.backgrounColor;
    this.hasSpecialCapability = other.hasSpecialCapability;
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

  public boolean isHasSpecialCapability() {
    return hasSpecialCapability;
  }

  public void setHasSpecialCapability(boolean hasSpecialCapability) {
    this.hasSpecialCapability = hasSpecialCapability;
  }

  public Style clone(){
    return new Style(this);

  }

  @Override
  public String toString() {
    return "Style{" +
          "name='" + name + '\'' +
          ", color='" + color + '\'' +
          ", backgrounColor='" + backgrounColor + '\'' +
          ", hasSpecialCapability=" + hasSpecialCapability +
          '}';
  }
}
