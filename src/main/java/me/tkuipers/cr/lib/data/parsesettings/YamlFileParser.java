package me.tkuipers.cr.lib.data.parsesettings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.util.Pair;
import me.tkuipers.cr.lib.data.parsesettings.filebacked.CRContext;
import me.tkuipers.cr.lib.data.parsesettings.filebacked.CRSettings;
import me.tkuipers.cr.lib.data.parsesettings.filebacked.CRStyle;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Context;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Settings;
import me.tkuipers.cr.lib.data.parsesettings.exceptions.SyntaxParseException;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Style;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Type;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

public class YamlFileParser {

  private Settings settings;
  private CRSettings crSettings;
  private ObjectMapper mapper;
  private Map<String, CRContext> contextMap;
  private Map<String, CRStyle> styleMap;
  private List<Pair<String, String>> recursionList;

  public YamlFileParser(URL syntaxFile) throws IOException {
    this.mapper = new ObjectMapper(new YAMLFactory());
    this.crSettings = mapper.readValue(syntaxFile, CRSettings.class);
    this.contextMap = Maps.newHashMap();
    this.recursionList = Lists.newArrayList();
  }

  public YamlFileParser(CRSettings settings) throws IOException {
    this.crSettings = settings;
    this.contextMap = Maps.newHashMap();
    this.recursionList = Lists.newArrayList();
  }

  public boolean shouldParse(String filePath){
    String pathWithDot = "." + FilenameUtils.getExtension(filePath);
    if(filePath.equals(pathWithDot)){
      return false;
    }
    return crSettings.getFileExtensions().contains(pathWithDot);
  }

  public Settings getSettings(){
    return settings;
  }

  public void build() {
    contextMap = populateContextMap(crSettings.getContexts());
    styleMap = populateStyleMap(crSettings.getStyles());
    var settings = new Settings();
    settings.setName(crSettings.getName());
    settings.setFileExtensions(crSettings.getFileExtensions());
    settings.setContexts(buildContexts(crSettings.getContexts(), null));
    settings.setStyles(styleMap.values().stream().map(m -> buildStyle(m)).collect(Collectors.toList()));

    var mainCRContext = contextMap.get("main");
    settings.setStyles(newArrayList(buildStyle(styleMap.get(mainCRContext.getStyles().get(0)))));

    settings.setStyles(newArrayList(mainCRContext.getStyles().stream().map(m -> buildStyle(styleMap.get(m))).collect(Collectors.toList())));

    //var mainContext = settings.getContexts().stream().filter(m -> m.getType() == Type.MAIN).findFirst();
    this.settings = settings;

    //detect the loop
    //add it to post procesing using impl and ID
    handleRecursions();
    setOrderOfContexts(settings.getContexts());
  }

  private void setOrderOfContexts(List<Context> contexts) {
    Collections.sort(contexts, Comparator.comparingInt(Context::getPriority));
  }

  private void handleRecursions() {
    for(var pair : recursionList){
      var parent = findContext(pair.getKey(), settings.getContexts());
      var child = findContext(pair.getValue(), settings.getContexts());
      addChild(parent, child);
    }
  }

  private void addChild(Context parent, Context child) {
    if(parent != null){
      if(child != null) {
        for(var grandChildContext : child.getContexts()){
          parent.addContext(grandChildContext);
        }
      }
    }
  }

  private Context findContext(String contextName, List<Context> contexts){
    for(var context : contexts){
      if(context.getName().equals(contextName)){
        return context;
      }
    }
    return findContext(contextName, contexts.stream().flatMap(m -> m.getContexts().stream()).collect(Collectors.toList()));
  }


  private Map<String, CRStyle> populateStyleMap(List<CRStyle> styles) {
    Map<String, CRStyle> map = Maps.newHashMap();
    for(var style : styles){
      if(!map.containsKey(style.getName())){
        map.put(style.getName(), style);
      }
      else{
        throw new SyntaxParseException("Could not parse file as there are multiple styles with the same name: " + style.getName());
      }
    }
    return map;
  }

  private Map<String, CRContext> populateContextMap(List<CRContext> conList) {
    Map<String, CRContext> map = Maps.newHashMap();
    for(var context : conList){
      addToMap(map, context);
    }
    return map;
  }

