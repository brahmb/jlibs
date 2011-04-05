/**
 * JLibs: Common Utilities for Java
 * Copyright (C) 2009  Santhosh Kumar T <santhosh.tekuri@gmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package jlibs.core.nio.channels;

import jlibs.core.nio.ClientChannel;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Santhosh Kumar T
 */
public class ClientOutputChannel extends OutputChannel{
    public ClientOutputChannel(ClientChannel client){
        this(new DefaultNIOSupport(client, ClientChannel.OP_WRITE));
    }

    public ClientOutputChannel(NIOSupport nioSupport){
        super(nioSupport);
    }

    @Override
    protected boolean activateInterest(){
        return true;
    }

    private ByteBuffer writeBuffer;

    @Override
    protected int onWrite(ByteBuffer src) throws IOException{
        int wrote = src.remaining();
        writeBuffer = src.duplicate();
        src.position(src.limit());
        return wrote;
    }

    @Override
    protected void writePending() throws IOException{
        if(writeBuffer!=null){
            nioSupport.process(writeBuffer);
            if(!writeBuffer.hasRemaining())
                writeBuffer = null;
        }
    }

    @Override
    public Status status(){
        return writeBuffer==null ? Status.COMPLETED : Status.NEEDS_OUTPUT;
    }
}
