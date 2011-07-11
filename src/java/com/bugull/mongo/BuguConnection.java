/**
 * Copyright (c) www.bugull.com
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bugull.mongo;

import com.bugull.mongo.cache.ClearExpiredCacheTask;
import com.mongodb.DB;
import com.mongodb.Mongo;
import java.net.UnknownHostException;
import java.util.Timer;
import org.apache.log4j.Logger;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class BuguConnection {
    
    private final static Logger logger = Logger.getLogger(BuguConnection.class);
    
    private static BuguConnection instance = new BuguConnection();
    
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private DB db;
    
    private Timer timer;
    private long cacheTimeout;
    
    private BuguConnection(){
        
    }
    
    public static BuguConnection getInstance(){
        return instance;
    }
    
    public void connect(String host, int port, String database, String username, String password){
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        connect();
    }

    public void connect(){
        Mongo mongo = null;
        try{
            mongo = new Mongo(host, port);
        }catch(UnknownHostException e){
            logger.error(e.getMessage());
        }
        db = mongo.getDB(database);
        boolean auth = db.authenticate(username, password.toCharArray());
        if(auth){
            logger.info("Connected to mongodb successfully!");
        }else{
            db = null;
            logger.error("Connect to mongodb failed! Failed to authenticate!");
        }
    }
    
    public void setCacheTimeout(long cacheTimeout){
        this.cacheTimeout = cacheTimeout;
        close();
        timer = new Timer();
        timer.schedule(new ClearExpiredCacheTask(), 60L*1000L, 2L*60L*1000L);
    }
    
    public long getCacheTimeout(){
        return cacheTimeout;
    }
    
    public void close(){
        if(timer != null){
            timer.cancel();
        }
    }
    
    public void setHost(String host){
        this.host = host;
    }
    
    public void setPort(int port){
        this.port = port;
    }
    
    public void setDatabase(String database){
        this.database = database;
    }
    
    public void setUsername(String username){
        this.username = username;
    }
    
    public void setPassword(String password){
        this.password = password;
    }

    public DB getDB(){
        return db;
    }
    
}
