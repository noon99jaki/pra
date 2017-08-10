package edu.cmu.lti.util.net;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Provides base single-threaded Server functionality. Properties of Server
 * objects are specified on construction with a Properties object. The
 * properties which Server objects pay attention to are:
 * 
 * <ul>
 * 
 * <li>Server.ServerSocketAddress - the InetSocketAddress to bind to
 * e.g. "localhost:4567" which should encode both server (hostname or IP
 * address) and port designation.</li>
 * 
 * <li>Server.ServerSocketBacklog - the "listen" backlog of the server socket,
 * which should be an integer greater than zero. Defaults to zero, which makes
 * the system choose the underlying kernel default.</li>
 * 
 * <li>Server.ServerSocketTimeout - time in milliseconds to wait on a blocking
 * call against a ServerSocket object before raising an exception. A value of
 * zero is interpreted as an infinite timeout.</li>
 * 
 * <li>Server.ClientSocketTimeout - time in milliseconds to wait on a blocking
 * call against a client Socket object before raising an exception. A value of
 * zero is interpreted as an infinite timeout.</li>
 * 
 * </ul>
 */
public abstract class Server implements Runnable
{
    protected static final Logger log = Logger.getLogger(Server.class);

    protected Properties properties;
    protected Thread thread;
    protected SocketAddress server_socket_address;
    protected int server_socket_backlog;
    protected int server_socket_timeout;
    protected ServerSocketChannel server_socket_channel;
    protected int client_socket_timeout;
    protected boolean serving;

    /**
     * Constructs a new Server object.
     * @param properties a Properties object containing configuration
     * information for the new Server object. All properties in the Properties
     * object not recognized by this class will be ignored.
     */
    public Server(Properties properties)
    {
	this.properties = properties;
	thread = null;
	server_socket_address = null;
	server_socket_backlog = 0;
	server_socket_timeout = 0;
	server_socket_channel = null;
	client_socket_timeout = 0;
	serving = false;
    }

    /**
     * Constructs a new Server object will an empty, non-null Properties object.
     */
    public Server()
    {
	this(new Properties());
    }

    /**
     * @param properties a Properties object from which this Server object
     * should extract its configuration.
     */
    public void setProperties(Properties properties)
    { this.properties = properties; }

    /**
     * @return the Properties object which this Server object extracts its
     * configuration from.
     */
    public Properties getProperties()
    { return properties; }

    /**
     * Starts the Server object in a new Thread.
     */
    public void start()
    {
	thread = new Thread(this, getClass().getName());
	thread.start();
    }

    /**
     * The main entry point for all Servers. This routine defines the general
     * processing cycle of all Server objects, including calls to all Strategy
     * interface "hooks" and principal server operations (e.g. binding a
     * ServerSocket to a given port, entering a main "accept" loop to process
     * incoming socket connections, etc).
     */
    public final void run()
    {
	try {
	    log.info("Starting");

	    try {
		// perform any validation of configuration options, or
		// manipulation of property values.
		configure();
	    } catch (Exception e) {
		throw new Exception("Configure failed", e);
	    }

	    try {
		// bind server to a live port
		bind();
	    } catch (Exception e) {
		throw new Exception("Bind failed", e);
	    }

	    try {
		// perform any initialization of internal server fields
		initialize();
	    } catch (Exception e) {
		throw new Exception("Initialization failed", e);
	    }

	    // once the server socket successfully binds to the port, we must
	    // remember to close it on any exceptional circumstances, so we wrap
	    // all following statements in a try-catch-finally statement. The
	    // finally block will handle closing of the server socket.
	    try {
		serving = true;
		accept();
	    } catch (InterruptedException e) {
		log.info("Server thread interrupted");
	    } catch (Exception e) {
		log.error("Fatal exception encountered", e);
	    } finally {
		serving = false;
		try {
		    close();
		} catch (Exception e) {
		    throw new Exception("Close failed", e);
		}
	    }

	} catch (Exception e) {
	    log.error("Fatal exception encountered", e);
	} finally {
	    try {
		shutdown();
	    } catch (Exception e) {
		log.error("Shutdown failed", e);
	    }
	}
    }

