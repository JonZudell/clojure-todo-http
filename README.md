# todo
![It's all data](/itsalldata.png)
Implement an HTTP API for a To-Do app.  (No UI necessary.)

Requirements:
- [x] Unique todo-list on a per user basis
- [x] "Log in" with an email address (only used for identification; no password)
- [x] Add a new to-do task
- [x] Mark a task as complete (or incomplete, if was already marked complete)
- [x] Delete an existing task
- [x] View a chart comparing the number of complete vs. incomplete tasks
- [x] View a burn-down chart showing the addition and completion/deletion of tasks over time (that is, at every moment a task was added, the chart should step up by one, and every moment a task was completed or deleted, the chart should step down by one)

Possible Improvements:
 - Implement input validation on http routes with clojure.spec
 - Create a EDN serialization middleware to replace the JSON middleware
 - Configure github workflow to deploy the application to aws

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

In order to build this project you will need to set environment variables for `DATOMIC_USER` and `DATOMIC_USER_PASS`

## Running

To start a web server for the application, run:

    lein ring server

## Testing

To run tests once

    lein test

To rerun tests on every file change

    lein test-refresh
