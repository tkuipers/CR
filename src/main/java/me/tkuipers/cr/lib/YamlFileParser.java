package me.tkuipers.cr.lib;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import me.tkuipers.cr.lib.data.filebacked.CRSettings;
import me.tkuipers.cr.lib.data.parsed.Settings;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class YamlFileParser {

  private URL syntaxFile;
  private Settings settings;
  private CRSettings crSettings;
  private ObjectMapper mapper;

  public YamlFileParser(URL syntaxFile) throws IOException {
    this.mapper = new ObjectMapper(new YAMLFactory());
    this.syntaxFile = syntaxFile;
    this.crSettings = mapper.readValue(syntaxFile, CRSettings.class);
    this.settings = parseToSettings();
  }

  public Settings parseToSettings() {
    System.out.println(crSettings);
    var settings = new Settings();

    return settings;
  }


}
