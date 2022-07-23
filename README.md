# What's MCF
MCF, which could stand either for *MineCraft Fast* or for *MCFunction*, is a scripting language made to greatly simplify the process of writing minecraft datapacks.

In order to write a MCF script, you'll need to create a `.mcf` file, with a name of choice. You'll then need to compile such file using the compiler, which you can find in the *releases* page of this repository.

**Assuming both the compiler and the script are in the same directory**, if you want to compile the script to a minecraft datapack, you'll need to choose one of the following:
1. If you named your file `script.mcf`, you can just double click on the compiler jar (which you can download from the *releases* page of this repository); this way, you won't get output feedback, so you won't know if the script compiled successfully.
2. From a terminal of choice, you can change the directory to the one the files are in and then run `java -jar MCF.jar <yourScript>.mcf`. This way you'll be able to get output feedback, to select a custom-named script *and* to add command line arguments, which are listed below.
# Command line arguments
When you launch the compiler through `java -jar MCF.jar <yourScript>.mcf`, you can add command line arguments to customize the compiler's behavior.

Taking the `-verbose` command line argument as an example, if you run `java -jar MCF.jar script.mcf -verbose`, the output logging will be verbose.

You can use as many command line arguments as you want, making sure to separate them with a space (ex: `java -jar MCF.jar script.mcf -verbose -noNewDirPrompt`)

## logPrefix
**Usage**: `-logPrefix`
**Effect**: Enables log prefixes, which explicit if the message is either:
1. An exception (`EXC`)
2. A warning (`WRN`)
3. An info (`INF`)
4. A verbose info (`VRB`)
5. An extremely verbose info (`XVB`)

A log message would for example change from "`This is an exception.`" to "`EXC: This is an exception.`"

## noNewDirPrompt
**Usage**: `-noNewDirPrompt`
**Effect**: Disables the prompt which asks you if you want to create a new directory. When this setting is disabled, all directories are automatically created.

## logDepth
**Usage**: `-logDepth <numericValue>`
**Effect**: sets the logging depth to the specified value (0=Exceptions, 1=Warnings, 2=Infos, 3=Verbose, 4=Extremely verbose)

# Syntax
## Metadata
Metadata consists of extra information which concerns the general configuration of the datapack. The general declaration of a metadata tag looks like this:
```
#<tag>=<value>
```

**When the first non-meta instruction is met, the program creates the physical implementation of the datapack: from that point onwards, every meta instruction will be completely useless**.

### name
Sets the name for the datapack, which physically consists of its folder's name. By default, this is `MCF`.

The name **must** be between quotes. You **must not** put `/` at the end of the name.

Example:
```
#name="DirectoryExample"
```

The name of this datapack's folder will now be `DirectoryExample`

### format
Sets the datapack format in the `pack.mcmeta` file. By default, this is `10`.

The format **must not** be bewtween quotes.

Example:
```
#format=9
```

The format of this datapack will now be `9`.

### description
Sets the datapack description in the `pack.mcmeta` file. By default, this is `Made with MCF!`.

The description **must** be between quotes.

Example:
```
#description="Made with love <3"
```
The description of this datapack will now be `Made with love <3`.

### destination
Sets the destination directory for this datapack. By default, this is empty (hence, the destination directory will be the same one as the directory the compiler is in).

The destination **must** be between quotes. You **must** put `/` at the end of the destination.

Example:
```
#destination="simple/path/"
```
The destination directory of this datapack will now be `simple/path/`. Know that the name will still be considered: the given example will have `simple/path/MCF/` as the complete datapack folder.