package org.doublegsoft.protosys.projbase;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ThirdParty {

  private String name;

  private String version;

  private String group;

  private final List<String> libraries = new ArrayList<>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public List<String> getLibraries() {
    return getLibraries("c");
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public List<String> getLibraries(String language) {
    Yaml yaml = new Yaml();

    // 2. 从 resources 目录加载 YAML 文件
    try (InputStream inputStream = ThirdParty.class
        .getClassLoader().getResourceAsStream(language + "/libs")) {

      // 3. 将 YAML 解析为 Map<String, List<String>> 结构
      Map<String, List<String>> data = (Map<String, List<String>>) yaml.load(inputStream);
      List<String> libs = data.get(getName());
      if (libs != null) {
        return libs;
      }
    } catch (Exception e) {

    }
    return Arrays.asList(getName());
  }
}
