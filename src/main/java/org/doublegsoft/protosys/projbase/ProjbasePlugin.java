package org.doublegsoft.protosys.projbase;

import com.doublegsoft.jcommons.lang.HashObject;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metamodel.ApplicationDefinition;
import com.doublegsoft.jcommons.metamodel.UsecaseDefinition;
import com.doublegsoft.jcommons.metaui.PageDefinition;
import com.doublegsoft.jcommons.metaui.WidgetDefinition;
import com.doublegsoft.jcommons.metaui.layout.Grid;
import com.doublegsoft.jcommons.metaui.layout.Position;
import com.doublegsoft.jcommons.programming.dart.DartConventions;
import com.doublegsoft.jcommons.programming.dart.DartNamingConvention;
import com.doublegsoft.jcommons.programming.rust.RustConventions;
import com.doublegsoft.jcommons.utils.Strings;
import com.google.gson.Gson;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.DefaultObjectWrapper;
import io.doublegsoft.tatabase.Tatabase;
import io.doublegsoft.tatabase.ne.DomainObject;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.doublegsoft.protosys.commons.FileSystemTemplateBasedPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO: ADD DESCRIPTION
 *
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 * @since 1.0
 */
public class ProjbasePlugin extends FileSystemTemplateBasedPlugin {

  private static final Tatabase TATABASE = new Tatabase();

  private ApplicationDefinition application;

  private ModelDefinition model;

  public ApplicationDefinition createApplication4Guidbase(String guidbaseSource) throws IOException {
    ApplicationDefinition retVal = new ApplicationDefinition();
    if (Strings.isBlank(guidbaseSource)) {
      return retVal;
    }
    return retVal;
  }

  /**
   * Generates prototype source code.
   *
   * @param modelbases
   *        the modelbase definition file paths
   *
   * @param guidbases
   *        the guic definition file paths
   *
   * @param outputRoot
   *        the output root under native file system
   *
   * @param templateRoot
   *        the template root under native file system
   *
   * @param globals
   *        the global variables
   *
   * @throws IOException
   *        in case of any errors, throw it
   *
   * @version 3.0.1 - add statics shared variables in freemarker configuration to support java static method invocation.
   */
  public void prototype(String[] modelbases, String[] guidbases, String outputRoot, String templateRoot, HashObject globals) throws IOException {
    FileTemplateLoader specific = new FileTemplateLoader(new File(templateRoot));
    FileTemplateLoader common = new FileTemplateLoader(new File(templateRoot + "/.."));
    FileTemplateLoader common2 = new FileTemplateLoader(new File(templateRoot + "/../.."));
    MultiTemplateLoader templateLoader = new MultiTemplateLoader(new TemplateLoader[]{common, common2, specific});
    FREEMARKER.setTemplateLoader(templateLoader);
    FREEMARKER.setSharedVariable("statics", ((DefaultObjectWrapper) FREEMARKER.getObjectWrapper()).getStaticModels());

    if (modelbases != null && modelbases.length > 0) {
      // if not, will report parse error
      model = createModelFromModelbase(modelbases);
      globalVariables.put("model", model);
    }

    StringBuilder guidbaseSource = new StringBuilder();
    for (String guidbaseFile : guidbases) {
      guidbaseSource.append(new String(Files.readAllBytes(new File(guidbaseFile).toPath()))).append("\n");
    }
    application = createApplication4Guidbase(guidbaseSource.toString());
    /*!
    ** Set application model from modelbase.
    */
    application.setModel(model);
    if (globals.containsKey("application")) {
      application.setName(globals.get("application"));
    }

    if (globals != null) {
      globalVariables.putAll(globals);
    }
    visitAndRender(outputRoot, "", templateRoot, "", application, globals);
  }

  /**
   * Creates the new file for the given widget. And for object-oriented language when we look at the widget as an
   * object, we should make an object type file to define the widget.
   * <p>
   * And this method is always invoked in template script file within different user interface frameworks.
   *
   * @param templatePath
   *        the template path to generate file
   *
   * @param widget
   *        the child widget definition
   *
   * @since 3.3
   *
   * @version 3.3 - initialize this method
   */
  public void create(String outputRoot, String outputPath, String outputName, String templatePath, WidgetDefinition widget) throws IOException {
    int lastSlashIndex = templatePath.lastIndexOf("/");
    String tmlpath = null;
    String tmlname = null;
    if (lastSlashIndex == -1) {
      tmlname = templatePath;
      tmlpath = "./";
    } else {
      tmlname = templatePath.substring(lastSlashIndex + 1);
      tmlpath = templatePath.substring(0, lastSlashIndex);
    }
    Map<String, Object> model = toTemplateData(widget);
    model.putAll(globalVariables);
    model.putAll(createTemplateData(application));
    renderTo(outputRoot, outputPath, outputName, tmlpath, tmlname, model);
  }

  public static void main(String[] args) throws Exception {
    Options options = new Options();

    options.addOption("m", "modelbase", true, "模型定义文件");
    options.addOption("u", "misuml", true, "用例定义文件");
    options.addOption("i", "guidbase", true, "界面定义文件");
    options.addOption("t", "template-root", true, "模板定义根目录");
    options.addOption("o", "output-root", true, "输出根路径");
    options.addOption("b", "tatabase", true, "tatabase数据目录");
    options.addOption("l", "license", true, "license文件");
    options.addOption("g", "globals", true, "全局变量");

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);

    String templateRoot = cmd.getOptionValue("template-root");
    String outputRoot = cmd.getOptionValue("output-root");
    String modelbasePaths = cmd.getOptionValue("modelbase");
    String misumlPaths = cmd.getOptionValue("misuml");
    String tatabase = cmd.getOptionValue("tatabase");
    String guidbasePaths = cmd.getOptionValue("guidbase");

    String globals = cmd.getOptionValue("globals");
    String licensePath = cmd.getOptionValue("license");

    String license;

    DomainObject.setDataDir(tatabase);

    HashObject globalVars = new HashObject();
    Gson gson = new Gson();
    if (globals != null) {
      globalVars.putAll(gson.fromJson(globals, Map.class));
    }
    globalVars.set("tatabase", TATABASE);
    globalVars.set("dart", new DartConventions());
    globalVars.set("rust", new RustConventions());
    globalVars.set("grid", new Grid());

    String namingClass = globalVars.get("globalNamingConvention");
    if (namingClass != null) {
      Object naming = Class.forName(namingClass).newInstance();
      globalVars.set("globalNamingConvention", naming);
    }

    if (licensePath != null) {
      license = new String(Files.readAllBytes(new File(licensePath).toPath()), "UTF-8");
      globalVars.set("license", license);
    }
    globalVars.put("helper", new ProjbaseHelper(outputRoot));
    ProjbasePlugin projbase = new ProjbasePlugin();
    try {
      projbase.prototype(
          modelbasePaths == null ? new String[0] : modelbasePaths.split(";"),
          guidbasePaths == null ? new String[0] : guidbasePaths.split(";"),
          outputRoot, templateRoot, globalVars);
    } catch (Throwable cause) {
      cause.printStackTrace();
    }

  }

}
