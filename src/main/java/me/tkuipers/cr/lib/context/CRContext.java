package me.tkuipers.cr.lib.context;

import java.util.List;

public class CRContext {
  CRType type;
  String regex;
  List<String> styles;
  List<String> include;

  public CRType getType() {
    return type;
  }

  public void setType(CRType type) {
    this.type = type;
  }

  public String getRegex() {
    return regex;
  }

  public void setRegex(String regex) {
    this.regex = regex;
  }

  public List<String> getStyles() {
    return styles;
  }

  public void setStyles(List<String> styles) {
    this.styles = styles;
  }

  public List<String> getInclude() {
    return include;
  }

  public void setInclude(List<String> include) {
    this.include = include;
  }
}
