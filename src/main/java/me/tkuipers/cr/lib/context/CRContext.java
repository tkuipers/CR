package me.tkuipers.cr.lib.context;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

public class CRContext {
  CRType type;
  String regex;
  List<String> styles;
  List<String> include;
  List<CRContext> contexts;

  public CRContext(){
    styles = Lists.newArrayList();
    include = Lists.newArrayList();
    contexts = Lists.newArrayList();
  }

  public CRContext(CRContext context){
    this.type = context.type;
    this.regex = context.regex;
    styles = Lists.newArrayList();
    include = Lists.newArrayList();
    contexts = Lists.newArrayList();
    this.styles.addAll(context.styles);
    this.include.addAll(context.include);
    this.contexts.addAll(context.contexts.stream().map(m -> m.clone()).collect(Collectors.toList()));
  }

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

  public List<CRContext> getContexts() { return contexts; }

  public void setContexts(List<CRContext> contexts) { this.contexts = contexts; }

  public CRContext clone(){ return new CRContext(this); }
}
