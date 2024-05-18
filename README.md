# Snoozenet

```
Snoozenet
I Post, Therefore I Am

- The Unix Haters Handbook
```

Snoozenet is a quick and dirty NNTP server, implemented to have as _much_ compatibility with USENET as I could manage without going insane. NNTP is genuinely an _awful_ protocol to implement. If you are looking to implement it, this should give you a pretty good idea how to.

If you are not familiar with USENET, look [here](https://en.wikipedia.org/wiki/Usenet) first.

Snoozenet currently supports NONE of the server-to-server functions of NNTP. It also has spotty compatibility with clients. It has been tested and works with Mozilla Thunderbird, Microsoft Outlook Express for Windows XP, and Outlook for Windows 98. Apart from those, Snoozenet is untested and might not work. Within the clients tested, most if not all features are fully functional.

# Compiling

Compiling is quick and dirty thanks to maven.

First install `openjdk` and `maven` for your respective platform. Nothing more is needed.

To compile, run

```
$ mvn compile
```

To package into a JAR file, run

```
$ mvn package
```

# Running

To run Snoozenet, you can simply run `java -jar Snoozenet.jar`. Please not that the actual jar file will likely be named different.

Snoozenet does not need a config file to run, however on is provided in the root of this repo as an example. Simply rename it to "config.xml" and have it in the same folder from which you executed `Snooznet.jar` 
