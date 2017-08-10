package edu.cmu.lti.util.net;

import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

/**
 * This class provides base thread-pool server functionality. A main server
 * thread runs a tight loop around the blocking <code>accept</code> call, and
 * adds all new client <code>SocketChannel</code> objects to a central client
 * connection set, as well as a client request queue. A specific number of
 * worker threads ("RequestHandler" objects) are initialized on server startup,
 * all of which attempt to dequeue client sockets from the client request queue
 * for further processing. If the set of client connections gets too large, the
 * main server thread will block until clients have been disconnected and
 * removed from the connection set. During this time, new connections will
 * accumulate according to the underlying system's server socket library, and
 * eventually be rejected when too many new, unaccepted connections build up.
 *
 * <p>ThreadedServer objects are constructed, like Server objects, with a
 * Properties object containing all pertinent configuration. The properties
 * which ThreadedServer objects pay attention to are:</p>
 *
 * <ul>
 *
 * <li>ThreadedServer.MaxClientSetSize - the maximum number of Socket objects to
 * accept for request handling before blocking on additional calls to
 * add. Default is 10.</li>
 *
 * <li>ThreadedServer.NumRequestHandlers - the number of RequestHandler Thread
 * objects to instantiate. These Threads attempt to dequeue SocketChannel
 * objects from the client request queue for further processing. Default is
 * 3.</li>
 *
 * </ul>
 */
public abstract class ThreadedServer extends Server
{
    protected int max_connection_set_size;
    protected HashSet sc_connection_set;
    protected LinkedList sc_request_queue;
    protected int num_request_handlers;
    protected ThreadGroup request_handler_group;
    protected LinkedList request_handler_list;

    /**
     * Constructs a new ThreadedServer object.
     * @param properties a Properties object containing configuration
     * information for the new Server object. All properties in the Properties
     * object not recognized by this class will be ignored.
     */
    public ThreadedServer(Properties properties) throws Exception
    {
	super(properties);
	max_connection_set_size = 0;
	sc_connection_set = null;
	sc_request_queue = null;
	num_request_handlers = 0;
	request_handler_group = null;
    }

    /**
     * Parses and validates Server configuration from the Properties object
     * supplied to the Server object constructor.
     */
    protected void configure() throws Exception
    {
	super.configure();
	try {
	    max_connection_set_size = Integer.parseInt
		(properties.getProperty
		 ("ThreadedServer.MaxClientSetSize", "10"));
	    if (max_connection_set_size < 1)
		throw new Exception
		    ("Positive integer required");
	} catch (Exception e) {
	    throw new Exception
		("Failed to parse ThreadedServer.MaxClientSetSize", e);
	}
	try {
	    num_request_handlers = Integer.parseInt
		(properties.getProperty
		 ("ThreadedServer.NumRequestHandlers", "3"));
	    if (num_request_handlers < 1)
		throw new Exception
		    ("Positive integer required");
	} catch (Exception e) {
	    throw new Exception
		("Failed to parse ThreadedServer.NumRequestHandlers", e);
	}
    }

    /**
     * Initializes all Server fields and constructs and starts all ServerThread
     * objects.
     */
    protected void initialize() throws Exception
    {
	super.initialize();
	sc_connection_set = new HashSet();
	sc_request_queue = new LinkedList();
	request_handler_group = new ThreadGroup("ServerThreads");
	request_handler_group.setDaemon(true);
	request_handler_list = new LinkedList();
	String name = getClass().getName()+"-";
	for (int i = 0; i < num_request_handlers; i++) {
	    RequestHandler request_handler = new RequestHandler(this, name+i);
	    request_handler.start();
	    request_handler_list.add(request_handler);
	}
    }

    /**
     * Defines how client SocketChannel objects are processed.
     */
    protected void process(SocketChannel sc_client) throws Exception
    {
	synchronized (sc_connection_set) {
	    while (sc_connection_set.size() >= max_connection_set_size)
		sc_connection_set.wait();
	    sc_connection_set.add(sc_client);
	    // test for successful add of SocketChannel object to Set should not
	    // be needed here. Only in a "debug" version of this class might it
	    // be helpful.
	    //   boolean added = sc_connection_set.add(sc_client);
	    sc_connection_set.notifyAll();
	}
	synchronized (sc_request_queue) {
	    sc_request_queue.addLast(sc_client);
	    sc_request_queue.notifyAll();
	}
    }

