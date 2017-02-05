# 📆 colander - filtering your calendar 

[![Build Status](https://jenkins.schnatterer.info/job/colander/job/develop//badge/icon)](https://jenkins.schnatterer.info/job/colander/job/develop/)
[![Quality Gates](https://sonarqube.schnatterer.info/api/badges/gate?key=info.schnatterer.colander:colander-parent)](http://sonarqube.schnatterer.info/dashboard?id=info.schnatterer.colander%3Acolander-parent&did=1)
[![Coverage](https://img.shields.io/sonar/https/sonarqube.schnatterer.info/info.schnatterer.colander:colander-parent/coverage.svg)](http://sonarqube.schnatterer.info/dashboard?id=info.schnatterer.colander%3Acolander-parent&did=1)
[![Technical Debt](https://img.shields.io/sonar/https/sonarqube.schnatterer.info/info.schnatterer.colander:colander-parent/tech_debt.svg)](http://sonarqube.schnatterer.info/dashboard?id=info.schnatterer.colander%3Acolander-parent&did=1)
[![JitPack](https://www.jitpack.io/v/schnatterer/colander.svg)](https://www.jitpack.io/#schnatterer/colander)
[![License](https://img.shields.io/github/license/schnatterer/colander.svg)](LICENSE)
[<img alt="powered by openshift" align="right" src="https://www.openshift.com/images/logos/powered_by_openshift.png"/>](https://www.openshift.com/)

Colander filters calender in ICS files. It can either be used as standalone application via [command line interface](#cli) or within 
JVM applications using the [API](#api). 

# CLI

* Download the latest version from [Releases](https://github.com/schnatterer/colander/releases).
* Extract the zip file.
* Use ist as follows:
```
  Usage: colander [options] <input.ics> [<output.ics]>
    Options:
      --help
        (optional) Show this message
        Default: false
      --remove-duplicate-events
        Remove event when summary, description, start date or end date are the same in another event
        Default: false
      --remove-empty-events
        Remove events when summary and description are empty
        times
        Default: false
      --remove-summary
        Remove calender component when summary contains expression
        Default: []
      --replace-description
        Replace in description of calender components (regex)
        Syntax: --replace-descriptionkey=value
        Default: {}
      --replace-summary
        Replace in summary calender components (regex)
        Syntax: --replace-summarykey=value
        Default: {}

```
* Example 
```
colander --remove-summary "Remove, RemoveIncludingLeadingSpace" --remove-summary "Another One to remove" --replace-summary "l.ne=line" cal.ics cal-new.ics
```
* Note that 
  * filters might refer to specific calender components (such as events). If not otherwise noted, a filter applies to all calender components (tasks, ToDos, Alarms, Venues, etc.)
  * the order of the arguments/filters is not maintained. That is, they are not applied in the order as passed
to the CLI.
  * If no `output.ics` file is passed, colander creates one, basing on the file name and the current timestamp, e.g. `input-20170129194742.ics`.
  * Colander never overwrites existing files. If the `output.ics` exists, colander fails.
  * If you care about return codes, they can be found here: [ExitStatus](cli/src/main/java/info/schnatterer/colander/cli/ExitStatus.java))
* Another example is the integration test for CLI (see [ColanderCliITCase](cli/src/test/java/info/schnatterer/colander/cli/ColanderCliITCase.java)). 

# API

The basic logic of colander is wrapped in the core module. This can be reused in other applications.
For now, this is not hosted on maven central, but on your can get it via jitpack.

Add the following maven repository to your POM.xml

```xml
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
```

Then add the actual dependency

```xml
        <dependency>
            <groupId>com.github.schnatterer.colander</groupId>
            <artifactId>colander-core</artifactId>
            <version>-SNAPSHOT</version>
        </dependency>
```

## How to use

```java
Colander.toss("/some/input.ics")
    .removeDuplicateEvents()
    .removeEmptyEvents()
    .removePropertyContains(Property.SUMMARY, "Remove me")
    // Generic replace in property
    .replaceInProperty(Property.DESCRIPTION, "L.ne", "Line")
    // Convenience: replace in property summary
    .replaceInSummary("Replace", "Replace!")
    .filter(event -> {
        System.out.println(event.toString());
        return Optional.of(event);
        })
    .rinse()
    .toFile("/some/output.ics");
```

Under the hood, colander uses [ical4j](https://github.com/ical4j/ical4j). You can get an instance of the result like so 

```java
Calendar cal = Colander.toss("/some/input.ics")
    // ...
    .rinse()
    .toCalendar("/some/output.ics");
```

More examples can be found in the 
* CLI module (see [ColanderCli](cli/src/main/java/info/schnatterer/colander/cli/ColanderCli.java)) and
* integration test for core (see [ColanderITCase](core/src/test/java/info/schnatterer/colander/ColanderITCase.java))

