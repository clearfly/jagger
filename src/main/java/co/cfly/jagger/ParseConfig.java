package co.cfly.jagger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ParseConfig
{
    public static void printUsage()
    {
        JarTextUtil usageTxt = new JarTextUtil();
        String usage = null;
        try
        {
            usage = usageTxt.dumpFile("usage.txt");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            usage = "ERROR RETRIEVING HELP FILE!";
        }
        System.out.println(usage);
        System.exit(0);
    }

    public static void processArgs(String[] args)
    {
        String processArgDebug = "Processing command-line arguments . . .\n";
        String configFile = null;
        if (args.length == 0)
        {
            printUsage();
        }
        else
        {
            for (String myArg : args)
            {
                if (myArg.length() <= 3)
                {
                    if (myArg.equals("-h") || myArg.equals("-?"))
                    {
                        printUsage();
                    }
                    else if (myArg.equals("-v"))
                    {
                        JaggerAgent.setDebugLevel(1);
                        processArgDebug = processArgDebug + "Command-line param: Debug level 1 enabled" + "\n";
                    }
                    else if (myArg.equals("-vv"))
                    {
                        JaggerAgent.setDebugLevel(2);
                        processArgDebug = processArgDebug + "Command-line param: Debug level 2 enabled" + "\n";
                    }
                }
                else
                {
                    String argParameter = myArg.substring(0, 2).toLowerCase();
                    String argValue = myArg.substring(3);
                    if (argParameter.equals("-c"))
                    {
                        configFile = argValue;
                        processArgDebug = processArgDebug + "Command-line param: Config file is: " + argValue + "\n";
                    }
                    else if (argParameter.equals("-u"))
                    {
                        JaggerAgent.setXmppUser(argValue);
                        processArgDebug = processArgDebug + "Command-line param: Username is: " + argValue + "\n";
                    }
                    else if (argParameter.equals("-d"))
                    {
                        JaggerAgent.setXmppDomain(argValue);
                        processArgDebug = processArgDebug + "Command-line param: Domain name is: " + argValue + "\n";
                    }
                    else if (argParameter.equals("-s"))
                    {
                        JaggerAgent.setXmppServer(argValue);
                        processArgDebug = processArgDebug + "Command-line param: Server is: " + argValue + "\n";
                    }
                    else if (argParameter.equals("-t"))
                    {
                        if (argValue != null)
                        {
                            JaggerAgent.setXmppPort(Integer.valueOf(argValue));
                            processArgDebug = processArgDebug + "Command-line param: Port is: " + argValue + "\n";
                        }
                    }
                    else if (argParameter.equals("-p"))
                    {
                        JaggerAgent.setXmppPassword(argValue);
                        processArgDebug = processArgDebug + "Command-line param: Password is: <hidden>\n";
                    }
                    else if (argParameter.equals("-r"))
                    {
                        JaggerAgent.setXmppResource(argValue);
                        processArgDebug = processArgDebug + "Command-line param: Resource is: " + argValue + "\n";
                    }
                }
            }
        }

        if (configFile != null)
        {
            processPropertiesFile(configFile);
        }

        if (JaggerAgent.getXmppUser() == null || JaggerAgent.getXmppDomain() == null || JaggerAgent.getXmppPassword() == null)
        {
            System.out.println("Incorrect usage:\n" + processArgDebug + "\n");
            printUsage();
        }
    }

    public static void processPropertiesFile(String configFile)
    {
        Properties properties = new Properties();
        try
        {
            properties.load(new FileInputStream(configFile));
        }
        catch (IOException e)
        {
            System.out.println("\nUnable to open config file!\n");
        }
        JaggerAgent.setXmppUser(properties.getProperty("username"));
        JaggerAgent.setXmppDomain(properties.getProperty("domain"));
        JaggerAgent.setXmppServer(properties.getProperty("server"));
        if (properties.getProperty("port") != null)
        {
            JaggerAgent.setXmppPort(Integer.valueOf(properties.getProperty("port")));
        }
        JaggerAgent.setXmppPassword(properties.getProperty("password"));
        JaggerAgent.setXmppResource(properties.getProperty("resource"));
        JaggerAgent.setXmppDescription(properties.getProperty("description"));
    }
}
