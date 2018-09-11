package me.tkuipers.cr.lib.data.filebacked;

import com.google.common.collect.Lists;
import me.tkuipers.cr.lib.data.parsed.Type;

import java.util.List;
import java.util.stream.Collectors;

public class CRContext {
  private String name;
  private Type type;
  private String regex;
  private List<String> styles;
  private List<String> include;
  private List<CRContext> contexts;

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

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
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

  public String getName() { return name; }

  public void setName(String name) { this.name = name; }

  public CRContext clone(){ return new CRContext(this); }

  @Override
  public String toString() {
    return "CRContext{" +
          "name='" + name + '\'' +
          ", type=" + type +
          ", regex='" + regex + '\'' +
          ", styles=" + styles +
          ", include=" + include +
          ", contexts=" + contexts +
          '}';
  }

}
