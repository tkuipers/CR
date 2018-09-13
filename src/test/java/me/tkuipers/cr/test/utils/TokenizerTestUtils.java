package me.tkuipers.cr.test.utils;

import com.google.common.collect.Lists;
import me.tkuipers.cr.lib.data.parsesettings.filebacked.CRContext;
import me.tkuipers.cr.lib.data.parsesettings.filebacked.CRSettings;
import me.tkuipers.cr.lib.data.parsesettings.filebacked.CRStyle;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Type;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class TokenizerTestUtils {

  public static CRSettings genSimpleSettings(){
    var mainContext = genCRContext();
    var mainStyle = genCRStyle();



    var settings = new CRSettings();
    settings.setName("json");
    settings.setFileExtensions(Lists.newArrayList(".json"));
    settings.setStyles(Lists.newArrayList(mainStyle));
    settings.setContexts(Lists.newArrayList(mainContext));

    return settings;
  }

  public static CRContext genCRContext(){
    var mainContext = new CRContext();
    mainContext.setRegex("\\{");
    mainContext.setType(Type.PATTERN);
    mainContext.setInclude(new ArrayList());
    mainContext.setStyles(Lists.newArrayList("mainStyle"));
    mainContext.setName("main");
    return mainContext;
  }

  public static CRStyle genCRStyle(){
    var mainStyle = new CRStyle();

    mainStyle.setBackgrounColor("#000000");
    mainStyle.setColor("#000000");
    mainStyle.setName("mainStyle");

    return mainStyle;
  }

  public static File buildRandomTempFolder(){
    var uuid = UUID.randomUUID();
    var folderPath = "/tmp/" + uuid;
    var folder = new File(folderPath);
    folder.mkdirs();
    return folder;
  }
}
