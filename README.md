# What's MCF
MCF, which could stand either for *MineCraft Fast* or for *MCFunction*, is a scripting language made to greatly simplify the process of writing minecraft datapacks.

In order to write a MCF script, you'll need to create a `.mcf` file, with a name of choice.

**Assuming both the compiler and the script are in the same direcotry**, if you want to compile the script to a minecraft datapack, you'll need to choose one of the following:
1. If you named your file `script.mcf`, you can just double click on the compiler jar (which you can download from the *releases* page of this repository); this way, you won't get output feedback, so you won't know if the script compiled successfully.
2. From a terminal of choice, you can change the directory to the one the files are in and then run `java -jar MCF.jar <yourScript>.mcf`. This way you'll be able to get output feedback *and* to select a custom-named script.

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