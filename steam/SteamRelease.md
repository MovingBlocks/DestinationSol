Steam Release Process
------------
Here is a quick run down of the steam build process and instructions on how to manually upload a new build should anyone need to do it. The future goal is to automate this process.

#### Prerequisites
1. You need to be a Steamworks developer with build permissions on the Destination Sol app.
2. Download the Steamworks SDK
3. Run the steamcmd in tools -> Content Builder. The first run downloads and installs some extra files.
4. I highly recommend reading the documentation: https://partner.steamgames.com/documentation/steampipe

#### Upload Process
1. Copy the build scripts from the Destination Sol repo to the steam-sdk directory at: steam-sdk/tools/ContentBuilder/scripts
2. Get the build you want to use. This is most likely the latest build from Jenkins: http://jenkins.movingblocks.net/job/DestinationSol/
3. Unzip the file and copy the DestinationSol folder into the steam-sdk directory at: steam-sdk/tools/ContentBuilder/content
4. Edit the script file app_build_342980.vdf and add a message to the "desc" field. This helps identify the build that you are uploading.
In most cases you can comment out depots 342982, 342983 and 342984 as these are the JRE for the different operating systems and only need to be included if they have been modified. If you are updating the JRE it should be kept in sync with Terasology by using the [TerasologyJRE](https://github.com/MovingBlocks/TerasologyJRE) tools.
5. From a terminal/command prompt at the ContentBuilder directory, run the following command. Replace username and password with your steam username and password. Change the steamcmd path so it's appropriate for your OS:
bash ./builder_osx/steamcmd.sh +login username password +run_app_build ../scripts/app_build_342980.vdf +quit
6. This will upload the files and this will show as a new build in Steamworks under the Builds tab.

#### Set Beta Branch to Live
The next steps depend on what you want to do. The most common approach would be to promote that build to Beta. This is easily achieved by:
1. Go to the Steamworks Builds tab for the app.
2. Click the "Select an app branch" in the drop down of the newly uploaded build
3. Select the Beta branch and click "Preview Change."
4. Add a comment about the release
5. Click "Set Build Live Now" to make the build live as the new Beta.
