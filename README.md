# telestion-extension-mavlink

A Mavlink message parser for the Telestion backend.

## Install

First, add the extension package registry to your gradle repositories:

```groovy
repositories {
    maven {
        name = "GitHubMavlink"
        url = uri("https://maven.pkg.github.com/wuespace/telestion-extension-mavlink/")
        credentials {
            username = System.getenv("GITHUB_ACTOR") == null ? GITHUB_ACTOR : System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN") == null ? GITHUB_TOKEN : System.getenv("GITHUB_TOKEN")
        }
    }
}
```

Next, add the extension to the project dependency list:

```groovy
dependencies {
    // ...
    implementation 'de.wuespace.telestion.extension:mavlink:0.4.0'
    // ...
}
```

And synchronize gradle.

Finished!

## Usage

The extension provides different verticles, that you can use and set up in your project `config.json`.
You will need:

- [Mavlink v2 Validator](https://wuespace.github.io/telestion-extension-mavlink/de/wuespace/telestion/extension/mavlink/ValidatorMavlink2.html)
- [Mavlink Message Registrar](https://wuespace.github.io/telestion-extension-mavlink/de/wuespace/telestion/extension/mavlink/MavlinkRegistrar.html)
- [Payload Parser](https://wuespace.github.io/telestion-extension-mavlink/de/wuespace/telestion/extension/mavlink/PayloadParser.html)

The `Mavlink Validator` verticles for `Mavlink v1` and `Mavlink v2` validate the incoming encoded mavlink message
and sends it to the parser on a valid mavlink message.
Additionally, all encoded messages can be sent to the `Mavlink Logger`, if set up,
which stores the encoded or raw version in the preconfigured data file.

A `Mavlink v2 Validator` configuration could look like:

```json
{
  "org.telestion.configuration": {
    "app_name": "Basic Example",
    "verticles": [
      {
        "name": "Mavlink v2 Validator",
        "verticle": "de.wuespace.telestion.extension.mavlink.ValidatorMavlink2",
        "magnitude": 1,
        "config": {
          "inAddress": "nice-serial",
          "packetOutAddress": "mavlink-raw-logger",
          "parserInAddress": "mavlink-payload-parser-in",
          "password": "AQ=="
        }
      }
    ]
  }
}
```

The mavlink message is received from the `nice-serial` channel, validated
and logged to the `Mavlink Raw Logger`.
If the mavlink is valid, the validated message and metadata are sent to the `Payload Parser`.

The configuration of the `Payload Parser` could look like:

```json
{
  "org.telestion.configuration": {
    "app_name": "Basic Example",
    "verticles": [
      {
        "name": "Payload Parser",
        "verticle": "de.wuespace.telestion.extension.mavlink.PayloadParser",
        "magnitude": 1,
        "config": {
          "inAddress": "mavlink-payload-parser-in",
          "outAddress": "parsed-payload"
        }
      }
    ]
  }
}
```

This verticle parses the encoded mavlink message incoming from the `mavlink-payload-parser-in` channel
and the parsed output are sent onto `parsed-payload` channel to other verticles.

It can only parse the encoded message to registered mavlink messages.
This is done with the `Mavlink Message Registrar` verticle.

This could look like:

```json
{
  "org.telestion.configuration": {
    "app_name": "Basic Example",
    "verticles": [
      {
        "name": "Mavlink Message Registrar",
        "verticle": "de.wuespace.telestion.project.daedalus2.mavlink.MavlinkRegistrar",
        "magnitude": 1,
        "config": {
          "classNames": [
            "de.wuespace.telestion.project.daedalus2.mavlink.SeedSystemT",
            "de.wuespace.telestion.project.daedalus2.mavlink.ConCmd"
          ]
        }
      }
    ]
  }
}
```

This verticle registers the `de.wuespace.telestion.project.daedalus2.mavlink.SeedSystemT` and
`de.wuespace.telestion.project.daedalus2.mavlink.SeedSystemT` mavlink messages to the `Payload Parser`.

To store all received encoded Mavlink messages, you can use the `Mavlink Logger`.

For example:

```json
{
  "org.telestion.configuration": {
    "app_name": "Basic Example",
    "verticles": [
      {
        "name": "MavLink Logger",
        "verticle": "de.wuespace.telestion.extension.mavlink.logger.RawLogger",
        "magnitude": 1,
        "config": {
          "inAddress": "mavlink-raw-logger",
          "filePath": "data/mavlink-logger/"
        }
      }
    ]
  }
}
```

It receives the encoded messages from the `Mavlink Validator` verticle
and stores it in time-tagged files in the predefined path. (here `data/mavlink-logger/`)

## Generating Mavlink Messages

To generate the Mavlink messages from the Mavlink xml definitions,
you can use the [`xml2mav.py`](scripts/xml2mav.py) python script.
It takes one or more definition files and outputs valid Java classes
which you can import and them directly in your project.

For example:

```shell
python xml2mav.py -f my-definition.xml -o messages
```

## Contributing

For the documentation on contributing to this repository,
please take a look at the [Contributing Guidelines](./CONTRIBUTING.md).

## About

This is part of [Telestion](https://telestion.wuespace.de/), a project by [WüSpace e.V.](https://www.wuespace.de/).
