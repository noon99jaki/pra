package edu.cmu.lti.util.system;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * A generic ObjectPool implementation which allows synchronization of access to
 * a pool of objects across multiple threads of control. <p/> Example usage:
 * 
 * <pre>
 * class MyObjectFactory implements ObjectPool&lt;MyObject&gt;.Factory
 * {
 *     public construct() throws Exception
 *     {
 *         return new MyObject();
 *     }
 * }
 * 
 * ObjectPool&lt;MyObject&gt; pool = new ObjectPool&lt;MyObject&gt;(new MyObjectFactory());
 * 
 * MyObject myObject = null;
 * try {
 *     myObject = pool.getInstance();
 *     // do something with the instance
 * } catch (Exception e) {
 *     // do exception handling here
 *     if (myObject.isBad()) {
 *         pool.disassociateInstance(myObject);
 *         myObject = null;
 *     }
 * } finally {
 *     if (myObject != null) pool.putInstance(myObject);
 * }
 * </pre>
 * 
 * @author Andy Schlaikjer
 */
public class ObjectPool<T>
{
    /**
     * a Factory instance used to generate more objects of type T if pool
     * constraints allow.
     */
    protected Factory<T> factory;
    
    /**
     * the queue for pool instances.
     */
    protected LinkedList<T> instance_queue;
    
    /**
     * a mapping of pool objects to the Threads currently borrowing them. These
     * Threads are refered to as "Borrowers".
     */
    protected Map<T, Thread> instance_borrowers;
    
    /**
     * max objects in the pool. 
     */
    protected int num_max_instances;
    
    /**
     * number of pool objects to construct on pool construction.
     */
    protected int num_initial_instances;
    
    /**
     * current number of instances in pool. 
     */
    protected int num_instances;

    /**
     * Constructs a new ObjectPool instance, and populates the pool with
     * <code>num_initial_instances</code> objects generated with the provided
     * ObjectPool.Factory.
     * @param factory the factory with which new pool instances are created.
     * @param num_max_instances the maximum number of objects allowed in the
     * pool.
     * @param num_initial_instances the initial number of objects to construct
     * and add to the pool.
     * @throws Exception if something bad happens while constructing initial
     * pool instances with the factory.
     */
    public ObjectPool(Factory<T> factory,
                      int num_max_instances,
                      int num_initial_instances) throws Exception
    {
        this.factory = factory;
        this.num_max_instances = num_max_instances;
        this.num_initial_instances = num_initial_instances;
        instance_queue = new LinkedList<T>();
        for (int i = 0; i < num_initial_instances; ++i)
            instance_queue.addLast(factory.construct());
        num_instances = num_initial_instances;
        instance_borrowers = new HashMap<T, Thread>();
    }

    /**
     * Constructs a new ObjectPool using the given factory, setting
     * <code>num_max_instances</code> to 10 and
     * <code>num_initial_instances</code> to 1.
     * @param factory the factory with which new pool instances are created.
     * @throws Exception if something bad happens while constructing initial
     * pool instances with the factory.
     */
    public ObjectPool(Factory<T> factory) throws Exception
    {
        this(factory, 10, 1);
    }

    /**
     * Returns an object from the pool. If the pool has been exhausted by
     * previous calls to this method, but the <code>max_num_instances</code>
     * threshold has not been met, a new object is constructed with the internal
     * factory and returned to the caller. Alternately, if
     * <code>max_num_instances</code> has been met, this call will block until
     * another thread of control returns an instance to this pool via
     * {@link #putInstance(Object)}. <p/> Please be aware that repeated calls
     * to this method will depleat the pool of instances and cause further calls
     * to block. Client code should return instances retrieved by this method as
     * quickly as possible. <p/> If while using the returned object it "goes
     * bad" and must be permanently disassociated with this ObjectPool, make
     * sure to call {@link #disassociateInstance(Object)} so the pool doesn't
     * expect it to be returned at some point in the future. <p/> Note that once
     * this method returns, the calling Thread is registered as a "borrower"
     * with this ObjectPool, and is subject to calls to
     * {@link #interruptBorrowers()} until the instance is returned to this
     * ObjectPool via {@link #putInstance(Object)}.
     * @return an object from the pool.
     * @throws Exception if something bad happens while constructing a new pool
     * instance.
     */
    public synchronized T getInstance() throws Exception
    {
        T instance = null;
        while (instance_queue.isEmpty()) {
            if (num_instances < num_max_instances) {
                instance_queue.addLast(factory.construct());
                num_instances++;
            } else {
                wait();
            }
	}
	instance = instance_queue.removeFirst();
	instance_borrowers.put(instance, Thread.currentThread());
	notifyAll();
        return instance;
    }

    /**
     * Returns an object to the pool. The object must be associated with this
     * ObjectPool, otherwise an IllegalArgumentException will be thrown.
     * @param instance an instance of class T which was retrieved from this
     * ObjectPool by a prior call to {@link #getInstance()}.
     */
    public synchronized void putInstance(T instance)
    {
        if (!instance_borrowers.containsKey(instance)) throw new IllegalArgumentException(
            "The given instance is not associated with this ObjectPool");
        instance_queue.addLast(instance);
        instance_borrowers.put(instance, null);
        notifyAll();
    }

    /**
     * Associates an object with this ObjectPool, adding the object to the pool
     * for subsequent retrieval via the {@link #getInstance()} method. The given
     * instance must not already be associated with this ObjectPool, otherwise
     * an IllegalArgumentException will be thrown.
     * @param instance
     */
    public synchronized void associateInstance(T instance)
    {
        if (instance_borrowers.containsKey(instance)) throw new IllegalArgumentException(
            "The given instance is already associated with this ObjectPool");
        instance_borrowers.put(instance, null);
        putInstance(instance);
        num_instances++;
        notifyAll();
    }
    
    /**
     * Disassociates an object from this pool, permanently decreasing this
     * pool's instance count by one. The given object must be associated with
     * this ObjectPool, otherwise an IllegalArgumentException will be thrown.
     * <p/> If an instance "goes bad" and you'd like to remove it from the pool,
     * make sure to call this method so the pool isn't expecting you to return
     * the bad instance at some future time.
     * @param instance the object to disassociate from this ObjectPool.
     */
    public synchronized void disassociateInstance(T instance)
    {
        if (!instance_borrowers.containsKey(instance)) throw new IllegalArgumentException(
            "The given instance is not registered with this ObjectPool");
        instance_queue.remove(instance);
        instance_borrowers.remove(instance);
        num_instances--;
        notifyAll();
    }
    
    /**
     * Interrupts all Thread objects which are currently "borrowing" objects
     * from this pool (a.k.a. "Borrowers"). Care must be taken when calling this
     * method; If the calling thread is currently borrowing an object from this
     * ObjectPool, then an immediate {@link InterruptedException} is raised.
     */
    public void interruptBorrowers()
    {
        for (Thread thread : instance_borrowers.values())
            if (thread != null) thread.interrupt();
    }
    
    public void destroy()
    {
        interruptBorrowers();
        instance_borrowers.clear();
        instance_queue.clear();
        num_instances = 0;
        factory = null;
    }
    
    /**
     * A factory interface for pool objects.
     * @param <T> The object type this Factory constructs.
     */
    public static interface Factory<T>
    {
        /**
         * @return a new instance of type T.
         * @throws Exception if something bad happens during construction.
         */
        public T construct() throws Exception;
    }
}
