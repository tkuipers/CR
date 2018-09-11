package me.tkuipers.cr.test.utils;

import com.google.common.collect.Lists;
import me.tkuipers.cr.lib.data.filebacked.CRContext;
import me.tkuipers.cr.lib.data.filebacked.CRSettings;
import me.tkuipers.cr.lib.data.filebacked.CRStyle;
import me.tkuipers.cr.lib.data.filebacked.CRType;

import java.util.ArrayList;

public class TokenizerTestUtils {

  public static CRSettings genSimpleSettings(){

    var mainContext = new CRContext();
    mainContext.setRegex("\\{");
    mainContext.setType(CRType.PATTERN);
    mainContext.setInclude(new ArrayList());
    mainContext.setStyles(Lists.newArrayList("mainStyle"));

    var mainStyle = new CRStyle();

    mainStyle.setBackgrounColor("#000000");
    mainStyle.setColor("#000000");
    mainStyle.setName("mainStyle");

    var settings = new CRSettings();
    settings.setName("json");
    settings.setFileExtensions(Lists.newArrayList(".json"));

    settings.setStyles(Lists.newArrayList(mainStyle));

    settings.setContexts(Lists.newArrayList(mainContext));

    return settings;
  }
}
