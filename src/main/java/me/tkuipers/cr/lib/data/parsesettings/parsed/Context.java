package me.tkuipers.cr.lib.data.parsesettings.parsed;

import java.util.List;

public class Context {

  private String name;
  private Type type;
  private String regex;
  private List<Style> styles;
  //I removed includes here in order to stick it into contexts
  private List<Context> contexts;

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String getRegex() {
    return regex;
  }

  public void setRegex(String regex) {
    this.regex = regex;
  }

  public List<Style> getStyles() {
    return styles;
  }

  public void setStyles(List<Style> styles) {
    this.styles = styles;
  }

  public List<Context> getContexts() {
    return contexts;
  }

  public void setContexts(List<Context> contexts) {
    this.contexts = contexts;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