    /**
     * Processes a client request Message and returns a response Message to be
     * written back to the client.
     * @param request a Message object containing some request for the server to
     * process.
     * @return a Message object containing the server response to the client
     * request.
     * @throws ThreadFatalException to signal RequestHandler termination.
     * @throws FatalException to signal Server termination.
     * @throws Exception on any other exceptional circumstance.
     */
    protected abstract Message process(Message request)
	throws ThreadFatalException, FatalException, Exception;

    /**
     * Objects of this class handle reading and processing of requests from
     * clients. When 'run', a RequestHandler will attempt to dequeue a
     * SocketChannel object from the client request queue and handle request
     * processing for that SocketChannel object.
     */
    protected class RequestHandler extends Server
    {
	ThreadedServer server;
	String name;
	
	public RequestHandler(ThreadedServer server, String name)
	{
	    super();
	    this.server = server;
	    this.name = name;
	}

	public void start()
	{
	    thread = new Thread(request_handler_group, this, name);
	    thread.setDaemon(true);
	    thread.start();
	}

	protected void configure() throws Exception {}
	protected void bind() throws Exception {}
	protected void initialize() throws Exception {}
	protected void close() throws Exception {}
	protected void shutdown() throws Exception {}
	protected Message newMessage() { return null; }
	protected Message process(Message request) throws Exception { return null; }

	protected void accept() throws Exception
	{
	    log.info("Monitoring request queue");
	    while (serving) {
		// dequeue a SocketChannel object from the request queue. This
		// is a blocking operation, as if the request queue is empty,
		// we'd like this thread to wait until another thread (the main
		// server thread) populates the queue with more SocketChannel
		// objects.
		SocketChannel sc_client;
		synchronized (sc_request_queue) {
		    while (sc_request_queue.size() == 0)
			sc_request_queue.wait();
		    sc_client = (SocketChannel) sc_request_queue.removeFirst();
		    sc_request_queue.notifyAll();
		}
		Socket s_client = sc_client.socket();
		boolean close_client_connection = false;
		boolean stop_thread = false;
		boolean stop_server = false;
		try {
		    process(sc_client);
		    synchronized (sc_request_queue) {
			sc_request_queue.addLast(sc_client);
			sc_request_queue.notifyAll();
		    }
		} catch (EmptyMessageException e) {
		    log.info("Client disconnected");
		    close_client_connection = true;
		} catch (ThreadFatalException e) {
		    log.error("Thread fatal exception encountered", e);
		    close_client_connection = stop_thread = true;
		} catch (FatalException e) {
		    log.error("Fatal exception encountered", e);
		    close_client_connection = stop_thread = stop_server = true;
		} catch (Exception e) {
		    log.error("Failed to process client socket", e);
		    close_client_connection = true;
		}
		if (close_client_connection) {
		    try {
			log.info("Closing client socket "+s_client.getRemoteSocketAddress());
			if (s_client.isClosed()) {
			    log.info("Client socket already closed");
			} else {
			    s_client.close();
			}
		    } catch (Exception e) {
			log.error("Failed to close client socket", e);
		    }
		    synchronized (sc_connection_set) {
			sc_connection_set.remove(sc_client);
			sc_connection_set.notifyAll();
		    }
		}
		if (stop_thread) {
		    serving = false;
		}
		if (stop_server) {
		    server.stop();
		}
	    }
	}

	protected void process(SocketChannel sc_client)
	    throws EmptyMessageException, ThreadFatalException, FatalException, Exception
	{
	    Message request = server.newMessage();
	    while (!request.read(sc_client)) {}
	    Message response = server.process(request);
	    while (!response.write(sc_client)) {}
	}
    }

    /**
     * Closes all client SocketChannel objects, as well as the
     * ServerSocketChannel object.
     */
    protected void close() throws Exception
    {
	super.close();
	request_handler_group.interrupt();
	synchronized (sc_connection_set) {
	    Iterator i_sc_connection_set = sc_connection_set.iterator();
	    while (i_sc_connection_set.hasNext()) {
		SocketChannel sc_client = (SocketChannel) i_sc_connection_set.next();
		try {
		    sc_client.socket().close();
		} catch (Exception e) {
		    log.error("Failed to close client socket", e);
		}
	    }
	}
    }

    protected void shutdown() throws Exception
    {
	super.shutdown();
	max_connection_set_size = 0;
	sc_connection_set = null;
	sc_request_queue = null;
	num_request_handlers = 0;
	request_handler_group = null;
    }
    
}
