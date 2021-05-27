# todo

FIXME

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
Add gpg key to github secrets and adjust workfloy for lein to use it.

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2021 FIXME
