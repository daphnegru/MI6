package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.initializeMI6;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    public static void main(String[] args) {
        if (args.length!=0){
            initializeMI6 m = new initializeMI6();
            m.run(args[0]);
            m.outputFiles(args[1],args[2]);
        }
    }

}