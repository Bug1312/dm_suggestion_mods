// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_7.network;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CallInfo implements Serializable {
	private static final long serialVersionUID = -2830805187524157459L;

	public static final Set<CallInfo> CALL_LIST = new HashSet<>();

	public final Integer id;
	public final UUID owner;
	
	public CallInfo(Integer id, UUID owner) {
		this.id = id;
		this.owner = owner;
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) || (obj instanceof CallInfo && this.id == ((CallInfo) obj).id);
	}
	
}
