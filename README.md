SicsthSense
===========

We have a documentation site describing the project and how to get started:
http://docs.sense.sics.se

This repository contain many different aspects of the SicsthSense system:

* `engine`: The core server of SicsthSense, a lightweight RESTful HTTP server in Java.
* `web`: A web based engine providing the SicsthSense engine and a more attractive web interface also in Java.
* `tools`:
    * `library`: A python library for easily interacting with the SicsthSense engine.
    * `contiki`: Code for installing on small devices that can then talk to SicsthSense.
    * `android`: Example code to run on Android phones.
* `deprecated`: old code and prototypes.

The `INSTALL.sh` script will build the database (using `buildDB.sql`) and copy the required configuration files (for Play in `web`)


