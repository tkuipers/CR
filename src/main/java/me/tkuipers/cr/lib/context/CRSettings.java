package me.tkuipers.cr.lib.context;

import java.util.List;

public class CRSettings {

  String name;
  List<String> fileExtensions;
  List<CRContext> contexts;
  List<CRStyle> styles;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getFileExtensions() {
    return fileExtensions;
  }

  public void setFileExtensions(List<String> fileExtensions) {
    this.fileExtensions = fileExtensions;
  }

  public List<CRContext> getContexts() {
    return contexts;
  }

  public void setContexts(List<CRContext> contexts) {
    this.contexts = contexts;
  }

  public List<CRStyle> getStyles() {
    return styles;
  }

  public void setStyles(List<CRStyle> styles) {
    this.styles = styles;
  }
}