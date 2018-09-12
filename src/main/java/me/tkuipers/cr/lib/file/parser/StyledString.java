package me.tkuipers.cr.lib.file.parser;

import me.tkuipers.cr.lib.data.parsesettings.parsed.Style;

public class StyledString {
  private String stringValue;
  private Style styleValue;

  public String getStringValue() {
    return stringValue;
  }

  public void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }

  public Style getStyleValue() {
    return styleValue;
  }

  public void setStyleValue(Style styleValue) {
    this.styleValue = styleValue;
  }
}
