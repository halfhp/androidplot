# Contributing Soure Code
Contributions are always welcome!  If you've got an idea for a specific feature and would like feedback 
on implementation or if you want to contribute but don't know where to start, feel free to [
start a topic in the Forums](https://groups.google.com/forum/#!forum/androidplot). If you already have 
a feature or bugfix to submit, take a look at the code style notes below and then submit a Pull Request.

## Code Style
For familiarity and simplicity, Androidplot uses [Android's code style guide](https://source.android.com/source/code-style.html). 
In particular:

* Indentations are 4 spaces each - no tab characters.
* Braces do not go on their own line; they go on the same line as the code before them.
* Line length should be a max of 100 characters.
* Don't catch generic Exception and don't ignore caught exceptions.
* Classes and methods should be documented with standard Javadoc comments.

## Pull Requests
If you have code changes you'd like to share with the project, issue a [Pull Request](https://help.github.com/articles/about-pull-requests/) against the master
branch of Androidplot.  Please make sure that:

* Difficult to follow logical statements, etc. are reasonably documented.
* Commented out lines, unused methods / dead code blocks have been removed.
* Unit Tests have have been refactored appropriately and all pass.
* A clear description of the change is included in the Pull Request.

Adding Unit Tests for new methods / functionality is highly encouraged.

# Git Newcomers
If you're new to Git, this section will show you the basics of checking out a local copy of the project and building it.

## Clone
Androidplot is hosted on Github.  If you've got a git client installed, you can either 
[fork](https://help.github.com/articles/fork-a-repo/) the project into your own
github account, or you can clone the project to your workstation using this command:

```
git clone https://github.com/halfhp/androidplot.git
```

You can also [download an ZIP](https://github.com/halfhp/androidplot/archive/master.zip) of the master branch of Androidplot.

## Build
Androidplot is a Gradle project, which makes building incredibly easy.  To perform a full build (runs tests, 
generates javadocs and builds the library and demo app) from the command line:

```
./gradlew test assemble
```
You can also of course use the IDE of your choice instead.  [Android Studio](https://developer.android.com/studio/index.html) is highly recommended.

## Useful Links
If you're new to Git these links will get you started:

* [Setting up Git](https://help.github.com/articles/set-up-git/) Start here if you don't have a Git client installed.
* [Pro Git (free eBook)](https://git-scm.com/book/en/v2)  - _The_ book to read when it comes to learning Git
* [Git Reference](https://git-scm.com/docs) - Good set of links for general reference