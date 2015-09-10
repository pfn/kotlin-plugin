# kotlin-plugin
Build kotlin code using sbt

## Usage

* for sbt 0.13.6+
* `addSbtPlugin("com.hanhuy.sbt" % "kotlin-plugin" % "0.3")`
* Kotlin code will build automatically from `src/XXX/kotlin`
* If necessary, add `libraryDependencies <+= kotlinLib("stdlib")`, it is not
  included by default.

## Options

* `kotlinCompileOrder`: specifies whether to have kotlin compile before or after
  `KotlinCompileOrder.KotlinBefore` and `KotlinCompileOrder.KotlinAfter`
  the normal (scala+java) compilation step, values:
* `kotlincPluginOptions`: specifies options to pass to kotlin compiler plugins.
  Use `val plugin = KotlinPluginOptions(PLUGINID)` and
  `plugin.option(KEY, VALUE)` to populate this setting
* `kotlinCompileJava`: include java sources while compiling kotlin, this enables
  mixed-mode compilation, however, java sources will not be included with the
  normal scala compilation step.
* `kotlinSource`: specifies kotlin source directory, defaults to
  `src/main/kotlin` and `src/test/kotlin`
* `kotlinVersion`: specifies versions of kotlin libraries to load using the
  `kotlinLib(LIB)` function, defaults to the version used while building this
  plugin
* `kotlincOptions`: options to pass to the kotlin compiler

### Examples

* See the [test cases](sbt-test/kotlin) for this plugin
