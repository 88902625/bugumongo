package com.bugull.mongo.encoder;

import com.bugull.mongo.annotations.Embed;
import com.bugull.mongo.annotations.Id;
import com.bugull.mongo.annotations.Property;
import com.bugull.mongo.annotations.Ref;
import com.bugull.mongo.annotations.RefList;
import java.lang.reflect.Field;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class EncoderFactory {
    
    public static Encoder createEncoder(Object obj, Field field){
        Encoder encoder = null;
        if(field.getAnnotations().length == 0 || field.getAnnotation(Property.class) != null){
            encoder = new PropertyEncoder(obj, field);
        }
        else if(field.getAnnotation(Id.class) != null){
            encoder = new IdEncoder(obj, field);
        }
        else if(field.getAnnotation(Embed.class) != null){
            encoder = new EmbedEncoder(obj, field);
        }
        else if(field.getAnnotation(Ref.class) != null){
            encoder = new RefEncoder(obj, field);
        }
        else if(field.getAnnotation(RefList.class) != null){
            encoder = new RefListEncoder(obj, field);
        }
        return encoder;
    }
    
}
