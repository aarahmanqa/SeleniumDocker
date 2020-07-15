#!/bin/sh
mvn -f /app/pom.xml clean install -DsuiteXmlFile=$1
