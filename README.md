# Nahara's Toolkit
A set of (maybe?) useful libraries that you can use in your project.

The original intention is to write libraries so that I can use them in small projects without rewriting too much code.

## Using the toolkit
### Jitpack
You can use Jitpack repository to obtain and use Nahara's Toolkit:

```grovvy
// build.gradle if you use Gradle
repositories {
    // ...
    maven { url 'https://jitpack.io' }
}

dependencies {
    // Replace <project name> with names below
    implementation 'com.github.nahkd123.nahara-toolkit:<project name>:main-SNAPSHOT'

    // Or you can include everything!
    // Beware: It also includes Spigot libraries and Fabric mods
    implementation 'com.github.nahkd123:nahara-toolkit:main-SNAPSHOT'
}
```

```xml
<!-- pom.xml if you use Maven -->
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.nahkd123.nahara-toolkit</groupId>
        <artifactId>[project name, see below.]</artifactId>
        <version>main-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### GitHub Packages
Just like Jitpack, artifacts will be published to GitHub Packages. However, you'll need to put your GitHub personal access token somewhere before you can use the repository.

```gradle
repositories {
    maven {
        url 'https://maven.pkg.github.com/nahkd123/nahara-toolkit'
        credentials {
            username 'your GitHub username'
            password 'ghp_YOURTOKENHERE'
        }
    }
}

dependencies {
    implementation 'me.nahkd:nahara-common-commands:0.0.1-SNAPSHOT'
}
```

> **Personal note**: I wanted to use GitHub Packages instead of Jitpack, but the PAT requirements is what was pulling people away from using it for open source projects. If you work in a company then this is really useful.

## Overview
### The ``common/`` projects
Projects in ``common/`` can be used without having running instance of Minecraft server.

- ``nahara-common-commands``: Commands executor and parser. Originally designed for Spigot commands but it can be used outside Spigot!
- ``nahara-common-configurations``: Configurations thing, with syntax similar to YAML.
- ``nahara-common-localize``: Localize package.
- ``nahara-common-nbtstring``: Convert NBT data from and to string. Mainly used for creating items with custom NBT data without touching the nasty NMS. That said, if you still wanted to touch NMS, you better use Fabric already.
- ``nahara-common-tasks``: Async tasks system. An attempt to bring ``async/await`` and ``Promise`` from JavaScript. Designed for fast code writing in mind.

### The ``spigot/`` projects
Projects in ``spigot/`` can only be used with your Spigot plugins.

- ``nahara-spigot-commands``: Basically ``common-commands``, but with Spigot stuffs. If you are making plugins, please use this and not ``nahara-common-commands``, unless you are a hardcore programmer.
- ``nahara-spigot-items``: Items builder + Items NBT manipulation thing.
- ``nahara-spigot-kit``: NaharaKit for creating main class a bit faster.

### The ``fabric/`` projects
Projects in ``fabric/`` are Fabric mods and they can be included in your mod with ``modImplementation include(...)`` in ``dependencies``.

## License
This project is licensed under MIT license, except we haven't included license headers to our source code yet. I'll do that soon™.
