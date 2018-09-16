package me.tkuipers.cr.lib.file.parser.tokentranslator;

import me.tkuipers.cr.lib.data.parsesettings.parsed.Style;
import me.tkuipers.cr.lib.file.parser.StyledString;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HTMLTokenTranslator implements TokenTranslator {

  @Override
  public String getStringFromToken(List<StyledString> stringList) {
    var string = new StringBuilder();
    string.append("<html>");
    var allPossibleStyles = getAllStyles(stringList).values();
    string.append(buildHtmlStyleSection(allPossibleStyles));
    string.append("</html>");
    string.append(buildHtmlBodySection(stringList));
    return string.toString();
  }

  private String buildHtmlBodySection(List<StyledString> stringList) {
    var builder = new StringBuilder();
    builder.append("<body>");
    for(var string : stringList){
      String allStyles = combineStyleNames(string.getStyles());
      builder.append("<span class=" + allStyles + ">" + string.getStringValue().replaceAll("\n", "<br/>").replaceAll("\t", "&emsp;").replaceAll(" ", "&nbsp;") + "</span>");
    }
    builder.append("</body>");
    return builder.toString();
  }

  private String combineStyleNames(List<Style> styles) {
    var builder = new StringBuilder();
    styles.forEach(s -> builder.append(" " + s.getName()));
    return builder.toString();
  }

  private Map<String, Style> getAllStyles(List<StyledString> stringList) {
    return stringList.stream()
          .flatMap(styledString-> styledString.getStyles().stream())
          .collect(Collectors.toMap(
                key -> key.getName(),
                value -> value,
                (value1, value2) -> value1));
  }

  private String buildHtmlStyleSection(Collection<Style> allPossibleStyles) {
    var builder = new StringBuilder();
    builder.append("<head>\n");
    builder.append("<style>\n");
    allPossibleStyles.stream().forEach(m -> builder.append(buildIndividualStyle(m)));
    builder.append("</style>\n");
    builder.append("</head>\n");
    return builder.toString();
  }

  private String buildIndividualStyle(Style style){
    var builder = new StringBuilder();

    var sanitized = style.getName().replace("/[!\\\"#$%&'\\(\\)\\*\\+,\\.\\/:;<=>\\?\\@\\[\\\\\\]\\^`\\{\\|\\}~]/g", "");
    style.setName(sanitized);
    builder.append("." + style.getName());
    builder.append("\n{");
    builder.append("\n\tbackground-color: " + style.getBackgrounColor() + ";");
    builder.append("\n\tcolor: " + style.getColor() + ";");
    builder.append("\n}\n");
    return builder.toString();
  }

}
