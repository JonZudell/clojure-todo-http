# todo

Implement an HTTP API for a To-Do app.  (No UI necessary.)

Requirements:
- [x] Unique todo-list on a per user basis
- [x] "Log in" with an email address (only used for identification; no password)
- [x] Add a new to-do task
- [x] Mark a task as complete (or incomplete, if was already marked complete)
- [x] Delete an existing task
- [x] View a chart comparing the number of complete vs. incomplete tasks
- [x] View a burn-down chart showing the addition and completion/deletion of tasks over time (that is, at every moment a task was added, the chart should step up by one, and every moment a task was completed or deleted, the chart should step down by one)

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Build requirements

In order to build this project you will need to:
 - setup gpg keys for leiningen.
 - Request a licence for https://cognitect.com/dev-tools

~/.lein/credentials.clj
```
;; ~/.lein/credentials.clj.gpg (see the Leiningen deploy authentication docs)
{#"my\.datomic\.com" {:username "<DATOMIC_USERNAME>"
                      :password "<DATOMIC_PASSWORD>"}}
```

Create a gpg key `gpg --gen-key`

Encrypt your credentials with gpg `gpg --default-recipient-self -e ~/.lein/credentials.clj > ~/.lein/credentials.clj.gpg`

### TODO:
Add gpg key to github secrets and adjust workflow for lein to use it.

## Running

To start a web server for the application, run:

    lein ring server

## Testing

To rerun tests on every file change

    lein test-refresh

## License

Copyright © 2021 FIXME
