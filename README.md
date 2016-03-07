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
