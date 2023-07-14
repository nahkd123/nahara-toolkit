# Nahara's Toolkit: Attachments
Attachments allows you to attach additional data to existing object without spending time writing yet another ``HashMap`` code.

```java
Attachments.getGlobal().set(player, MyPlayerData.class, new MyPlayerData());
MyPlayerData data = Attachments.getGlobal().get(player, MyPlayerData.class);
```
