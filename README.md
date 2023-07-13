# Nahara's Toolkit
A set of (maybe?) useful libraries that you can use in your project.

The original intention is to write libraries so that we can use them in small projects without rewriting too much code.

## Using the toolkit
_Maven repository will be provided in the future!_

## Overview
### The ``common/`` projects
Projects in ``common/`` can be used without having running instance of Minecraft server.

- ``nahara-common-commands``: Commands executor and parser. Originally designed for Spigot commands but it can be used outside Spigot!
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
This project is licensed under MIT license, except we haven't included license headers to our source code yet. We'll do that soon™.
