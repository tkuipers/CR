package me.tkuipers.cr.lib.data.parsesettings.parsed;

import java.util.List;

public interface IContextContainer {
  List<Context> getContexts();
  String getContextsCombinedRegex();
  List<Style> getStyles();
}
