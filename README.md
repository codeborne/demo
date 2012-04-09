Mobi
=========

Demo project for demonstrating [Mobile-ID](http://github.com/codeborne/mobileid) and [Selenide](http://github.com/codeborne/selenide) functionality.


How to run from IDE
=========
Make sure you have play 1.2.4 installed in ${PLAY_HOME}

To retrieve all required dependencies into the lib/ directory, run:

    play dependencies --sync

Then, in order to generate a project for your IDE:

    play idealize

or

    play eclipsify


Run java class **play.server.Server** with the following parameters:

    -javaagent:${PLAY_HOME}/framework/play-1.2.4.jar
    -XX:-UseSplitVerifier
    -Dplay.debug=yes
    -Dapplication.path=.
