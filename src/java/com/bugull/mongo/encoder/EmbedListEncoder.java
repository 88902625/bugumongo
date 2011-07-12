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

package com.bugull.mongo.encoder;

import com.bugull.mongo.BuguMapper;
import com.bugull.mongo.annotations.EmbedList;
import com.mongodb.DBObject;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class EmbedListEncoder extends AbstractEncoder{
    
    private final static Logger logger = Logger.getLogger(EmbedListEncoder.class);
    
    public EmbedListEncoder(Object obj, Field field){
        super(obj, field);
    }

    @Override
    public String getFieldName() {
        String fieldName = field.getName();
        EmbedList embedList = field.getAnnotation(EmbedList.class);
        if(embedList != null){
            String name = embedList.name();
            if(!name.equals("")){
                fieldName = name;
            }
        }
        return fieldName;
    }

    @Override
    public Object encode() {
        List list = (List)value;
        List<DBObject> result = new LinkedList<DBObject>();
        for(Object o : list){
            result.add(new BuguMapper().toDBObject(o));
        }
        return result;
    }
    
}