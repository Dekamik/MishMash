MishMash
========

MishMash is an API mashup for the purpose of gathering and combining artist information from three different APIs:
 MusicBrainz, Wikipedia and Cover Art Archive.

Usage
=====

While the application is running locally, point your favorite browser towards `localhost:8080/mishmash/<mbid>`, where 
 \<mbid\> is the MusicBrainz Identifier belonging to the artist you're looking for.

NOTE: If you query an artist that isn't cached, the API needs to query this data from its external APIs, 
 which may take a few seconds.

Response
--------

\<TODO: Insert JSON response schema\> 

Getting started
===============

Requirements
------------

Maven must be installed, as well as Java 1.8 or newer.
The application uses DropWizard and is directly run with an embedded Jetty server. 

Run application
---------------

1. Open a terminal and `cd` to the location of your mishmash-1-0 tarball
2. Extract the tarball: `tar -zxf mishmash-1-0.tar.gz`
3. (Optional) Edit the configuration file named mishmash.yml and define the max amount of cached artists
4. Compile and run with Maven: `mvn compile exec:java`

Developers notes
================

This project entailed a lot of new frameworks and design patterns for me. Below I've listed the functionality that I
 didn't have the time to do and implement.
 
Testing
-------

I've implemented some unit testing and done some basic integration tests. I didn't have the time to load-test this 
 application however. I regrettably cannot tell you how this application behaves during high load.

It does however limit the rate of API calls (where needed) and it does cache results in order to not overload the
 external APIs.

Missing functionality
---------------------

Due to the time constraints, I didn't have enough time to implement some functionality:

### Better error handling

Errors are only to some extent visible from the command line. One missing key feature are proper error responses to the
 end-user. There's a stub in the MishMashResource class, which works for some errors, but more testing is needed in 
  order to ensure that the proper errors are returned.

### Port configuration from Maven runtime

There should be a possibility to edit which port the embedded Jetty server should run this application on.
 I have a hunch that there's a way to do this from the command-line. I haven't had the time to look into this, however.

### More TDD and more unit tests

The implemented unit tests are only there to ensure base functionality with the intended input. More time should be 
 devoted to testing other responses from this API, with regards to illegal input and errors when using the external
  APIs.

### Use UTF-8

This is probably a small change, but a change I didn't have time for none the less. Currently the API cannot handle
 non-ASCII characters too well. The solution is to encode everything in UTF-8; especially the input streams from
  the external APIs.

Possible additional features
----------------------------

There are a couple of ways to improve this API:

### Implement health checks

Application health checks are a vital feature for ops. Of course this could be handled by ops themselves with i.e. Nagios
 or Chef. Either way this should be considered.

### Create database storage

All searches could not only be cached in memory, but also stored in a server-side database. This way we could further
 limit the use of APIs to only when the artist needs to be updated (after a specified cache expiration time).
  The cache could then be loaded by a background process during or after startup.

External API restrictions
=========================

The following external API restrictions date from 2016-02-21, changes may apply since then.

MusicBrainz
-----------

In compliance with the [MusicBrainz' documentation](http://musicbrainz.org/doc/Development/XML_Web_Service/Version_2), 
 the following restrictions has been observed and met in this API: 

* "[...] client applications [must] never make more than ONE web service call per second."
    * Met through throttling calls to once each second.
* "It is important that your application set a proper User-Agent in its HTTP request headers."
    * This is met in each HTTP request from this application.

Wikipedia API
-------------

In compliance with the [Mediawiki API Etiquette](https://www.mediawiki.org/wiki/API:Etiquette) which applies to the API, 
 the following restrictions has been observed and met in this API:

* "There is no hard and fast limit on read requests, but we ask that you be considerate and try not to take a site down."
    * Met through aforementioned throttling.
* "Use a descriptive User-Agent header that includes your application's name and potentially your email address if appropriate."
    * This is met in each HTTP request from this application.
    
Cover Art Archive
-----------------

According to the [Cover Art Archive documentation](https://wiki.musicbrainz.org/Cover_Art_Archive/API), there are 
 currently no throttling restrictions and no User-Agent requirement has been observed. The descriptive User-Agent is still present however.