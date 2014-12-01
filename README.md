# Doublejump

This project was just a small test how to use phaser.IO with clojurescript and chestnut live programming.

![Screenshort phaser.io with clojurescript](https://raw.githubusercontent.com/kstrempel/doublejump/master/resources/ScreenShot.png)

For the test I implemented the extremly nice double jump example from the [Game Mechanic Explorer](http://gamemechanicexplorer.com/#platformer-5).

Special thanks goes to [John Watson](https://twitter.com/yafd)


## Development

Start a REPL (in a terminal: `lein repl`, or from Emacs: open a
clj/cljs file in the project, then do `M-x cider-jack-in`. Make sure
CIDER is up to date).

In the REPL do

```clojure
(run)
(browser-repl)
```

The call to `(run)` does two things, it starts the webserver at port
10555, and also the Figwheel server which takes care of live reloading
ClojureScript code and CSS. Give them some time to start.

Running `(browser-repl)` starts the Weasel REPL server, and drops you
into a ClojureScript REPL. Evaluating expressions here will only work
once you've loaded the page, so the browser can connect to Weasel.

When you see the line `Successfully compiled "resources/public/app.js"
in 21.36 seconds.`, you're ready to go. Browse to
`http://localhost:10555` and enjoy.

## License

Copyright © 2014 Kai Strempel

Distributed under the Eclipse Public License either version 1.0 any later version.
