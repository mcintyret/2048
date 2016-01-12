This is my java8 version of the incredibly addictive game [2048](https://gabrielecirulli.github.io/2048/)

To give it a go simply:

1. clone the repo
2. `mvn clean package`
3. `java -jar target/2048.jar`

##Bots

You can also write a bot to play the game!

Simply implement the [MoveStrategy](https://github.com/mcintyret/2048/blob/master/src/main/java/com/mcintyret/twenty48/bot/MoveStrategy.java) class
and put the implementation class name as the first argument to the VM. Make sure the class is on the classpath! Currently the program can handle
primitive (or autoboxed) or String constructor args passed on the commandline as well.

eg:

```java
package foo.bar;

import com.mcintyret.twenty48.bot.MoveStrategy;

public class MyCunningMoveStrategy implements MoveStrategy {

    public MyCunningMoveStrategy(int intArg, float floatArg, String stringArg) {
        ...
    }

    ...

}
```

could be run (assuming the compiled MoveStrategy.class file is in folder <pwd>/foo/bar) by:

`java -classpath foo:target/2048.jar com.mcintyret.twenty48.ui.GUI foo.bar.MyCunningMoveStrategy 15 874.56 helloWorld`

(unfortunately the jar cannot be run directly as this doesn't allow adding extra files to the classpath. GUI.java is the main entry point for the program.)

This will reflectively instantiate MyCunningMoveStrategy with the args `(15, 874.56F, "helloWorld")`. Then you can watch your strategy at play!

My highest score with a bot is a rather piddling 5000. I'm sure you can do better!