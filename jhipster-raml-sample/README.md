JHipster RAML sample
================================

This project reimplements the JHispter Spring REST API using using RAML and JAX-RS on CXF for a generated sample. 

See this [blog post][blog-post] for an introducation. 

Sample generation options
-------------------------
The generated sample is a Maven-based Java 8 project with cookie-based authentication on MySQL backend with Grunt and Compass CSS.

Implemenation
================
The following changes were applied:
 * Maven build
  * Added CXF dependency
  * Updated Jackson dependencies
  * Added RAML-to-JAX-RS plugin for code generation
  * Added JSON dependency
 * Implementation of generated JAX-RS interfaces
  * Updated security configuration, and
  * changed AngularJS services.js request paths accordingly
 * CXF web configurator
  * Added service implementation beans
  * Configured parameter converters
   
Building
===========
See [JHipster installation instructions][jhipster-installation].

Install [RAML JAX-RS Codegen][raml-jaxrs-codegen] into your local maven repository before building. 

Running
==========
Run the commands

    mvn spring-boot:run

and navigate to [localhost:8080][localhost].

[jhipster-installation]: https://jhipster.github.io/installation.html
[localhost]: http://localhost:8080
[blog-post]: http://www.greenbird.com/2014/10/26/raml-webapp-experiments/
[raml-jaxrs-codegen]: https://github.com/mulesoft/raml-jaxrs-codegen
