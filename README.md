# 📆 colander - filtering your calendar 

[![Docker Image](https://images.microbadger.com/badges/image/schnatterer/colander.svg)](https://hub.docker.com/r/schnatterer/colander/)
[![Build Status](https://travis-ci.org/schnatterer/colander.svg?branch=develop)](https://travis-ci.org/schnatterer/colander)
[![Quality Gates](https://sonarcloud.io/api/project_badges/measure?project=info.schnatterer.colander%3Acolander-parent&metric=alert_status)](https://sonarcloud.io/dashboard?id=info.schnatterer.colander%3Acolander-parent)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=info.schnatterer.colander%3Acolander-parent&metric=coverage)](https://sonarcloud.io/dashboard?id=info.schnatterer.colander%3Acolander-parent)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=info.schnatterer.colander%3Acolander-parent&metric=sqale_index)](https://sonarcloud.io/dashboard?id=info.schnatterer.colander%3Acolander-parent)
[![JitPack](https://www.jitpack.io/v/schnatterer/colander.svg)](https://www.jitpack.io/#schnatterer/colander)
[![License](https://img.shields.io/github/license/schnatterer/colander.svg)](LICENSE)

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
      --remove-description
        Remove calender component when description contains expression
        Default: []
      --remove-duplicate-events
        Remove event when summary, description, start date or end date are the 
        same in another event
        Default: false
      --remove-empty-events
        Remove events when summary and description are empty
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
* Colander CLI writes logs to the `logs` folder.

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
            <version>0.1.0</version>
        </dependency>
```

## How to use

```java
Colander.toss("/some/input.ics")
    .removeDuplicateEvents()
    .removeEmptyEvents()
    .removePropertyContains(Property.SUMMARY, "Remove me")
    .removeDescriptionContains("Remove me 2")
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

