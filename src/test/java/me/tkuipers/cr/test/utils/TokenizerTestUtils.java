package me.tkuipers.cr.test.utils;

import com.google.common.collect.Lists;
import me.tkuipers.cr.lib.data.parsesettings.filebacked.CRContext;
import me.tkuipers.cr.lib.data.parsesettings.filebacked.CRSettings;
import me.tkuipers.cr.lib.data.parsesettings.filebacked.CRStyle;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Type;

import java.util.ArrayList;

public class TokenizerTestUtils {

  public static CRSettings genSimpleSettings(){

    var mainContext = new CRContext();
    mainContext.setRegex("\\{");
    mainContext.setType(Type.PATTERN);
    mainContext.setInclude(new ArrayList());
    mainContext.setStyles(Lists.newArrayList("mainStyle"));
    mainContext.setName("main");

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
