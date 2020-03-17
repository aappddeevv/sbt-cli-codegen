# sbt-cli-codgen

This sbt plugin generates scala source artifacts. Sbt by default does not
cache source generated artifacts. The manual recommends optimizing code
generation. This plugin implements that pattern so you can 
more easily generate artifacts with sbt caching.

[ ![Download](https://api.bintray.com/packages/aappddeevv/sbt-plugins/sbt-cli-codegen/images/download.svg?version=0.1.1) ](https://bintray.com/aappddeevv/sbt-plugins/sbt-cli-codegen/0.1.1/link)

## Usage

Generate scala source artifacts more easily than writing your
own tasks. While it is easy to generate your own scala sources
if you read sbt manual on this topic, once you want to start
tracking input and output files so that the build is optimized
a bit more than running the generator on every compile, you encounter
a little bit of boilerplate.

This plugin removes that boilerplate for simple cases but does
require you to be specific about your inputs and outputs so that
they can be manager by sbt.

sbt uses the `<project dir>/target/scala-<scala version>/src_managed/main`
directory by default for managed sources that you generate. To make
the plugin easier to configure, you should arrange for cli to 
place its outputs into that location.

To use, add the following:

```scala
// plugins.sbt
resolvers += Resolver.bintrayIvyRepo("aappddeevv", "sbt-plugins")

addSbtPlugin("ttg" % "sbt-cli-codegen" % "<latest version here>")
```

Then in your build.sbt:

```scala
// build.sbt

// you must be a bit more specific since it uses a more general plugin
val cli_command = (input_files: Seq[String]) =>
   (Seq("awesome-cli", "--param", "1", 
           "--output", "mysubproject/target/scala-2.13/src_managed/main/cli_codgen/awesome.scala"),
    Seq("awesome.scala"))

lazy val subproject = project.in(file("subproject"))
  .enablePlugin(CLICodegenPlugin)
  .setting(
        codegenCommand := cli_command,
  	codegenInputSources := Seq(sourceDirectory.value.toGlob / "mysubproject/src/main/awesome/*.awesome")
   )
```

There are some other ways to specify input and output files so that sbt can manage the run
process. Please check the source as the plugin is only 20-30 lines.
You can also dynamically generate an output file that has the list of output files that
were generated into the src_managed directory and the plugin will ensures that they exist.

## License

MIT license.
