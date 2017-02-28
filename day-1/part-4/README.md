## Continuous deployment and integration using Cloud Foundry

In this session we will explore how to build and deploy cloud native applications using Cloud Foundry.

Requirements:

- JDK 1.8 or later
- Gradle 2.3+ or Maven 3.0+
- IDE of choice
- [Virtual Box](https://www.virtualbox.org/wiki/Downloads)
- [Vagrant](https://www.vagrantup.com/downloads.html)
- [Cloud Foundry CLI](https://github.com/cloudfoundry/cli#downloads)
- [PCF Dev](https://network.pivotal.io/products/pcfdev)

### Topics

In this session we will explore how to set up a local Cloud Foundry environment using PCF Dev.

Topics:

* Introduction to Cloud Foundry
* Provisioning and binding to services
* Pushing applications to Cloud Foundry
* Service Brokers
* Spring Cloud Services
* Integrating Microservices on Cloud Foundry

### Getting setup

This section will require some extra steps to get your development environment setup. We're going to need to have Cloud Foundry running locally, as well as Concourse. This will require that we have the latest version of Virtual Box installed.

#### Virtual Box

The first thing we need is Virtual Box. Go to the download page located here: https://www.virtualbox.org/wiki/Downloads

Download and install the latest version of Virtual Box for your machine.

#### Vagrant

The next thing we're going to need is Vagrant. After Virtual Box has finished installing, go to the Vagrant download page located here: https://www.vagrantup.com/downloads.html

Download and install the latest version of Vagrant for your machine.

#### Cloud Foundry CLI

Before we can start talking to Cloud Foundry from our local machine, we'll need to grab and install the latest command line tools. You can find installation instructions here: https://github.com/cloudfoundry/cli#downloads

If you're on macOS, you can easily grab and install the binaries using Homebrew.


    $ brew tap cloudfoundry/tap
    $ brew install cf-cli

After you have installed the CF CLI, make sure you can open up a terminal or console window and run the following command.

    $ cf -v
    cf version 6.25.0+787326d95.2017-02-28

You should see the same or similar version as a response. If so, you're all set!

#### PCF Dev (Cloud Foundry)

What if you had a cloud environment on your local machine that you could use to learn and test your cloud-native applications on Cloud Foundry? That's the idea of PCF Dev -- a local Cloud Foundry environment for developers from Pivotal.

After Virtual Box and Vagrant have finished installing, go login or sign up for a Pivotal Network account: https://network.pivotal.io/registrations/new

If you're already signed up, you can download PCF Dev from here: https://network.pivotal.io/products/pcfdev

After you've downloaded the binary for PCF Dev from the download page on Pivotal Network, you'll need to open up a terminal or console to install the PCF Dev plugin.

    $ ./pcfdev-v0.24.0+PCF1.9.0-osx
    Plugin successfully installed. Current version: 0.24.0. For more info run: cf dev help

After locating and executing the PCF Dev binary, you should see the message above. The PCF Dev environment is actually a plugin for the CF CLI we installed in the previous step. Now all we have to do to spin up our local Cloud Foundry environment is to run the following command.

    $ cf dev start

You'll be asked to enter your Pivotal network credentials, which you should have handy from earlier. After authenticating on the CLI, you may be presented with an EULA for the PCF Dev beta. Read and then agree, to start the VM download and installation of PCF Dev.

Congrats! You've just installed a cloud to your local machine.

#### Concourse CI

The next thing we'll need to do is to set up a local environment for Concourse CI. Concourse is a continuous integration and deployment tool for cloud-native applications. We'll use this tool to build a continuous delivery pipeline to build and integration test our applications before deploying to Cloud Foundry.

The fastest way to get up and running with a local Concourse environment is to use Vagrant. Thankfully we installed Vagrant earlier.

The installation instructions can be found here: https://concourse.ci/vagrant.html

Open up a new terminal or console and run the following commands:

    $ vagrant init concourse/lite
    $ vagrant up

It's recommended you do this from a directory where you'll start Concourse from in the future. This is because the `vagrant init` command will download a `Vagrantfile` with the recipe for spinning up a Concourse VM in Virtual Box. Every time you want to stop or start the Concourse VM you'll return to this same directory.

After Vagrant is finished downloading and running the VM in Virtual Box, you'll be able to browse to the Concourse dashboard at http://192.168.100.4:8080/

You should see a web page that tells you to install the CLI tools. Go ahead and install the CLI tools for your platform.

The binary you'll download is called `fly` and you should install it to your environment so that it will always be available from your terminal or console. For macOS you can simply move it to your `/usr/local/bin`.

    $ chmod +x fly
    $ mv fly /usr/local/bin

You're all set. Try running the following command from a new terminal or console window.

    $ fly -t lite login -c http://192.168.100.4:8080

You should now be signed in. Just to test the deployment of a simple pipeline, you can save the following snippet as `deploy-hello.yml`.

    jobs:
    - name: hello-world
      plan:
      - task: say-hello
        config:
          platform: linux
          image_resource:
            type: docker-image
            source: {repository: ubuntu}
          run:
            path: echo
            args: ["Hello, world!"]

Now open a terminal window and go to the directory where you saved `deploy-hello.yml`. Run the following command.

    $ fly -t lite set-pipeline -p hello-world -c deploy-hello.yml

Follow the prompts and you'll then be able to see your pipeline running on the dashboard located here: http://192.168.100.4:8080/teams/main/pipelines/hello-world

To run the deployment job you can click the play button on the dashboard or head back to your CLI and run the following command:

    $ fly -t lite unpause-pipeline -p hello-world

Now head back to your dashboard in the browser and you should see the `hello-world` build running!