    /**
     * Parses and validates Server configuration from the Properties object
     * supplied to the Server object constructor.
     */
    protected void configure() throws Exception
    {
	log.info("Configuring");
	try {
	    server_socket_address = Utilities.parseInetSocketAddress
		(properties.getProperty
		 ("Server.ServerSocketAddress"));
	} catch (Exception e) {
	    throw new Exception
		("Failed to parse Server.ServerSocketAddress property", e);
	}
	try {
	    server_socket_backlog = Integer.parseInt
		(properties.getProperty
		 ("Server.ServerSocketBacklog", "0"));
	    if (server_socket_backlog < 0)
		throw new Exception
		    ("Non-negative integer required");
	} catch (Exception e) {
	    throw new Exception
		("Failed to parse Server.ServerSocketBacklog property", e);
	}
	try {
	    server_socket_timeout = Integer.parseInt
		(properties.getProperty
		 ("Server.ServerSocketBacklog", "0"));
	    if (server_socket_timeout < 0)
		throw new Exception
		    ("Non-negative integer required");
	} catch (Exception e) {
	    throw new Exception
		("Failed to parse Server.ServerSocketBacklog property", e);
	}
	try {
	    client_socket_timeout = Integer.parseInt
		(properties.getProperty
		 ("Server.ClientSocketTimeout", "30000"));
	    if (client_socket_timeout < 0)
		throw new Exception
		    ("Non-negative integer required");
	} catch (Exception e) {
	    throw new Exception
		("Failed to parse Server.ClientSocketTimeout property", e);
	}
    }

    /**
     * Binds the ServerSocket to the InetServerAddress specified in
     * configuration.
     */
    protected void bind() throws Exception
    {
	log.info("Binding server socket to "+server_socket_address);
	server_socket_channel = ServerSocketChannel.open();
	ServerSocket server_socket = server_socket_channel.socket();
	server_socket.bind(server_socket_address, server_socket_backlog);
	server_socket.setSoTimeout(server_socket_timeout);
    }

    /**
     * Initializes all Server fields, in this case simply constructing the
     * ServerSocket object.
     */
    protected void initialize() throws Exception
    {
	log.info("Initializing");
    }

    /**
     * Defines the main ServerSocket accept loop.
     */
    protected void accept() throws Exception
    {
	log.info("Accepting clients");
	while (serving) {
	    SocketChannel sc_client = server_socket_channel.accept();
	    Socket s_client = sc_client.socket();
	    try {
		log.info("Connect from "+s_client.getRemoteSocketAddress());
		s_client.setSoTimeout(client_socket_timeout);
		process(sc_client);
	    } catch (FatalException e) {
		log.error("Fatal exception encountered", e);
		serving = false;
	    } catch (Exception e) {
		log.error("Failed to process client socket", e);
	    }
	}
    }

    /**
     * Defines how client SocketChannel objects are processed.
     * @param sc_client a SocketChannel object to process.
     */
    protected void process(SocketChannel sc_client) throws Exception
    {
	Socket s_client = sc_client.socket();
	try {
	    while (serving) {
		Message request = newMessage();
		while (!request.read(sc_client)) {}
		Message response = process(request);
		while (!response.write(sc_client)) {}
	    }
	} catch (EmptyMessageException e) {
	    log.info("Client disconnected");
	} finally {
	    try {
		log.info("Closing client socket"+s_client.getRemoteSocketAddress());
		s_client.close();
	    } catch (Exception e) {
		log.error("Failed to close client socket", e);
	    }
	}
    }

    /**
     * @return a new Message object which the server may use to read in a
     * request from the client.
     */
    protected abstract Message newMessage();

    /**
     * Processes a client request Message and returns a response Message to be
     * written back to the client.
     * @param request a Message object containing some request for the server to
     * process.
     * @return a Message object containing the server response to the client
     * request.
     * @throws FatalException to signal Server termination.
     * @throws Exception on any other exceptional circumstance.
     */
    protected abstract Message process(Message request)
	throws FatalException, Exception;

    /**
     * Closes the ServerSocketChannel object and any other communications
     * channels.
     */
    protected void close() throws Exception
    {
	log.info("Closing");
	server_socket_channel.close();
    }

    /**
     * Takes care of any final cleanup tasks just before the Server completes
     * its run cycle. This method does not remove any configuration data
     * necessary to re-start the server with a subsequent call to the start()
     * method, but attempts to free as many resources as it can.
     */
    protected void shutdown() throws Exception
    {
	log.info("Stopping");
	thread = null;
	server_socket_address = null;
	server_socket_backlog = 0;
	server_socket_timeout = 0;
	server_socket_channel = null;
	client_socket_timeout = 0;
	serving = false;
    }
    
    /**
     * @return boolean flag value specifying "serving" state of this Server
     * object.
     */
    public synchronized boolean isServing()
    {
	return serving;
    }

    /**
     * Sets the serving state of this Server object to false, and then
     * interrupts the server thread to jump out of blocking operations.
     */
    public synchronized void stop()
    {
	log.info("Interrupting");
	serving = false;
	thread.interrupt();
    }

    /**
     * @return the Thread in which the Server is running.
     */
    public Thread getThread()
    {
	return thread;
    }
}
