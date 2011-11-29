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
import com.bugull.mongo.mapper.DataType;
import com.bugull.mongo.mapper.FieldUtil;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class PropertyFieldHandler extends AbstractFieldHandler{
        
    public PropertyFieldHandler(Object obj, java.lang.reflect.Field field, String prefix){
        super(obj, field, prefix);
    }

    @Override
    public void handle(Document doc){
        IndexProperty ip = field.getAnnotation(IndexProperty.class);
        process(doc, ip.analyze(), ip.store(), ip.boost());
    }
    
    protected void process(Document doc, boolean analyze, boolean store, float boost) {
        Class<?> type = field.getType();
        if(type.isArray()){
            processArray(doc, analyze, store, boost);
        }else{
            processPrimitive(doc, analyze, store, boost);
        }
    }
    
    private void processArray(Document doc, boolean analyze, boolean store, float boost) {
        Class<?> type = field.getType();
        String typeName = type.getComponentType().getName();
        String fieldName = prefix + field.getName();
        Object objValue = FieldUtil.get(obj, field);
        Field f = new Field(fieldName, getArrayString(objValue, typeName),
                    store ? Field.Store.YES : Field.Store.NO,
                    analyze ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED);
        f.setBoost(boost);
        doc.add(f);
    }
    
    private void processPrimitive(Document doc, boolean analyze, boolean store, float boost) {
        Class<?> type = field.getType();
        Object objValue = FieldUtil.get(obj, field);
        String fieldName = prefix + field.getName();
        String typeName = type.getName();
        Fieldable f = null;
        if(DataType.isString(typeName)){
            f = new Field(fieldName, objValue.toString(),
                    store ? Field.Store.YES : Field.Store.NO,
                    analyze ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED);
        }
        else if(DataType.isBoolean(typeName) || DataType.isBooleanObject(typeName)){
            f = new Field(fieldName, objValue.toString(), Field.Store.NO, Field.Index.NOT_ANALYZED);
        }
        else if(DataType.isChar(typeName) || DataType.isCharObject(typeName)){
            f = new Field(fieldName, objValue.toString(), Field.Store.NO, Field.Index.NOT_ANALYZED);
        }
        else if(DataType.isInteger(typeName) || DataType.isIntegerObject(typeName)){
            int v = Integer.parseInt(objValue.toString());
            f = new NumericField(fieldName).setIntValue(v);
        }
        else if(DataType.isLong(typeName) || DataType.isLongObject(typeName)){
            long v = Long.parseLong(objValue.toString());
            f = new NumericField(fieldName).setLongValue(v);
        }
        else if(DataType.isShort(typeName) || DataType.isShortObject(typeName)){
            short v = Short.parseShort(objValue.toString());
            f = new NumericField(fieldName).setIntValue(v);
        }
        else if(DataType.isFloat(typeName) || DataType.isFloatObject(typeName)){
            float v = Float.parseFloat(objValue.toString());
            f = new NumericField(fieldName).setFloatValue(v);
        }
        else if(DataType.isDouble(typeName) || DataType.isDoubleObject(typeName)){
            double v = Double.parseDouble(objValue.toString());
            f = new NumericField(fieldName).setDoubleValue(v);
        }
        else if(DataType.isDate(typeName)){
            Date date = (Date)objValue;
            f = new NumericField(fieldName).setLongValue(date.getTime());
        }
        else if(DataType.isTimestamp(typeName)){
            Timestamp ts = (Timestamp)objValue;
            f = new NumericField(fieldName).setLongValue(ts.getTime());
        }
        else if(DataType.isSet(typeName) || DataType.isList(typeName)){
            Collection coll = (Collection)objValue;
            StringBuilder sb = new StringBuilder();
            for(Object o : coll){
                sb.append(o).append(JOIN);
            }
            f = new Field(fieldName, sb.toString(),
                    store ? Field.Store.YES : Field.Store.NO,
                    analyze ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED);
        }
        f.setBoost(boost);
        doc.add(f);
    }
    
}