# kotlin-plugin

Build kotlin code using sbt

Current version 0.9.3

## Usage

* for sbt 0.13.6+
* `addSbtPlugin("com.hanhuy.sbt" % "kotlin-plugin" % "0.9.3")`
* Kotlin code will build automatically from `src/XXX/kotlin`
* If necessary, add `libraryDependencies <+= kotlinLib("stdlib")`, it is not
  included by default.
  * Loading standard kotlin libraries and plugins: use `kotlinLib(NAME)` as
    above to load standard kotlin modules provided by JetBrains. For JetBrains
    kotlin compiler plugins, use `kotlinPlugin(NAME)` (e.g.
    `kotlinPlugin("android-extensions")`). The difference is that the latter
    marks the module as a `provided` dependency and will be excluded from the
    final build product.
  * All other libraries can be loaded using the normal `libraryDependencies`
    mechanism. Other compiler plugins should use the above, but be specified to
    be `% "provided"`
* If a non-standard Classpath key needs to be added to the kotlin compile step,
  it can be added using the `kotlinClasspath(KEY)` function
  * For example, to compile with the android platform using `android-sdk-plugin`:
    `kotlinClasspath(Compile, bootClasspath in Android)`

## Options

* `kotlincPluginOptions`: specifies options to pass to kotlin compiler plugins.
  Use `val plugin = KotlinPluginOptions(PLUGINID)` and
  `plugin.option(KEY, VALUE)` to populate this setting
* `kotlinCompileJava`: include java sources while compiling kotlin, this enables
  mixed-mode compilation, however, java sources will not be compiled during the
  normal scala compilation step.
* `kotlinSource`: specifies kotlin source directory, defaults to
  `src/main/kotlin` and `src/test/kotlin`
* `kotlinVersion`: specifies versions of kotlin libraries to load using the
  `kotlinLib(LIB)` function, defaults to the version used while building this
  plugin
* `kotlincOptions`: options to pass to the kotlin compiler

### Examples

* See the [test cases](src/sbt-test/kotlin) for this plugin
