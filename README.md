Technical Code Screen - Stocks
============
Hi there,

Welcome to the in-depth coding portion of our interview! Congratulations on making it this far - we appreciate the time and effort you've put in.

Get Started
============
To complete this coding project, you should only need to manually install the [Java 1.8 JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) if you don't have it already installed.

If you have any issues with getting the project loaded, please contact us immediately.

Project Details
============
This project is meant to give you a chance to showcase your coding chops. It represents a very simple stock management system. Your goal is to make all the unit tests green
by filling in the portions of `StockManager` that have the comment:

`// TODO: Implement me.`

You will not need to alter any of the other classes in the project; only implementing `StockManager` is necessary. However, if you need or want to introduce helper classes for convenience and clarity, please do so; **make sure you write the appropriate test harness for any new classes you introduce**.

We are looking for a few important things (in order from most important to least important):

* Make `StockManagerTest` pass running `gradlew clean build`
  * Note: on a Mac you may need to say `./gradlew clean build`
  * For what it's worth, we don't care if the test passes in your IDE - if it does not pass using the build tool it will not count
* Have fun with the project
* Roughly follow the existing coding conventions of the project (please use best judgement here)
* If you introduce a new dependency (either library or language), please document *why* you chose to do this
  * We aren't against this per se, but whatever you do better work running `gradlew clean build` and you should have a good reason (aside from "I like it so I put it in")

In terms of functionality, please carefully read the JavaDocs for each method to get an idea of how they should work. If you notice something incorrect in the documentation, please let us know.

We've tried to make this as self-explanatory as possible, but if you are unclear about one or more of the objectives in this exercise please contact us right away.

That's really it. We're not looking to stump you or throw something impossible at you - just get an idea of how you approach well-defined problems in an existing codebase. There are no "gotchas" or requirements to be overly clever - just do your best and make your solution clean and easy to understand.

Gradle
============
We're using [Gradle](http://gradle.org) as our build tool. You should not need to install anything - just run `gradlew` and it will download what's needed the first time you run it.

You should not need to modify the build process at all. All we're looking for is that `gradlew clean build` passes all the tests. That's it.

If you do modify the build (to add a dependency, for example), please document what you did and why you did so.

Time
============
You should be able to complete this task in **about 2-3 hours**. If you run into trouble and it takes a little longer, no worries; but please document why it took longer.