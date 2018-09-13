package me.tkuipers.cr.lib.file.parser;

import me.tkuipers.cr.lib.data.parsesettings.parsed.Style;

import java.util.Collection;
import java.util.List;

public class StyledString {
  private String stringValue;
  private List<Style> styleValue;

  public String getStringValue() {
    return stringValue;
  }

  public void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }

  public List<Style> getStyles() {
    return styleValue;
  }

  public void setStyles(List<Style> styleValue) {
    this.styleValue = styleValue;
  }

  @Override
  public String toString() {
    return "StyledString{" +
          "stringValue='" + stringValue + '\'' +
          ", styleValue=" + styleValue +
          '}';
  }
}
