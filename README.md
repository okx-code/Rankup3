# Rankup3
## Compiling
### Prerequisites
Compilation requires the jdk8 (java development kit).  
#### Windows
The [Eclipse Foundation](https://www.eclipse.org/org/) releases [the required build tools here](https://adoptium.net/temurin/releases).  
It's recommended to select an installer download for simplicity and/or compiling other plugins. Don't select a zip archive.  
You can easily clone this repo using the [github desktop app](https://desktop.github.com/).  
1. Copy the link provided in the above green `Code` button.
2. In the github desktop app, Select `Clone a repository from the Internet` or `Clone repository...`, then select URL, and paste into the url line.
3. Open the Rankup3 directory you downloaded. The default location is in your user's `\Documents\GitHub\Rankup3` folder.
4. Open a command prompt in the directory and run [`dir`](https://docs.microsoft.com/en-us/windows-server/administration/windows-commands/dir) to catalog the directory.
5. When you see the `gradlew` file listed, you can now try the [build command](#building).  

#### Linux
Add `jdk8` or `jdk8-openjdk` from your package manager.  
Add `git` (required) and/or `gh` (optional, for github integration) from your package manager.  
After installing packages, clone the repository by running `git clone` on the url provided above in the Code button, or the provided `gh` cloning command, from your shell.  
Then try the [build command](#building).  

### Building
In the Rankup3 directory, a jar can be compiled with the provided `gradlew` wrapper:  
- Command Prompt: `gradlew build`
- Unix/Powershell/Other: `./gradlew build`

#### Known Build Errors
##### `Unsupported class file major version #`
This error indicates you are building with the wrong version of the JDK.  
Solution:  
- Windows: Correct your `Environment Variables` for `JAVA_HOME` to point to the installation location of the JDK 8. `Environment Variables` is program searchable in the start menu that will prompt you with a list of filepaths to your jvm installations in the `JAVA_HOME` variable. The JDK 8's filepath should be moved to the top of the list before [building](#building).
- Linux: Your package manager may natively support installing multiple JDK versions simultaneously. Consult your package manger's docs for information about managing your jvm's. Here are some examples: [`sudo update-alternatives --config java`](https://help.ubuntu.com/community/Java#Choosing_the_default_Java_to_use) or [`sudo archlinux-java set java-8-openjdk`](https://wiki.archlinux.org/title/Java#Switching_between_JVM). The JDK 8 should be selected before [building](#building).
### Jars
The compiled plugin jar will be in the `/build/libs` directory.  
All branches are using the same jar name. Rename your build artifacts.  

## Translation

If you would like to contribute to translating the plugin, you can fork it.
Locale names are in ISO-639-1 and are in the folder [`src/main/resources/locale`](https://github.com/okx-code/Rankup3/tree/master/src/main/resources/locale)

When a locale is added, it should be saved [here](https://github.com/okx-code/Rankup3/blob/master/src/main/java/sh/okx/rankup/RankupPlugin.java#L294), ie through adding a line such as `saveLocale("es");`. However, I can do it if you forget.

You can use the [English](https://github.com/okx-code/Rankup3/blob/master/src/main/resources/locale/en.yml) locale to help translate.

Locales should be added or modified with a pull request, but if you wish to contribute often I can give you push access to this branch.

Here is a blank template for new locales:

```yaml
rankup:
  requirements-not-met: 
  no-rankup: 
  success-public: 
  success-private: 
  confirmation: 
  title:  
  must-prestige: 
  list:
    complete: 
    current: 
    incomplete: 
    header: ""
    footer: ""
  cooldown:
    singular: 
    plural: 
prestige:
  requirements-not-met: 
  no-prestige: 

  success-public: 
  success-private: 

  confirmation: 
  title: 

  list:
    complete: 
    current: 
    incomplete: 
    header: ""
    footer: ""
  cooldown:
    singular: 
    plural: 

not-high-enough: 
not-in-ladder: 
invalid-rankup: 
```
