
VoxelSniper
============
The premier long-distance brush editor for [Bukkit](https://bukkit.org/), [Spigot](https://www.spigotmc.org/), [Paper](https://papermc.io/), and most Bukkit-based server distributions. The plugin is fully functional (according to our limited usage), and should be able to snipe most blocks. Please submit an issue if something is not working and I'll try my best to work it out.
This plugin is a fork created to just maintains the project updated to new minecraft versions. No warranty are provided about the fact the plugin is fully functional and future features will be added.

Minecraft Version
------------------
VoxelSniper was built against the PaperMC 1.18.2 API.

 - Minecraft 1.18.2 [Tested on PaperMC, 6.2.0-SNAPSHOT]

Compilation
-----------
Pre-compiled JARs are available in releases. Latest release is `6.2.0-SNAPSHOT`.

Fork use Gradle to handle dependencies.

Issue Tracker Notes
-------------------

How do I create a ticket the right way?

- Separate your reports. You think there is something wrong, but also want this new brush? Make life easier for us and create two tickets. We'd appreciate it big times.
- Don't tell us your story of life. We want facts and information. The more information about `the Problem` you give us, the easier it is for us to figure out what's wrong.
- Check the closed tickets first. Maybe someone created a similar ticket already. If you think it's unresolved, then give us more information on there instead.

### Bug Report

As said above I provide no warranty about the full functionality of the plugin, you can report issues, major should be fixed.

Make sure to not tell us your story of life. We want brief descriptions of what's wrong to get directly to fixing.
Also try to make the title describe briefly what's wrong and attach things like logs or screenshots to help illustrate the issue further.

Here is an example, where an imaginary brush that should create a ball, creates a cube:

Title: `Brush A creates Cube instead of Ball`

```
Expected behaviour:
Brush A should create a ball with radius 5 when I set it to brush A with brush size 5.

Experienced behaviour:
Brush A created a cube instead.

Additional Information:
CraftBukkit 1.3.2-R1.0
VoxelSniper 5.166.11
java -version output:
java version "1.7.0_07"
Java(TM) SE Runtime Environment (build 1.7.0_07-b11)
Java HotSpot(TM) Client VM (build 23.3-b01, mixed mode)
```

Additional Information like what java version the server runs on would be appriciated, but is not required at all times.

### Enhancement Request

As said above I provide no warranty about adding new features.

This is where imagination comes in, but make sure to keep as it easy for us. As mentioned, we don't want your story of life. We want to know what you think would be a good enhancement.

Here is an example of an enhancement request.

Title: `Brush that creates lines`

```
Enhancement Proposal:
Creating a brush that creates a line.

Suggested usage:
You click two points with your arrow and it will create a line with blocks.

Reason of proposal:
It would be useful, since off angle lines are sometimes hard to make.
```

Keep in mind that those are guidelines.
We will still look into stuff that does not follow these guidelines, but it will give you an idea what we want in a ticket and make our life easier.

Pull Requests
-------------

We do accept pull requests and enhancements from third parties. Guidelines how to submit your pull requests properly and how to format your code will come.

Some rough guidelines for now:

- Keep the number of commits to a minimum. We want to look over the commit and basically see what you've done.
- Coding guidelines should try to comply to the checkstyle rules (checkstyle.xml) but not blindly. Use your mind to make smart decissions.
- Give us a good description to what you've done.
- Try to submit one change in one pull request and try to link it to the issue in the tracker if possible.

[Original VoxelSniperWiki]: http://voxelwiki.com/minecraft/VoxelSniper/
[PaperMC]: https://papermc.io
[Gradle]: https://gradle.org
