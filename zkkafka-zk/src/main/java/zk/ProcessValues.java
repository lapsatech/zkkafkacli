package zk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProcessValues {

  public static class Mapping {
    private String argName;
    private String valueName;
    private String value;

    @Override
    public String toString() {
      return "Mapping [argName=" + argName + ", valueName=" + valueName + ", value=" + value + "]";
    }

    public String getArgName() {
      return argName;
    }

    public String getValueName() {
      return valueName;
    }

    public String getValue() {
      return value;
    }

    public String getEnvVar() {
      if (valueName == null) {
        return null;
      }
      return toEnvVar(valueName);
    }
  }

  public static void main(String[] args) throws IOException {
    Map<String, Mapping> mapping = new HashMap<>();
    try (InputStream is = ProcessValues.class.getResourceAsStream("/text2.txt");
        Reader r = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(r)) {
      String s;
      // --override num.network.threads={{.Values.NumNetworkThreads}} \
      Pattern p = Pattern.compile("\\s*--override\\s+([\\w\\.]+)\\s*\\=\\s*\\{\\{\\s*\\.Values\\.(\\w+)\\s*\\}\\}.*");
      while ((s = br.readLine()) != null) {
//        System.out.println(s);
        Matcher m = p.matcher(s);
        if (m.matches()) {
          Mapping mm = new Mapping();
          mm.argName = m.group(1);
          mm.valueName = m.group(2);
          mapping.compute(mm.valueName, (k, v) -> {
            if (v != null) {
              throw new IllegalStateException("Duplicate valueName " + k);
            }
            return mm;
          });
        }
      }

    }

    try (InputStream is = ProcessValues.class.getResourceAsStream("/text.txt");
        Reader r = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(r)) {
      String s;

      Pattern p = Pattern.compile("\\s*([\\w\\.]+)\\s*\\:\\s*(.*)");
      Pattern pp = Pattern.compile("\\\"(.*)\\\"");
      while ((s = br.readLine()) != null) {
        Matcher m = p.matcher(s);
        if (m.matches()) {
          String valueName = m.group(1);

          Mapping map = mapping.computeIfAbsent(valueName, v -> {
            Mapping mm = new Mapping();
            mm.valueName = v;
            return mm;
          });

          String dirty = m.group(2).trim();
          Matcher mat = pp.matcher(dirty);
          map.value = mat.matches()
              ? mat.group(1)
              : dirty;
        }
      }

    }

//    System.out.println(mapping);

    List<Mapping> maps = mapping.values()
        .stream()
        .sorted(Comparator.comparing(Mapping::getValueName))
        .collect(Collectors.toList());

//    maps.forEach(m -> System.out.printf("%1$s=${%1$s:-%2$s}\n", m.getEnvVar(), m.getValue()));

//    maps.forEach(m -> System.out.printf("--override %1$s=${%2$s:-%3$s} \\\n", m.getArgName(), m.getEnvVar(), m.getValue()));
//    - name: MEMORY_HEAP
//      value: {{ $.Values.app.node.memory.heap | quote }}
//    maps.forEach(m -> {
//      System.out.printf("    - name: \"KAFKA_%1$s\"\n", m.getEnvVar());
//      System.out.printf("      value: \"%1$s\"\n", m.getValue());
//    });

    maps.forEach(m -> {
    System.out.printf("    \"%1$s\": \"%2$s\"\n", m.getEnvVar(), m.getValue());
      
    });
  }

  private static String toEnvVar(String valueName) {
    String envVar = valueName;
    envVar = envVar.replaceAll("([A-Z])", "_$1");
    if (envVar.startsWith("_")) {
      envVar = envVar.substring(1);
    }
    envVar = envVar.toUpperCase();
    return envVar;
  }
}
