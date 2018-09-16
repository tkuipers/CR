package me.tkuipers.cr.test.lib.fileparser;

import com.google.common.collect.Lists;
import me.tkuipers.cr.lib.data.parsesettings.YamlFileParser;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Style;
import me.tkuipers.cr.lib.file.parser.StyledString;
import me.tkuipers.cr.lib.file.parser.file.FileParser;
import me.tkuipers.cr.lib.file.parser.tokentranslator.HTMLTokenTranslator;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class HTMLTokenTranslatorTests {
  HTMLTokenTranslator translator;
  Style style1;
  Style style2;
  StyledString styleString1;
  StyledString styleString2;

  @Before
  public void setUp(){
    translator = new HTMLTokenTranslator();

    style1 = new Style();
    style1.setBackgrounColor("#ffffff");
    style1.setColor("#D30C7B");
    style1.setName("style1");

    style2 = new Style();
    style2.setBackgrounColor("#ffffff");
    style2.setColor("#3FC180");
    style2.setName("style2");

    styleString1 = new StyledString();
    styleString1.setStringValue("string1");
    styleString1.setStyles(Lists.newArrayList(style1));

    styleString2 = new StyledString();
    styleString2.setStringValue("string2");
    styleString2.setStyles(Lists.newArrayList(style2));

  }

  @Test
  public void testBasicStyleStringIsGeneratedCorrectly(){

    var styleString3 = new StyledString();
    styleString3.setStyles(Lists.newArrayList(style1));
    styleString3.setStringValue("string3");

    List<StyledString> testList = Lists.newArrayList(styleString1, styleString2, styleString3);

    var out = translator.getStringFromToken(testList);
  }



}
