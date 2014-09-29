package com.fast.client;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import com.fast.pool.PoolAdapter;

public class FastDfsPool extends PoolAdapter{

	public FastDfsPool(Config poolConfig, PoolableObjectFactory factory) {
		super(poolConfig, factory);
		// TODO Auto-generated constructor stub
	}

}
