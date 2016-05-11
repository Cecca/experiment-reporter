package it.unipd.dei.experiment;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

public class TagTest {

  private static Properties originalProperties = new Properties();

  @BeforeClass
  public static void setupClass() {
    Properties sysProps = System.getProperties();
    for(String k : sysProps.stringPropertyNames()) {
      originalProperties.setProperty(k, sysProps.getProperty(k));
    }
  }

  @After
  public void teardown() {
    System.setProperties(originalProperties);
  }

  @Test
  public void testSystemTagsSingle() {
    System.setProperty("experiment.tag.test", "1");
    Experiment exp = new Experiment();

    Map<String, Object> tags = exp.getTags();

    assertTrue("Tag map=" + tags.toString(),
            tags.containsKey("test"));
    assertEquals(tags.get("test"), 1);
  }

  @Test
  public void testSystemTagsMultiple() {
    System.setProperty("experiment.tag.test", "1");
    System.setProperty("experiment.tag.some-string", "a string");
    Experiment exp = new Experiment();

    Map<String, Object> tags = exp.getTags();

    assertTrue("Tag map=" + tags.toString(),
            tags.containsKey("test"));
    assertEquals(tags.get("test"), 1);
    assertTrue("Tag map=" + tags.toString(),
            tags.containsKey("some-string"));
    assertEquals(tags.get("some-string"), "a string");
  }

  @Test
  public void testSystemTagsEmpty() {
    Experiment exp = new Experiment();

    Map<String, Object> tags = exp.getTags();

    assertTrue("Tag map=" + tags.toString(),
            tags.isEmpty());
  }

  @Test
  public void testSystemTagsNumeric() {
    System.setProperty("experiment.tag.test-integer", "1");
    System.setProperty("experiment.tag.test-double", "3.14");
    Experiment exp = new Experiment();

    Map<String, Object> tags = exp.getTags();

    assertEquals(tags.get("test-integer"), 1);
    assertEquals(tags.get("test-double"), 3.14);
  }


}
