package com.capture.files;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GridFSConfig {

    @Value("${spring.data.mongodb.database}")
    private String mongodb;

    @Bean
    public GridFSBucket gridFSBucket(MongoClient mongoClient) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(mongodb);
        GridFSBucket bucket = GridFSBuckets.create(mongoDatabase);
        return bucket;
    }

}
