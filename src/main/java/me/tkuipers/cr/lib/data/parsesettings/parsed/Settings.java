package me.tkuipers.cr.lib.data.parsesettings.parsed;

import me.tkuipers.cr.lib.file.parser.file.regexHandler.RegularExpressionCombiner;

import java.util.List;

public class Settings implements IContextContainer {
  private String name;
  private List<String> fileExtensions;
  private List<Context> contexts;
  private List<Style> styles;
  private String combinedRegex;

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

  @Override
  public String getContextsCombinedRegex() {
    return combinedRegex;
  }

  public void setContexts(List<Context> contexts) {
    this.contexts = contexts;
    combinedRegex = RegularExpressionCombiner.combineContexts(contexts);
  }

  public List<Style> getStyles() {
    return styles;
  }

  @Override
  public List<Style> getInheritedStyles() {
    return null;
  }

  @Override
  public Type getType() {
    return Type.MAIN;
  }

  public void setStyles(List<Style> styles) {
    this.styles = styles;
  }
}
