# kotlin-plugin

[![Build Status](https://travis-ci.org/pfn/kotlin-plugin.svg?branch=master)](https://travis-ci.org/pfn/kotlin-plugin)

Build kotlin code using sbt

Current version 1.0.2

## Usage

* for sbt 0.13.6+
* `addSbtPlugin("com.hanhuy.sbt" % "kotlin-plugin" % "1.0.2")`
* Kotlin code will build automatically from `src/XXX/kotlin`
* If necessary, add `kotlinLib("stdlib")`, it is not included by default.
  * Loading standard kotlin libraries and plugins: use `kotlinLib(NAME)` as
    above to load standard kotlin modules provided by JetBrains. For JetBrains
    kotlin compiler plugins, use `kotlinPlugin(NAME)` (e.g.
    `kotlinPlugin("android-extensions")`). The difference is that the latter
    marks the module as a `compile-internal` dependency and will be excluded
    from the final build product.
  * Any other libraries can be loaded using the normal `libraryDependencies`
    mechanism. Compiler plugins should be added as a normal `libraryDependency`
    but specified to be `% "compile-internal"`
* If a non-standard Classpath key needs to be added to the kotlin compile step,
  it can be added using the `kotlinClasspath(KEY)` function
  * For example, to compile with the android platform using `android-sdk-plugin`:
    `kotlinClasspath(Compile, bootClasspath in Android)`

## Options

* `kotlincPluginOptions`: specifies options to pass to kotlin compiler plugins.
  Use `val plugin = KotlinPluginOptions(PLUGINID)` and
  `plugin.option(KEY, VALUE)` to populate this setting
* `kotlinSource`: specifies kotlin source directory, defaults to
  `src/main/kotlin` and `src/test/kotlin`
* `kotlinVersion`: specifies versions of kotlin libraries to load using the,
   currently set to `1.0.2`
  `kotlinLib(LIB)` function, defaults to the version used while building this
  plugin
* `kotlincOptions`: options to pass to the kotlin compiler

### Examples

* See the [test cases](src/sbt-test/kotlin) for this plugin
