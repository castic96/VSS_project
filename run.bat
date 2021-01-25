@echo off
title Hospital simulation launcher
echo Hospital simulation launcher && echo Part 1/2 - Build && mvn clean install && echo Part 2/2 - Run && java -jar target\VSS_project-1.0-jar-with-dependencies.jar