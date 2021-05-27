# Comply-22

Comply-22 is a graph-based compliance engine. 

It uses Neo4J to map different information security and other standards to each other over multiple hops. Neo4J is uniquely suited for this task.

Comply-22 can also be used to document the application, implementation and assessment of controls over time.

## Engine / REST API

The first version is currently being developed.

## Data

Comply-22 includes some datasets based on publicly available sources. You will need the APOC library to scrape them directly from Github into the datgabase. There
is no need to download the files first.

## OSCAL: NIST 800-53 rev5 

In the data directory you will find a set of Neo4J cypher commands. They will scrape the [OSCAL](https://github.com/usnistgov/OSCAL) representation 
of the [NIST 800-53](https://github.com/usnistgov/oscal-content) catalog layer directly from Github 
into the Neo4J database.

There are also scripts to scrape the [profile layer](https://pages.nist.gov/OSCAL/documentation/schema/) (LOW, MEDIUM, HIGH and PRIVACY profiles). These will be linked to the catalog layer nodes so make sure to import these first.

## OSCAL: FedRAMP profiles

These scripts will scrape the [FedRAMP](https://github.com/GSA/fedramp-automation) profiles (LOW, MEDIUM, HIGH) form the Github repository right into Neo4J. Theay will be linked to the previously imported 800-53 controls and control enhancements
so make sure that you have imported them first.

## German BSI IT-Baseline Catalog

These scripts import a JSON representation of the German [IT-Baseline catalog](https://www.bsi.bund.de/EN/Topics/ITGrundschutz/itgrundschutz_node.html) ("IT-Grundschutz-Kompendium") into Neo4J. A script is provided to
convert an XML version of the catalog into a JSON format suitable for importing with APOC.

The required source XML file can be found as part of the [verinice](https://github.com/SerNet/verinice) project
