# Nahara's Toolkit: Commands
The Nahara's Toolkit commands framework. Originally designed for me to quickly create plugin commands with tab completion, but you can use it without Spigot.

```java
new Command<>("name")
	.argument("arg1").argument("arg2")
	.option("s", true)
	.option("key", false)
	.onExec(ctx -> {
		ctx.option("s").ifPresent(v -> ctx.println("-s is present"));
		ctx.option("key").ifPresent(v -> ctx.println("-key = " + v));
	})
	.child(new Command<>("childcommand")
		.onExec(ctx -> {
			ctx.println("Child command executed");
		})
	);
```
