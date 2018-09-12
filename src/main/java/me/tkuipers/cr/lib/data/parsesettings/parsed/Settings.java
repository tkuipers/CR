package me.tkuipers.cr.lib.data.parsesettings.parsed;

import java.util.List;

public class Settings implements IContextContainer {
  private String name;
  private List<String> fileExtensions;
  private List<Context> contexts;
  private List<Style> styles;

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

  public List<Context> getContexts() {
    return contexts;
  }

  public void setContexts(List<Context> contexts) {
    this.contexts = contexts;
  }

  public List<Style> getStyles() {
    return styles;
  }

  public void setStyles(List<Style> styles) {
    this.styles = styles;
  }
}
