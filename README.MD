# JarAgent

Java Instrumentation Agent for modifying classes at runtime.

Maybe helpful at AWD competitions.

## Usage

Write your patches in `src/main/java/patch` and run `mvn clean package` to build the agent jar.

You can learn how to write patches by reading the example patches in `src/main/java/patch`.

Compile the agent with `mvn clean package` and you will get `jaragent.jar`.

Then run your target program with `-javaagent:jaragent.jar` to apply the patches.

## Example

Before patching, when running the vuljava.jar in the example

Attacker can execute commands at 'http://127.0.0.1:8281/test?cmd=id'

After patching, the command execution will be blocked and return 'Patched'

And there will be a new route 'http://127.0.0.1:8281/new' which returns 'New Route' 

## License

MIT License
