package me.tkuipers.cr.lib.data.parsed;

import me.tkuipers.cr.lib.data.filebacked.CRContext;
import me.tkuipers.cr.lib.data.filebacked.CRType;

import java.util.List;

public class Context {

  private CRType type;
  private String regex;
  private List<Style> styles;
  //I removed includes here in order to stick it into contexts
  private List<CRContext> contexts;
}
