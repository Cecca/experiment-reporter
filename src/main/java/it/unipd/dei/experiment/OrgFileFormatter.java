package it.unipd.dei.experiment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

public class OrgFileFormatter {

  public static DateFormat orgDateFormat = new SimpleDateFormat("<yyyy-MM-dd EEE HH:mm>");

  public static String format(Experiment experiment) {
    StringBuffer sb = new StringBuffer();
    sb.append(headline(experiment));
    sb.append(notes(experiment));
    sb.append(tags(experiment));
    sb.append(tables(experiment));
    return sb.toString();
  }

  private static String headline(Experiment experiment) {
    StringBuffer sb = new StringBuffer();
    sb.append("* ").append(experiment.getName())
      .append("  ").append(orgDateFormat.format(experiment.getDate()))
      .append("      ").append(":").append(experiment.getExperimentClass()).append(":")
      .append("\n");
    return sb.toString();
  }

  private static String notes(Experiment experiment) {
    StringBuffer sb = new StringBuffer();
    for(Experiment.Note note : experiment.getNotes()) {
      sb.append("  - ")
        .append(orgDateFormat.format(note.date)).append("  ")
        .append(note.message).append("\n");
    }
    return sb.toString();
  }

  private static String tags(Experiment experiment) {
    StringBuffer sb = new StringBuffer();
    sb.append("** Tags\n");
    for(Map.Entry<String, Object> t : experiment.getTags().entrySet()) {
      sb.append("   - ").append(t.getKey())
        .append(" : ").append(t.getValue().toString())
        .append("\n");
    }
    return sb.toString();
  }

  private static String tables(Experiment experiment) {
    StringBuffer sb = new StringBuffer();
    sb.append("** Tables\n");
    for(Map.Entry<String, Table> t : experiment.getTables().entrySet()) {
      sb.append("*** ").append(t.getKey()).append("\n");
      sb.append(t.getValue().asOrgTable()).append("\n");
    }
    return sb.toString();
  }

}
