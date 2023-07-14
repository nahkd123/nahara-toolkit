# Nahara's Toolkit: NBT String
Convert string to NBT objects and vice versa.

ItemStack NBT data manipulation made possible with this project! 100% no NMS is used. Open ``nahara-spigot-items`` and see it yourself.

That said, modifying NBT can be risky (although less risky than using NMS) as newer game version may changes how item data are stored.

```java
NbtCompound compound = new NbtCompound();
compound
	.getOrCreate("display", NbtCompound::new).compound()
	.getOrCreate("Name", NbtCompound::new).string()
	.setText("\"Hello!\"");
String tag = compound.serializeAsString();

String displayName = Nbt.fromString(tag).compound()
	.get("display").compound()
	.get("Name").string()
	.getText();
```
