# Nahara's Toolkit: Localize
Provides localization engine for your Java application.

```java
public static final Message MY_MESSAGE = new Message("my.translation.key", "This {} is not translates!");

JsonObject myTranslations = JsonParser.parseJson(Files.readString(...)).getAsJsonObject();
Localizer myLocalizer = (key, def) -> myTranslations.has(key)? myTranslations.get(key).getAsString() : def;
Localizer.setInstance(myLocalizer);

System.out.println(MY_MESSAGE.of("message"));
```