  private void addToMap(Map<String, CRContext> map, CRContext context) {
    if(!map.containsKey(context.getName())) {
      map.put(context.getName(), context);
      if(context.getContexts().size() != 0){
        map.putAll(populateContextMap(context.getContexts()));
      }
    } else{
      throw new SyntaxParseException("Could not parse file as there are multiple contexts with the same name: " + context.getName());
    }
  }

  private List<Context> buildContexts(List<CRContext> conList, Context parentContext) {
    List<Context>newList = Lists.newArrayList();
    addNestedContexts(parentContext, newList, conList);
    return newList;
  }

  private Context buildContext(CRContext con, Context parentContext, int priority) {
    var context = new Context();
    context.setPriority(priority);
    context.setName(con.getName());
    context.setRegex(con.getRegex());
    context.setType(con.getType());
    context.setParent(parentContext);
    context.setStyles(buildStyles(con));
    context.setContexts(buildContexts(con, context));


    validateContext(context);
    addContextDefaults(context);
    return context;
  }

  private void addContextDefaults(Context context) {
    if(context.getType() == Type.INLINE_PUSH){
      List<Style> inherStyles = Lists.newArrayList();
      if(context.getParent() != null){
        inherStyles.addAll(context.getParent().getStyles().stream().map(m -> {
          if(m != null){
            return m.clone();
          }
          return null;
        }).collect(Collectors.toList()));
        if(inherStyles.size() != 0){
          context.setInheritedStyles(inherStyles);
        }
        else{
          context.setInheritedStyles(context.getStyles());
        }

      }

    }

    if(context.getType() == Type.INLINE_PUSH || context.getType() == Type.MULTILINE_PUSH){
      for(var indContext : context.getContexts()){
        if(indContext.getType() == Type.POP){
          indContext.setStyles(context.getStyles());
        }
      }
    }
  }

  private Context getNewlinePop(){
    var newlinePop = new Context();
    newlinePop.setName("New line pop");
    newlinePop.setRegex("(\\r\\n|\\r|\\n)");
    newlinePop.setType(Type.POP);
    return newlinePop;
  }

  private void validateContext(Context context) {
    if(context.getType() == Type.POP && context.getContexts().size() != 0){
      throw new SyntaxParseException("Cannot have an inner context to a POP context type");
    }
  }

  private List<Style> buildStyles(CRContext con) {
    List<Style> list = newArrayList();

    for(var crStyle : con.getStyles()){
      if(styleMap.containsKey(crStyle)){
        list.add(buildStyle(styleMap.get(crStyle)));
      }
      else{
        throw new SyntaxParseException("Found unexpected style to include: " + crStyle);
      }
    }

    return list;
  }

  private Style buildStyle(CRStyle crStyle) {
    var style = new Style();
    style.setName(crStyle.getName());
    style.setColor(crStyle.getColor());
    style.setBackgrounColor(crStyle.getBackgrounColor());
    return style;
  }

  private List<Context> buildContexts(CRContext con, Context parentContext) {
    List<Context> list = newArrayList();
    addNestedContexts(parentContext, list, con.getContexts());
    for(int i = 0; i < con.getInclude().size(); i++ ){
      handleInclude(con, parentContext, list, i);
    }
    return list;
  }

  private void handleInclude(CRContext con, Context parentContext, List<Context> list, int i) {
    var include = con.getInclude().get(i);
    if(contextMap.containsKey(include)){
      CRContext context = contextMap.get(include);
      if(!recursionInContext(context, parentContext)) {
        includeContextWithNoRecursion(con, parentContext, list, i, context);
      }
      else{
        recursionList.add(new Pair<>(con.getName(), include));
      }
    }
    else{
      throw new SyntaxParseException("Found unexpected context type to include: " + include);
    }
  }

  private void includeContextWithNoRecursion(CRContext con, Context parentContext, List<Context> list, int i, CRContext context) {
    var newContext = buildContext(context, parentContext, con.getContexts().size()+i);
    list.addAll(newContext.getContexts());
  }

  private void addNestedContexts(Context parentContext, List<Context> list, List<CRContext> contexts) {
    for (int i = 0; i < contexts.size(); i++) {
      list.add(buildContext(contexts.get(i), parentContext, i));
    }
  }

  private boolean recursionInContext(CRContext context, Context parentContext) {
    Context curContext = parentContext;
    while(curContext != null){
      if(curContext.getName().equals(context.getName())){
        return true;
      }
      curContext = curContext.getParent();
    }
    return false;
  }
}
