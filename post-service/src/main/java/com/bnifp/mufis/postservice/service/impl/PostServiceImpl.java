package com.bnifp.mufis.postservice.service.impl;

import com.bnifp.mufis.postservice.dto.input.PostInput;
import com.bnifp.mufis.postservice.dto.output.PostOutput;
import com.bnifp.mufis.postservice.dto.output.PostOutputDetail;
import com.bnifp.mufis.postservice.dto.response.BaseResponse;
import com.bnifp.mufis.postservice.model.Post;
import com.bnifp.mufis.postservice.repository.PostRepository;
import com.bnifp.mufis.postservice.service.KafkaProducer;
import com.bnifp.mufis.postservice.service.PostService;
import org.apache.commons.collections4.IterableUtils;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    public void setMapper(ModelMapper mapper){
        this.mapper = mapper;
    }

    //Check if string is null or empty
    private Boolean isNullorEmpty(String str){
        if (str == null || str.isEmpty() || str.trim().isEmpty()) {
            return true;
        }
        return false;
    }

    //function to find a Post with id
    private Post findPost(Long id){
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            return null;
        }
        return post.get();
    }

    @Override
    public ResponseEntity<BaseResponse> addOne(PostInput postInput) {
        if(isNullorEmpty(postInput.getTitle())|| isNullorEmpty(postInput.getContent())){
            String message = "Title and content cannot be null or empty!";
            return new ResponseEntity<BaseResponse>(new BaseResponse<>(Boolean.FALSE, message),
                    HttpStatus.BAD_REQUEST);
        }

        Post post = mapper.map(postInput, Post.class);
        try{
            postRepository.save(post);
        } catch(Exception e){
            String message = e.getMessage();
            return new ResponseEntity<BaseResponse>(new BaseResponse<>(Boolean.FALSE, message),
                    HttpStatus.CONFLICT);
        }
        return new ResponseEntity<BaseResponse>(new BaseResponse<>
                (this.mapper.map(post, PostOutput.class)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BaseResponse> getOne(Long id) {
        Post post = findPost(id);;
        if(Objects.isNull(post)){
            String message = "Post with id: " + id.toString() + " is not Found";
            return new ResponseEntity<BaseResponse>(new BaseResponse<>(Boolean.FALSE, message),
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<BaseResponse>(new BaseResponse<>
                (this.mapper.map(post, PostOutputDetail.class)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BaseResponse> updateOne(Long id, PostInput postInput){
        if(isNullorEmpty(postInput.getTitle())|| isNullorEmpty(postInput.getContent())){
            String message = "Title and content cannot be null or empty!";
            return new ResponseEntity<BaseResponse>(new BaseResponse<>(Boolean.FALSE, message),
                    HttpStatus.BAD_REQUEST);
        }
        Post post = findPost(id);
        if(Objects.isNull(post)){
            String message = "Post with id: " + id.toString() + " is not Found";
            return new ResponseEntity<BaseResponse>(new BaseResponse<>(Boolean.FALSE, message),
                    HttpStatus.NOT_FOUND);
        }

        this.mapper.map(postInput, post);
        postRepository.save(post);
        return new ResponseEntity<BaseResponse>(new BaseResponse<>
                (this.mapper.map(post, PostOutputDetail.class)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BaseResponse> deleteOne(Long id) {
        Post post = findPost(id);
        if(Objects.isNull(post)){
            String message = "Post with id: " + id.toString() + " is not Found";
            return new ResponseEntity<BaseResponse>(new BaseResponse<>(Boolean.FALSE, message),
                    HttpStatus.NOT_FOUND);
        }
        try{
            String psn = new JSONObject()
                    .put("name", "post-service")
                    .put("data", post)
                    .toString();
            kafkaProducer.produce(psn);
            postRepository.deleteById(id);
        }catch (Exception e){
            return new ResponseEntity<BaseResponse>(new BaseResponse<>
                    (Boolean.TRUE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String message = "Successfully Deleted post with id: " + id;
        return new ResponseEntity<BaseResponse>(new BaseResponse<>(Boolean.TRUE, message), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BaseResponse> getAll() {
        Iterable<Post> posts = postRepository.findAll();
        List<Post> postList = IterableUtils.toList(posts);

        List<PostOutputDetail> outputs = new ArrayList<>();
        for(Post post: postList){
            outputs.add(mapper.map(post, PostOutputDetail.class));
        }
        return new ResponseEntity<BaseResponse>(new BaseResponse<>(outputs), HttpStatus.OK);
    }

}