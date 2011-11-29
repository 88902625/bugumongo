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

package com.bugull.mongo.lucene.handler;

import com.bugull.mongo.lucene.annotations.IndexProperty;
import com.bugull.mongo.mapper.FieldUtil;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class ListPropertyFieldHandler extends PropertyFieldHandler{
        
    protected ListPropertyFieldHandler(Object obj, java.lang.reflect.Field field, String prefix){
        super(obj, field, prefix);
    }
    
    @Override
    public void handle(Document doc){
        IndexProperty ip = field.getAnnotation(IndexProperty.class);
        processList((List)obj, doc, ip.analyze(), ip.store(), ip.boost());
    }
    
    protected void processList(List objList, Document doc, boolean analyze, boolean store, float boost){
        StringBuilder sb = new StringBuilder();
        Class<?> type = field.getType();
        if(type.isArray()){
            String typeName = type.getComponentType().getName();
            for(Object o : objList){
                Object value = FieldUtil.get(o, field);
                if(value == null){
                    continue;
                }
                sb.append(getArrayString(value, typeName)).append(JOIN);
            }
        }else{
            for(Object o : objList){
                String value = FieldUtil.get(o, field).toString();
                sb.append(value).append(JOIN);
            }
        }
        Field f = new Field(prefix + field.getName(), sb.toString(), 
                store ? Field.Store.YES : Field.Store.NO,
                analyze ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED);
        f.setBoost(boost);
        doc.add(f);
    }
    
}