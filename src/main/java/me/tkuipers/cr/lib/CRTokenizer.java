package me.tkuipers.cr.lib;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import me.tkuipers.cr.lib.context.CRContext;
import me.tkuipers.cr.lib.context.CRSettings;
import me.tkuipers.cr.lib.context.CRStyle;
import me.tkuipers.cr.lib.context.CRType;

import java.io.IOException;
import java.util.ArrayList;

public class CRTokenizer {

  public CRTokenizer() throws IOException {
    var mapper = new ObjectMapper(new YAMLFactory());

    var mainContext = new CRContext();
    mainContext.setRegex("\\{");
    mainContext.setType(CRType.INLINE_PUSH);
    mainContext.setInclude(new ArrayList());
    mainContext.setStyles(new ArrayList<String>(){{
      add("mainStyle");
    }});

    var mainStyle = new CRStyle();

    mainStyle.setBackgrounColor("#000000");
    mainStyle.setColor("#000000");
    mainStyle.setName("mainStyle");

    var settings = new CRSettings();
    settings.setName("json");
    settings.setFileExtensions(new ArrayList<String>(){{
      add(".json");
    }});

    settings.setStyles(new ArrayList<CRStyle>(){{
      add(mainStyle);
    }});

    settings.setContexts(new ArrayList<CRContext>(){{
      add(mainContext);
    }});

    var s = mapper.writeValueAsString(settings);

    System.out.println("Thing");
    System.out.println(s);

  }



}
