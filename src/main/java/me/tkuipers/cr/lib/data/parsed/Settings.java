package me.tkuipers.cr.lib.data.parsed;

import me.tkuipers.cr.lib.data.filebacked.CRContext;
import me.tkuipers.cr.lib.data.filebacked.CRStyle;

import java.util.List;

public class Settings {
  private String name;
  private List<String> fileExtensions;
  private List<CRContext> contexts;
  private List<CRStyle> styles;

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
