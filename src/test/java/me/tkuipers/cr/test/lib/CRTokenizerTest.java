package me.tkuipers.cr.test.lib;

import me.tkuipers.cr.lib.YamlFileParser;
import org.junit.Test;

import java.io.IOException;

public class CRTokenizerTest {

  @Test
  public void test1() throws IOException {
    ClassLoader cl = this.getClass().getClassLoader();
    new YamlFileParser(cl.getResource("ExampleSyntaxFile.yml"));
  }


}
