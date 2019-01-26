# re-frame-fire-auth

A [re-frame](https://github.com/Day8/re-frame) X [fire-auth](https://firebase.google.com/docs/auth/) example.

## Setup

1. Go to the [firebase console]() and create a new app.
2. Enable [hosting]().
3. Enable [google auth]().
4. Copy your web credentials into `re-frame-fire-auth.core/mount`.
5. Build, run & profit.

## Development Mode

### Run application:

```
$ lein repl
=> (use 'figwheel-sidecar.repl-api)
=> (start-figwheel!)
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build

To compile clojurescript to javascript:

```
lein package
```
