package edu.cmu.lti.util.net;

import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * The Message interface is meant to describe a very basic interface for reading
 * (and writing) atomic communication elements (messages) in a client-server
 * protocol from (and to) ByteChannels. The structure of a Message object can be
 * arbitrary, but is likely to contain some kind of storage field for the data
 * "payload" of the message. This storage field, possibly a ByteBuffer object,
 * could be used as the destination for data read from, or the origin for data
 * written to, some Channel.
 */
public interface Message
{
    /**
     * Reads bytes from a ReadableByteChannel into this Message object,
     * returning true if the Message is complete after the call to read, or
     * false if more data is required to complete the Message.
     *
     * <p>An implementation of this interface should not assume that a single
     * call to this method will supply the Message object with all the data it
     * needs to form a valid Message object. In the context of a select-based
     * server for instance, this method could be called many times before all
     * Message data is read from the Channel.</p>
     *
     * @param rbc the source Channel to read data from.
     * @return true if the Message has been read in full from the
     * ReadableByteChannel.
     * @throws EmptyMessageException if end-of-stream is encountered on the
     * ReadableButeChannel before any data has been read for this Message. This
     * is likely to signal normal client disconnect.
     * @throws Exception for all other exceptional circumstances.
     */
    public boolean read(ReadableByteChannel rbc)
	throws EmptyMessageException, Exception;

    /**
     * Writes bytes from this Message object to a WriteableByteChannel,
     * returning true if the Message has been written completely to the Channel,
     * or false if more data must be written to complete transmission of the
     * Message.
     * 
     * <p>An implementation of this interface should not assume that a single
     * call to this method will successfully write all Message data to the
     * Channel. In the context of a select-based server for instance, this
     * method could be called many times before all Message data is written to
     * the Channel.</p>
     *
     * @param wbc the destination Channel to write data to.
     * @return true if the Message has been written in full to the
     * WritableByteChannel.
     * @throws EmptyMessageException if this Message object is empty when this
     * method is called.
     * @throws Exception for all exceptional circumstances.
     */
    public boolean write(WritableByteChannel wbc)
	throws EmptyMessageException, Exception;
}
