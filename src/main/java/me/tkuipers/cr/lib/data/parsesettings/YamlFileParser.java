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
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
  }

  private void handleRecursions() {
    System.out.println("RecursionList: " + recursionList);
    for(var pair : recursionList){
      var parent = findContext(pair.getKey(), settings.getContexts());
      var child = settings.getContexts().stream().filter(m -> m.getName().equals(pair.getValue())).findFirst();
      System.out.println("checking the thing");
      System.out.println("parent is present: " + (parent != null));
      System.out.println("child is present: " + child.isPresent());
      if(parent != null){
        if(child.isPresent()){
          for(var grandChildContext : child.get().getContexts()){
            parent.addContext(grandChildContext);
          }

        }
      }
    }

  }

  private Context findContext(String contextName, List<Context> contexts){
    Context out = null;
    for(var context :contexts){
      System.out.println("Comparing: " + contextName + " to " + context.getName());
      if(context.getName().equals(contextName)){
        out =  context;
        break;
      }

      List<Context> children = Lists.newArrayList();
      for(var childCon : context.getContexts()){
        children.add(childCon);
      }

      out = findContext(contextName, children);
      if(out != null){
        break;
      }
    }
    return out;
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
    return conList.stream().map(con -> buildContext(con, parentContext)).collect(Collectors.toList());
  }

  private Context buildContext(CRContext con, Context parentContext) {
    var context = new Context();
    context.setName(con.getName());
    context.setRegex(con.getRegex());
    context.setType(con.getType());
    context.setParent(parentContext);
    context.setContexts(buildContexts(con, context));
    context.setStyles(buildStyles(con));

    validateContext(context);
    addContextDefaults(context);
    return context;
  }

  private void addContextDefaults(Context context) {
    if(context.getType() == Type.INLINE_PUSH){
      context.addContext(getNewlinePop());
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
    for(var crContext : con.getContexts()){
      list.add(buildContext(crContext, parentContext));
    }
    for(var include : con.getInclude()){
      if(contextMap.containsKey(include)){
        CRContext context = contextMap.get(include);
        if(!recursionInContext(context, parentContext)) {
          var newContext = buildContext(context, parentContext);
          list.addAll(newContext.getContexts());
        }
        else{
          recursionList.add(new Pair<>(con.getName(), include));
        }
      }
      else{
        throw new SyntaxParseException("Found unexpected context type to include: " + include);
      }
    }
    return list;
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
