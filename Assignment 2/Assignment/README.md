# Bharath Sukesh GUI Text Editor Tool - README

### Purpose: 
* Graphical text editor that is designed to primarily utilise plugins and scripts, as well as other secondary features as per requested by the Assignment Specification.

### Compiling:  

To compile and run do `./gradlew run` in the main directory (that contains this README.md file). 

> Ref: Provided UIExample file was adapted for Assignment.


### Notes:

The program accepts arguments to specify a Locale, in the form of:
    `./gradlew run --args='--locale=en-AU'` via the commandline argument.
Some further examples of this are:
    `./gradlew run --args='--locale=do'` for my custom dollar language (as per Assignment Specification)
    `./gradlew run --args='--locale=jp-JP'` for a Japanese Locale (identifiable by the Date Plugin format of YYYY-MM-DD 23:59)
    `./gradlew run --args='--locale=en-US'` for a US Locale (also identifiable by the Date Plugin format of MM-DD-YY 11:59 PM)

If absent, the program will automatically select the default system locale.

### Assumptions:
    - The scripts require a .py Python file specifically.