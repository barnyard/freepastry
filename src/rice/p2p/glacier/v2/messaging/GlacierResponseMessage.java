/*******************************************************************************

"FreePastry" Peer-to-Peer Application Development Substrate

Copyright 2002-2007, Rice University. Copyright 2006-2007, Max Planck Institute 
for Software Systems.  All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

- Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.

- Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

- Neither the name of Rice  University (RICE), Max Planck Institute for Software 
Systems (MPI-SWS) nor the names of its contributors may be used to endorse or 
promote products derived from this software without specific prior written 
permission.

This software is provided by RICE, MPI-SWS and the contributors on an "as is" 
basis, without any representations or warranties of any kind, express or implied 
including, but not limited to, representations or warranties of 
non-infringement, merchantability or fitness for a particular purpose. In no 
event shall RICE, MPI-SWS or contributors be liable for any direct, indirect, 
incidental, special, exemplary, or consequential damages (including, but not 
limited to, procurement of substitute goods or services; loss of use, data, or 
profits; or business interruption) however caused and on any theory of 
liability, whether in contract, strict liability, or tort (including negligence
or otherwise) arising in any way out of the use of this software, even if 
advised of the possibility of such damage.

*******************************************************************************/ 
package rice.p2p.glacier.v2.messaging;

import java.io.IOException;

import rice.*;
import rice.p2p.commonapi.*;
import rice.p2p.commonapi.rawserialization.*;
import rice.p2p.glacier.*;

public class GlacierResponseMessage extends GlacierMessage {
  public static final short TYPE = 13;

  protected FragmentKey[] keys;
  protected long[] lifetimes;
  protected boolean[] haveIt;
  protected boolean[] authoritative;

  public GlacierResponseMessage(int uid, FragmentKey key, boolean haveIt, long lifetime, boolean authoritative, NodeHandle source, Id dest, boolean isResponse, char tag) {
    this(uid, new FragmentKey[] { key }, new boolean[] { haveIt }, new long[] { lifetime }, new boolean[] { authoritative }, source, dest, isResponse, tag);
  }

  public GlacierResponseMessage(int uid, FragmentKey[] keys, boolean[] haveIt, long[] lifetimes, boolean[] authoritative, NodeHandle source, Id dest, boolean isResponse, char tag) {
    super(uid, source, dest, isResponse, tag);

    this.keys = keys;
    this.haveIt = haveIt;
    this.authoritative = authoritative;
    this.lifetimes = lifetimes;
  }

  public int numKeys() {
    return keys.length;
  }

  public FragmentKey getKey(int index) {
    return keys[index];
  }

  public boolean getHaveIt(int index) {
    return haveIt[index];
  }

  public boolean getAuthoritative(int index) {
    return authoritative[index];
  }

  public long getExpiration(int index) {
    return lifetimes[index];
  }

  public String toString() {
    return "[GlacierResponse for " + keys[0] + " ("+(numKeys()-1)+" more keys)]";
  }
  
  /***************** Raw Serialization ***************************************/
  public short getType() {
    return TYPE; 
  }
  
  public void serialize(OutputBuffer buf) throws IOException {
    buf.writeByte((byte)0); // version    
    super.serialize(buf);
    
    buf.writeInt(keys.length);
    for (int i = 0; i < keys.length; i++) {
      keys[i].serialize(buf); 
    }
    
    buf.writeInt(lifetimes.length);
    for (int i = 0; i < lifetimes.length; i++) {
      buf.writeLong(lifetimes[i]); 
    }    
    buf.writeInt(authoritative.length);
    for (int i = 0; i < authoritative.length; i++) {
      buf.writeBoolean(authoritative[i]); 
    }
    buf.writeInt(haveIt.length);
    for (int i = 0; i < haveIt.length; i++) {
      buf.writeBoolean(haveIt[i]); 
    }
  }

  public static GlacierResponseMessage build(InputBuffer buf, Endpoint endpoint) throws IOException {
    byte version = buf.readByte();
    switch(version) {
      case 0:
        return new GlacierResponseMessage(buf, endpoint);
      default:
        throw new IOException("Unknown Version: "+version);
    }
  }
    
  private GlacierResponseMessage(InputBuffer buf, Endpoint endpoint) throws IOException {
    super(buf, endpoint);

    keys = new FragmentKey[buf.readInt()];
    for (int i = 0; i < keys.length; i++) {
      keys[i] = new FragmentKey(buf, endpoint); 
    }
    
    lifetimes = new long[buf.readInt()];
    for (int i = 0; i < lifetimes.length; i++) {
      lifetimes[i] = buf.readLong(); 
    }
    authoritative = new boolean[buf.readInt()];
    for (int i = 0; i < authoritative.length; i++) {
      authoritative[i] = buf.readBoolean(); 
    }
    haveIt = new boolean[buf.readInt()];
    for (int i = 0; i < haveIt.length; i++) {
      haveIt[i] = buf.readBoolean(); 
    }
  }
}

