package me.tkuipers.cr.test.lib.regex;

import com.google.common.collect.Lists;
import me.tkuipers.cr.lib.file.parser.file.regexHandler.RegularExpressionCombiner;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class RegularExpressionCombinationTests {

  @Test
  public void testOneExpression(){
    var expression = "\\}";
    var expectedExpression = "(\\})";

    var expList = Lists.newArrayList(expression);

    var actualExpression = RegularExpressionCombiner.combineRegularExpresssions(expList);

    assertEquals(expectedExpression, actualExpression);
  }

  @Test
  public void testTwoExpressions(){
    var expression1 = "\\}";
    var expression2 = "\\{";
    var expectedExpression = "(\\})|(\\{)";

    var expList = Lists.newArrayList(expression1, expression2);

    var actualExpression = RegularExpressionCombiner.combineRegularExpresssions(expList);

    assertEquals(expectedExpression, actualExpression);
  }

  @Test
  public void testMultipleNestedExpressions(){
    var expression1 = "(\\}|(12))|(3)";
    var expression2 = "(w)|((y)|(j))";
    var expectedExpression = "((?:\\}|(?:12))|(?:3))|((?:w)|(?:(y)|(?:j)))";

    var expList = Lists.newArrayList(expression1, expression2);

    var actualExpression = RegularExpressionCombiner.combineRegularExpresssions(expList);

    assertEquals(expectedExpression, actualExpression);
  }

  @Test
  public void testLargeNumberOfExpressions(){
    List<String> expList = Lists.newArrayList();
    expList.add("1");
    expList.add("2");
    expList.add("3");
    expList.add("4");
    expList.add("5");
    expList.add("6");
    expList.add("7");
    var expectedExpression = "(1)|(2)|(3)|(4)|(5)|(6)|(7)";

    var actualExpression = RegularExpressionCombiner.combineRegularExpresssions(expList);

    assertEquals(expectedExpression, actualExpression);
  }
}
