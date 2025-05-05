package com.library.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FileStorageService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations operations;

    public String storeFile(MultipartFile file, String metadata) throws IOException {
        DBObject metaData = new BasicDBObject();
        metaData.put("uploadedBy", "admin");
        metaData.put("description", metadata);
        
        ObjectId id = gridFsTemplate.store(
            file.getInputStream(), 
            file.getOriginalFilename(), 
            file.getContentType(), 
            metaData
        );
        
        return id.toString();
    }

    public InputStream getFile(String id) throws IllegalStateException, IOException {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        if (file == null) {
            return null;
        }
        return operations.getResource(file).getInputStream();
    }

    public void deleteFile(String id) {
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(id)));
    }
}