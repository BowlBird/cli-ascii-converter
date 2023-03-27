# ASCII CONVERT

## Building
To build, since this is a command line utility, the normal `gradle run` command will not work. This is because you cannot properly pass in arguments that the program needs. Instead, run `gradle installDist` and that will make a script file that you can run. Otherwise, for a more modular approach you can run `gradle distZip` or `gradle distTar`.

Enjoy!