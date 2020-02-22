# Generate graphql Artifacts

Generate graphql artifacts using the apollo code generator.

To use, add the following:

```scala
// plugins.sbt
resolvers += Resolver.bintrayIvyRepo("aappddeevv", "sbt-plugins")

addSbtPlugin("ttg" % "sbt-graphql-apollo-gen-scala" % "<latest version here>")
```

Then in your build.sbt:

```scala
// build.sbt
...project def...
  .enablePlugin(ApolloCodegenPlugin)
  .setting(graphQLPackageName := "org.blah") // defaults to project normalized name
```

By default, the plugin looks for graphql sources at `<project>/src/main/graphql/*.graphql`.
It is assumed that the schema file is called `schema.json`. The schema file can be
generated using the apollo cli as well.

See the plugin code for various settings to customize the code
generation process, e.g., the schema file or the output package name.

Don't forget to install the apollo tooling CLI via npm :-)

```sh
# install to local project folder with a package.json already present
npm i apollo
```

Instead of this plugin you could use the watch facility in apollo codegen
directly and ensure that you pickup all your files accordingly and set
the output file correctly into the src_generated directory or a well-known
location of your choosing to ensure a rebuild as needed.

# License

MIT license.
