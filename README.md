pages
=====

[![Scala.js](https://img.shields.io/badge/scala.js-0.6.20%2B-blue.svg)](https://www.scala-js.org)
[![travis](https://travis-ci.org/flowtick/pages.svg?branch=master)](https://travis-ci.org/flowtick/pages)
[![latest release for 2.11](https://img.shields.io/maven-central/v/com.flowtick/pages_sjs0.6_2.11.svg?label=scala+2.11)](http://mvnrepository.com/artifact/com.flowtick/pages_2.11)
[![latest release for 2.12](https://img.shields.io/maven-central/v/com.flowtick/pages_sjs0.6_2.12.svg?label=scala+2.12)](http://mvnrepository.com/artifact/com.flowtick/pages_2.12)

A simple page router for Scala(.js) inspired by [page.js](https://visionmedia.github.io/page.js).

See [spec](shared/src/test/scala/pages/PageSpec.scala) for usage examples.

Build
=====

    sbt package

Release
=======

    sbt +compile
    sbt release

License
=======

Apache License Version 2.0, see [LICENSE](LICENSE)
