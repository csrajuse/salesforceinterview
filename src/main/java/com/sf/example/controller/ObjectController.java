package com.sf.example.controller;


import com.sf.example.repository.AnyObjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/objects/")
public class ObjectController {

    private static final Logger log = LoggerFactory.getLogger("ObjectController");

    @Autowired
    private AnyObjectRepository anyObjectRepository;

    @GetMapping(path = "/{objectName}")
    public ResponseEntity<?> listObjects(@PathVariable String objectName){
        log.info("Listing "+objectName);
        try {
            List<Map<String, String>> objectDataListMap = anyObjectRepository.fetchAllObjects(objectName);
            return ResponseEntity.ok(objectDataListMap);
        }catch(Exception ex){
            log.error("Error listing object",ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/{objectName}/{id}")
    public ResponseEntity<?> listObjects(@PathVariable String objectName,
                                          @PathVariable String id){
        log.info("Listing "+objectName);
        try {
            Map<String, String> objectDataMap = anyObjectRepository.fetchObject(objectName,id);
            return ResponseEntity.ok(objectDataMap);
        }catch(Exception ex){
            log.error("Error listing object",ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/{objectName}")
    public ResponseEntity<?> newObject(@RequestBody Map<String,String> objectData,
                                       @PathVariable String objectName){
        log.info("Saving any object");
        try{
            anyObjectRepository.save(objectName,objectData);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception ex){
            log.error("Error saving object",ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/{objectName}/{id}")
    public ResponseEntity<?> upsertObject(@RequestBody Map<String,String> objectData,
                                       @PathVariable String objectName,
                                       @PathVariable String id){
        log.info("Saving any object");
        try{
            objectData.put("Id",id);
            anyObjectRepository.save(objectName,objectData);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception ex){
            log.error("Error saving object",ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/{objectName}/{id}")
    public ResponseEntity<?> deleteObject(@RequestBody Map<String,String> objectData,
                                       @PathVariable String objectName,
                                       @PathVariable String id){
        log.info("Deleting any object");
        try{
            objectData.put("Id",id);
            objectData.put("IsDeleted","true");
            anyObjectRepository.save(objectName,objectData);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception ex){
            log.error("Error saving object",ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
